package cn.iocoder.yudao.module.mes.service.pro.process;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.process.vo.MesProProcessPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.process.vo.MesProProcessSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.process.MesProProcessDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.process.MesProProcessMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.route.MesProRouteProcessMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 生产工序 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesProProcessServiceImpl implements MesProProcessService {

    @Resource
    private MesProProcessMapper processMapper;

    @Resource
    private MesProProcessContentService processContentService;

    @Resource
    private MesProRouteProcessMapper routeProcessMapper;

    @Override
    public Long createProcess(MesProProcessSaveReqVO createReqVO) {
        // 1.1 校验工序编码唯一性
        validateProcessCodeUnique(null, createReqVO.getCode());
        // 1.2 校验工序名称唯一性
        validateProcessNameUnique(null, createReqVO.getName());

        // 2. 插入工序
        MesProProcessDO process = BeanUtils.toBean(createReqVO, MesProProcessDO.class);
        processMapper.insert(process);
        return process.getId();
    }

    @Override
    public void updateProcess(MesProProcessSaveReqVO updateReqVO) {
        // 1.1 校验存在
        validateProcessExists(updateReqVO.getId());
        // 1.2 校验工序编码唯一性
        validateProcessCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 1.3 校验工序名称唯一性
        validateProcessNameUnique(updateReqVO.getId(), updateReqVO.getName());

        // 2. 更新工序
        MesProProcessDO updateObj = BeanUtils.toBean(updateReqVO, MesProProcessDO.class);
        processMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProcess(Long id) {
        // 1.1 校验存在
        validateProcessExists(id);
        // 1.2 校验是否被工艺路线引用
        // TODO @AI：CollUtil.isNotEmpty() 可能更好
        if (!routeProcessMapper.selectListByProcessId(id).isEmpty()) {
            throw exception(PRO_PROCESS_USED_BY_ROUTE);
        }

        // 2. 删除工序
        processMapper.deleteById(id);
        // 3. 级联删除工序内容
        processContentService.deleteProcessContentByProcessId(id);
    }

    @Override
    public void validateProcessExists(Long id) {
        if (processMapper.selectById(id) == null) {
            throw exception(PRO_PROCESS_NOT_EXISTS);
        }
    }

    private void validateProcessCodeUnique(Long id, String code) {
        MesProProcessDO process = processMapper.selectByCode(code);
        if (process == null) {
            return;
        }
        if (id == null) {
            throw exception(PRO_PROCESS_CODE_EXISTS);
        }
        if (!process.getId().equals(id)) {
            throw exception(PRO_PROCESS_CODE_EXISTS);
        }
    }

    private void validateProcessNameUnique(Long id, String name) {
        MesProProcessDO process = processMapper.selectByName(name);
        if (process == null) {
            return;
        }
        if (id == null) {
            throw exception(PRO_PROCESS_NAME_EXISTS);
        }
        if (!process.getId().equals(id)) {
            throw exception(PRO_PROCESS_NAME_EXISTS);
        }
    }

    @Override
    public MesProProcessDO getProcess(Long id) {
        return processMapper.selectById(id);
    }

    @Override
    public List<MesProProcessDO> getProcessList(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return processMapper.selectByIds(ids);
    }

    @Override
    public PageResult<MesProProcessDO> getProcessPage(MesProProcessPageReqVO pageReqVO) {
        return processMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesProProcessDO> getProcessListByStatus(Integer status) {
        return processMapper.selectListByStatus(status);
    }

}
