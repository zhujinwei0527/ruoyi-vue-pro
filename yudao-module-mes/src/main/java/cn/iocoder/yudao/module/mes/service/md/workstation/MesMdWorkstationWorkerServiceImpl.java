package cn.iocoder.yudao.module.mes.service.md.workstation;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.worker.MesMdWorkstationWorkerSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationWorkerDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkstationWorkerMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 工位人员 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesMdWorkstationWorkerServiceImpl implements MesMdWorkstationWorkerService {

    @Resource
    private MesMdWorkstationWorkerMapper workstationWorkerMapper;

    @Override
    public Long createWorkstationWorker(MesMdWorkstationWorkerSaveReqVO createReqVO) {
        // 校验同一工位下岗位不重复
        MesMdWorkstationWorkerDO existing = workstationWorkerMapper.selectByWorkstationIdAndPostId(
                createReqVO.getWorkstationId(), createReqVO.getPostId());
        if (existing != null) {
            throw exception(MD_WORKSTATION_WORKER_POST_EXISTS);
        }

        // 插入
        MesMdWorkstationWorkerDO worker = BeanUtils.toBean(createReqVO, MesMdWorkstationWorkerDO.class);
        workstationWorkerMapper.insert(worker);
        return worker.getId();
    }

    @Override
    public void updateWorkstationWorker(MesMdWorkstationWorkerSaveReqVO updateReqVO) {
        // TODO @AI：抽个方法，参考别的模块
        if (workstationWorkerMapper.selectById(updateReqVO.getId()) == null) {
            throw exception(MD_WORKSTATION_WORKER_NOT_EXISTS);
        }

        // 更新
        MesMdWorkstationWorkerDO updateObj = BeanUtils.toBean(updateReqVO, MesMdWorkstationWorkerDO.class);
        workstationWorkerMapper.updateById(updateObj);
    }

    @Override
    public void deleteWorkstationWorker(Long id) {
        // TODO @AI：抽个方法，参考别的模块
        if (workstationWorkerMapper.selectById(id) == null) {
            throw exception(MD_WORKSTATION_WORKER_NOT_EXISTS);
        }

        // 删除
        workstationWorkerMapper.deleteById(id);
    }

    @Override
    public List<MesMdWorkstationWorkerDO> getWorkstationWorkerListByWorkstationId(Long workstationId) {
        return workstationWorkerMapper.selectListByWorkstationId(workstationId);
    }

}
