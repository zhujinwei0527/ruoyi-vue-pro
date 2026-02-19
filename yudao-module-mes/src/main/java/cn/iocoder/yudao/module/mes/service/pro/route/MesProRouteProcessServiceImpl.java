package cn.iocoder.yudao.module.mes.service.pro.route;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.route.vo.process.MesProRouteProcessSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.MesProProcessDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProcessDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.MesProRouteProcessMapper;
import cn.iocoder.yudao.module.mes.service.pro.MesProProcessService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Comparator;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

// TODO @AI：参考别的模块，需要校验下相关的工艺路线、工序是否存在；（别的 route 也是）；最好对方 service 提供校验方法；
/**
 * MES 工艺路线工序 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesProRouteProcessServiceImpl implements MesProRouteProcessService {

    @Resource
    private MesProRouteProcessMapper routeProcessMapper;

    // TODO @AI：貌似没用到？是不是应该要用下哈？
    @Resource
    @Lazy
    private MesProProcessService processService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRouteProcess(MesProRouteProcessSaveReqVO createReqVO) {
        // 1. 校验
        validateRouteProcessCreate(createReqVO);

        // 2. 插入
        MesProRouteProcessDO routeProcess = BeanUtils.toBean(createReqVO, MesProRouteProcessDO.class);
        routeProcessMapper.insert(routeProcess);

        // 3. 更新工序链
        rebuildProcessChain(createReqVO.getRouteId());
        return routeProcess.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRouteProcess(MesProRouteProcessSaveReqVO updateReqVO) {
        // 1.1 校验存在
        validateRouteProcessExists(updateReqVO.getId());
        // 1.2 校验唯一性
        validateRouteProcessUpdate(updateReqVO);

        // 2. 更新
        MesProRouteProcessDO updateObj = BeanUtils.toBean(updateReqVO, MesProRouteProcessDO.class);
        routeProcessMapper.updateById(updateObj);

        // 3. 重建工序链
        rebuildProcessChain(updateReqVO.getRouteId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRouteProcess(Long id) {
        // 1. 校验存在
        MesProRouteProcessDO routeProcess = routeProcessMapper.selectById(id);
        if (routeProcess == null) {
            throw exception(PRO_ROUTE_PROCESS_NOT_EXISTS);
        }

        // 2. 删除
        routeProcessMapper.deleteById(id);

        // 3. 重建工序链
        rebuildProcessChain(routeProcess.getRouteId());
    }

    /**
     * 重建工序链（更新所有工序的 nextProcessId）
     */
    private void rebuildProcessChain(Long routeId) {
        List<MesProRouteProcessDO> list = routeProcessMapper.selectListByRouteId(routeId);
        // TODO @AI：CollUtil isEmpty；
        if (list.isEmpty()) {
            return;
        }
        // 按 sort 排序
        list.sort(Comparator.comparing(MesProRouteProcessDO::getSort));
        for (int i = 0; i < list.size(); i++) {
            MesProRouteProcessDO item = list.get(i);
            if (i + 1 < list.size()) {
                item.setNextProcessId(list.get(i + 1).getProcessId());
            } else {
                item.setNextProcessId(null);
            }
            routeProcessMapper.updateById(item);
        }
    }

    private void validateRouteProcessExists(Long id) {
        if (routeProcessMapper.selectById(id) == null) {
            throw exception(PRO_ROUTE_PROCESS_NOT_EXISTS);
        }
    }

    // TODO @AI：参考别的模块，拆分为多个模块
    private void validateRouteProcessCreate(MesProRouteProcessSaveReqVO reqVO) {
        // 序号唯一
        MesProRouteProcessDO existing = routeProcessMapper.selectByRouteIdAndSort(reqVO.getRouteId(), reqVO.getSort());
        if (existing != null) {
            throw exception(PRO_ROUTE_PROCESS_SORT_DUPLICATE);
        }
        // 工序不重复
        existing = routeProcessMapper.selectByRouteIdAndProcessId(reqVO.getRouteId(), reqVO.getProcessId());
        if (existing != null) {
            throw exception(PRO_ROUTE_PROCESS_DUPLICATE);
        }
        // 关键工序唯一
        if (Boolean.TRUE.equals(reqVO.getKeyFlag())) {
            existing = routeProcessMapper.selectKeyProcessByRouteId(reqVO.getRouteId());
            if (existing != null) {
                throw exception(PRO_ROUTE_PROCESS_KEY_DUPLICATE);
            }
        }
    }

    // TODO @AI：参考别的模块，拆分为多个模块
    private void validateRouteProcessUpdate(MesProRouteProcessSaveReqVO reqVO) {
        // 序号唯一（排除自身）
        MesProRouteProcessDO existing = routeProcessMapper.selectByRouteIdAndSort(reqVO.getRouteId(), reqVO.getSort());
        if (existing != null && !existing.getId().equals(reqVO.getId())) {
            throw exception(PRO_ROUTE_PROCESS_SORT_DUPLICATE);
        }
        // 工序不重复（排除自身）
        existing = routeProcessMapper.selectByRouteIdAndProcessId(reqVO.getRouteId(), reqVO.getProcessId());
        if (existing != null && !existing.getId().equals(reqVO.getId())) {
            throw exception(PRO_ROUTE_PROCESS_DUPLICATE);
        }
        // 关键工序唯一（排除自身）
        if (Boolean.TRUE.equals(reqVO.getKeyFlag())) {
            existing = routeProcessMapper.selectKeyProcessByRouteId(reqVO.getRouteId());
            if (existing != null && !existing.getId().equals(reqVO.getId())) {
                throw exception(PRO_ROUTE_PROCESS_KEY_DUPLICATE);
            }
        }
    }

    @Override
    public MesProRouteProcessDO getRouteProcess(Long id) {
        return routeProcessMapper.selectById(id);
    }

    @Override
    public List<MesProRouteProcessDO> getRouteProcessListByRouteId(Long routeId) {
        return routeProcessMapper.selectListByRouteId(routeId);
    }

}
