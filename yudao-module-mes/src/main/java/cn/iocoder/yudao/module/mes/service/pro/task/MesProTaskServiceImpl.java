package cn.iocoder.yudao.module.mes.service.pro.task;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.task.vo.MesProTaskPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.task.vo.MesProTaskSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.task.MesProTaskDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.task.MesProTaskMapper;
import cn.iocoder.yudao.module.mes.enums.md.autocode.MesMdAutoCodeRuleCodeEnum;
import cn.iocoder.yudao.module.mes.enums.pro.MesProTaskStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.autocode.MesMdAutoCodeRecordService;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.process.MesProProcessService;
import cn.iocoder.yudao.module.mes.service.pro.route.MesProRouteService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.PRO_TASK_NOT_EXISTS;

/**
 * MES 生产任务 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesProTaskServiceImpl implements MesProTaskService {

    @Resource
    private MesProTaskMapper taskMapper;

    @Resource
    @Lazy
    private MesProWorkOrderService workOrderService;
    @Resource
    private MesMdWorkstationService workstationService;
    @Resource
    private MesProProcessService processService;
    @Resource
    private MesMdItemService itemService;
    @Resource
    private MesProRouteService routeService;
    @Resource
    private MesMdAutoCodeRecordService autoCodeRecordService;

    @Override
    public Long createTask(MesProTaskSaveReqVO createReqVO) {
        // 1. 校验关联数据存在
        workOrderService.validateWorkOrderExists(createReqVO.getWorkOrderId());
        workstationService.validateWorkstationExists(createReqVO.getWorkstationId());
        routeService.validateRouteExists(createReqVO.getRouteId());
        processService.validateProcessExists(createReqVO.getProcessId());
        MesMdItemDO item = itemService.validateItemExists(createReqVO.getItemId());

        // 2.1 构建任务 DO
        MesProTaskDO task = BeanUtils.toBean(createReqVO, MesProTaskDO.class)
                .setName(item.getName() + "【" + createReqVO.getQuantity() + "】")
                .setCode(autoCodeRecordService.generateAutoCode(MesMdAutoCodeRuleCodeEnum.TASK_CODE.getCode()))
                .setStatus(MesProTaskStatusEnum.PREPARE.getStatus());
        // 2.2 插入
        taskMapper.insert(task);
        return task.getId();
    }

    @Override
    public void updateTask(MesProTaskSaveReqVO updateReqVO) {
        // 1.1 校验存在
        validateTaskExists(updateReqVO.getId());
        // 1.2 校验关联数据存在
        workOrderService.validateWorkOrderExists(updateReqVO.getWorkOrderId());
        workstationService.validateWorkstationExists(updateReqVO.getWorkstationId());
        routeService.validateRouteExists(updateReqVO.getRouteId());
        processService.validateProcessExists(updateReqVO.getProcessId());
        MesMdItemDO item = itemService.validateItemExists(updateReqVO.getItemId());

        // 2. 更新
        MesProTaskDO updateObj = BeanUtils.toBean(updateReqVO, MesProTaskDO.class)
                .setName(item.getName() + "【" + updateReqVO.getQuantity() + "】");
        taskMapper.updateById(updateObj);
    }

    @Override
    public void deleteTask(Long id) {
        // 1. 校验存在
        validateTaskExists(id);
        // 2. 删除
        taskMapper.deleteById(id);
    }

    @Override
    public MesProTaskDO getTask(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public PageResult<MesProTaskDO> getTaskPage(MesProTaskPageReqVO pageReqVO) {
        return taskMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesProTaskDO> getTaskSimpleList(Long workOrderId) {
        if (workOrderId != null) {
            return taskMapper.selectListByWorkOrderId(workOrderId);
        }
        return taskMapper.selectList();
    }

    @Override
    public void validateTaskExists(Long id) {
        if (taskMapper.selectById(id) == null) {
            throw exception(PRO_TASK_NOT_EXISTS);
        }
    }

    @Override
    public List<MesProTaskDO> getTaskList(Collection<Long> ids) {
        return taskMapper.selectByIds(ids);
    }

    @Override
    public void finishTaskByOrderId(Long workOrderId) {
        // 1. 读取工单下所有任务，过滤掉已完成/已取消的
        List<MesProTaskDO> tasks = taskMapper.selectListByWorkOrderId(workOrderId);
        LocalDateTime now = LocalDateTime.now();
        // 2. 构建待更新列表，批量更新
        List<MesProTaskDO> updateList = convertList(tasks,
                task -> new MesProTaskDO().setId(task.getId())
                        .setStatus(MesProTaskStatusEnum.FINISHED.getStatus()).setFinishDate(now),
                task -> !MesProTaskStatusEnum.isEndStatus(task.getStatus()));
        if (CollUtil.isNotEmpty(updateList)) {
            taskMapper.updateBatch(updateList);
        }
    }

    @Override
    public void cancelTaskByOrderId(Long workOrderId) {
        // 1. 读取工单下所有任务，过滤掉已完成/已取消的
        List<MesProTaskDO> tasks = taskMapper.selectListByWorkOrderId(workOrderId);
        LocalDateTime now = LocalDateTime.now();
        // 2. 构建待更新列表，批量更新
        List<MesProTaskDO> updateList = convertList(tasks,
                task -> new MesProTaskDO().setId(task.getId())
                        .setStatus(MesProTaskStatusEnum.CANCELED.getStatus()).setCancelDate(now),
                task -> !MesProTaskStatusEnum.isEndStatus(task.getStatus()));
        if (CollUtil.isNotEmpty(updateList)) {
            taskMapper.updateBatch(updateList);
        }
    }

}

