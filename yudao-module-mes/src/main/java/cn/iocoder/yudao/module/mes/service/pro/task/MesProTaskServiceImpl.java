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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.PRO_TASK_ALREADY_FINISHED;
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
                .setName(buildTaskName(item.getName(), createReqVO.getQuantity()))
                .setCode(autoCodeRecordService.generateAutoCode(MesMdAutoCodeRuleCodeEnum.TASK_CODE.getCode()))
                .setStatus(MesProTaskStatusEnum.PREPARE.getStatus());
        // 2.2 插入
        taskMapper.insert(task);
        return task.getId();
    }

    @Override
    public void updateTask(MesProTaskSaveReqVO updateReqVO) {
        // 1.1 校验存在
        MesProTaskDO task = validateTaskExists(updateReqVO.getId());
        // 1.2 校验关联数据存在
        if (updateReqVO.getWorkOrderId() != null) {
            workOrderService.validateWorkOrderExists(updateReqVO.getWorkOrderId());
        }
        if (updateReqVO.getWorkstationId() != null) {
            workstationService.validateWorkstationExists(updateReqVO.getWorkstationId());
        }
        if (updateReqVO.getRouteId() != null) {
            routeService.validateRouteExists(updateReqVO.getRouteId());
        }
        if (updateReqVO.getProcessId() != null) {
            processService.validateProcessExists(updateReqVO.getProcessId());
        }
        MesMdItemDO item;
        if (updateReqVO.getItemId() != null) {
            item = itemService.validateItemExists(updateReqVO.getItemId());
        } else {
            item = itemService.validateItemExists(task.getItemId());
        }
        BigDecimal quantity = updateReqVO.getQuantity() != null ? updateReqVO.getQuantity() : task.getQuantity();

        // 2. 更新
        MesProTaskDO updateObj = BeanUtils.toBean(updateReqVO, MesProTaskDO.class)
                .setName(buildTaskName(item.getName(), quantity));
        taskMapper.updateById(updateObj);
    }

    private String buildTaskName(String itemName, BigDecimal quantity) {
        return itemName + "【" + quantity + "】";
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
    public List<MesProTaskDO> getTaskListByWorkOrderId(Long workOrderId) {
        return taskMapper.selectListByWorkOrderId(workOrderId);
    }

    @Override
    public List<MesProTaskDO> getTaskListByWorkOrderIds(Collection<Long> workOrderIds) {
        if (CollUtil.isEmpty(workOrderIds)) {
            return Collections.emptyList();
        }
        return taskMapper.selectListByWorkOrderIds(workOrderIds);
    }

    @Override
    public MesProTaskDO validateTaskExists(Long id) {
        MesProTaskDO task = taskMapper.selectById(id);
        if (task == null) {
            throw exception(PRO_TASK_NOT_EXISTS);
        }
        return task;
    }

    @Override
    public MesProTaskDO validateTaskNotFinished(Long id) {
        MesProTaskDO task = validateTaskExists(id);
        if (MesProTaskStatusEnum.isEndStatus(task.getStatus())) {
            throw exception(PRO_TASK_ALREADY_FINISHED);
        }
        return task;
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
