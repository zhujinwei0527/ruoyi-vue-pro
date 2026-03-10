package cn.iocoder.yudao.module.mes.service.wm.stocktaking.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialstock.vo.MesWmMaterialStockFreezeReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.MesWmStockTakingTaskPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.MesWmStockTakingTaskSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskLineDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskResultDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.task.MesWmStockTakingTaskLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.task.MesWmStockTakingTaskMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.task.MesWmStockTakingTaskResultMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmStockTakingTaskLineStatusEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmStockTakingTaskStatusEnum;
import cn.iocoder.yudao.module.mes.service.wm.materialstock.MesWmMaterialStockService;
import cn.iocoder.yudao.module.mes.service.wm.stocktaking.plan.MesWmStockTakingPlanService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 盘点任务 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesWmStockTakingTaskServiceImpl implements MesWmStockTakingTaskService {

    @Resource
    private MesWmStockTakingTaskMapper stockTakingTaskMapper;
    // TODO @芋艿：看看怎么清理掉，这块的依赖；应该调用对方的 service
    @Resource
    private MesWmStockTakingTaskLineMapper stockTakingTaskLineMapper;
    // TODO @芋艿：看看怎么清理掉，这块的依赖；应该调用对方的 service
    @Resource
    private MesWmStockTakingTaskResultMapper stockTakingTaskResultMapper;

    @Resource
    private MesWmMaterialStockService materialStockService;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private MesWmStockTakingPlanService stockTakingPlanService;
    @Resource
    private MesWmStockTakingTaskLineService stockTakingTaskLineService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStockTakingTask(MesWmStockTakingTaskSaveReqVO createReqVO) {
        // 1.1 校验 code 唯一
        validateTaskCodeUnique(null, createReqVO.getCode());
        // 1.2 校验方案存在
        adminUserApi.validateUser(createReqVO.getUserId());
        // 1.3 校验方案可用
        // TODO @芋艿：貌似可以 planId 不填写；无 plan 就手动维护；
        stockTakingPlanService.validateStockTakingPlanEnabled(createReqVO.getPlanId());

        // 2. 插入任务
        MesWmStockTakingTaskDO task = BeanUtils.toBean(createReqVO, MesWmStockTakingTaskDO.class)
                .setStatus(MesWmStockTakingTaskStatusEnum.PREPARE.getStatus());
        stockTakingTaskMapper.insert(task);

        // 3. 根据方案生成盘点明细行
        // TODO @AI：有 plan 的情况下，才类似操作；
        stockTakingTaskLineService.generateStockTakingLines(task);
        return task.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockTakingTask(MesWmStockTakingTaskSaveReqVO updateReqVO) {
        // 1.1 校验任务存在且为草稿状态
        validateTaskExistsAndPrepare(updateReqVO.getId());
        // 1.2 校验 code 唯一
        validateTaskCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 1.3 校验方案存在
        adminUserApi.validateUser(updateReqVO.getUserId());
        // 1.4 校验方案可用
        // TODO @芋艿：貌似可以 planId 不填写；无 plan 就手动维护；
        stockTakingPlanService.validateStockTakingPlanEnabled(updateReqVO.getPlanId());

        // 2. 更新任务
        MesWmStockTakingTaskDO updateObj = BeanUtils.toBean(updateReqVO, MesWmStockTakingTaskDO.class);
        stockTakingTaskMapper.updateById(updateObj);

        // 3. 删除老的明细，重新生成新的
        // TODO @AI：有 plan 的情况下，才类似操作；
        stockTakingTaskLineMapper.deleteByTaskId(updateReqVO.getId());
        stockTakingTaskLineService.generateStockTakingLines(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStockTakingTask(Long id) {
        // 1. 校验任务存在且为草稿状态
        validateTaskExistsAndPrepare(id);

        // 2. 删除任务和明细
        stockTakingTaskResultMapper.deleteByTaskId(id);
        stockTakingTaskLineMapper.deleteByTaskId(id);
        stockTakingTaskMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitStockTakingTask(Long id) {
        // 1.1 校验任务存在且为草稿状态
        MesWmStockTakingTaskDO task = validateTaskExistsAndPrepare(id);
        // 1.2 检查要盘点的内容
        List<MesWmStockTakingTaskLineDO> lines = stockTakingTaskLineMapper.selectListByTaskId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_STOCK_TAKING_TASK_NO_LINE);
        }

        // 2. 更新任务状态为审批中
        stockTakingTaskMapper.updateById(new MesWmStockTakingTaskDO().setId(id)
                .setStatus(MesWmStockTakingTaskStatusEnum.APPROVING.getStatus()));

        // 3. 根据冻结标识，对物资进行冻结
        // TODO @芋艿：后续跟进下！
        if (Boolean.TRUE.equals(task.getFrozenFlag())) {
            for (MesWmStockTakingTaskLineDO line : lines) {
                if (line.getMaterialStockId() != null) {
                    updateFrozen(line.getMaterialStockId(), true);
                }
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishStockTakingTask(Long id) {
        // TODO @AI：补全下注释；
        // TODO @AI：notEquals；
        MesWmStockTakingTaskDO task = validateTaskExists(id);
        if (!MesWmStockTakingTaskStatusEnum.APPROVING.getStatus().equals(task.getStatus())) {
            throw exception(WM_STOCK_TAKING_TASK_NOT_APPROVING);
        }
        List<MesWmStockTakingTaskLineDO> lines = stockTakingTaskLineMapper.selectListByTaskId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_STOCK_TAKING_TASK_NO_LINE);
        }

        stockTakingTaskResultMapper.deleteByTaskId(id);
        for (MesWmStockTakingTaskLineDO line : lines) {
            if (MesWmStockTakingTaskLineStatusEnum.NORMAL.getStatus().equals(line.getStatus())) {
                continue;
            }
            stockTakingTaskResultMapper.insert(MesWmStockTakingTaskResultDO.builder()
                    .taskId(id)
                    .lineId(line.getId())
                    .materialStockId(line.getMaterialStockId())
                    .itemId(line.getItemId())
                    .batchId(line.getBatchId())
                    .batchCode(line.getBatchCode())
                    .warehouseId(line.getWarehouseId())
                    .locationId(line.getLocationId())
                    .areaId(line.getAreaId())
                    .quantity(differenceQuantity(line))
                    .remark(line.getRemark())
                    .build());
        }

        task.setEndTime(LocalDateTime.now());
        task.setStatus(MesWmStockTakingTaskStatusEnum.FINISHED.getStatus());
        stockTakingTaskMapper.updateById(task);
        if (Boolean.TRUE.equals(task.getFrozenFlag())) {
            unfreezeTaskStocks(lines);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelStockTakingTask(Long id) {
        MesWmStockTakingTaskDO task = validateTaskExists(id);
        if (MesWmStockTakingTaskStatusEnum.FINISHED.getStatus().equals(task.getStatus())
                || MesWmStockTakingTaskStatusEnum.CANCELED.getStatus().equals(task.getStatus())) {
            throw exception(WM_STOCK_TAKING_TASK_CANNOT_CANCEL);
        }
        task.setStatus(MesWmStockTakingTaskStatusEnum.CANCELED.getStatus());
        stockTakingTaskMapper.updateById(task);
        if (Boolean.TRUE.equals(task.getFrozenFlag())) {
            unfreezeTaskStocks(stockTakingTaskLineMapper.selectListByTaskId(id));
        }
    }

    @Override
    public MesWmStockTakingTaskDO getStockTakingTask(Long id) {
        return stockTakingTaskMapper.selectById(id);
    }

    @Override
    public MesWmStockTakingTaskDO validateStockTakingTaskExists(Long id) {
        return validateTaskExists(id);
    }

    @Override
    public PageResult<MesWmStockTakingTaskDO> getStockTakingTaskPage(MesWmStockTakingTaskPageReqVO pageReqVO) {
        return stockTakingTaskMapper.selectPage(pageReqVO);
    }

    private void updateFrozen(Long materialStockId, boolean frozen) {
        MesWmMaterialStockFreezeReqVO reqVO = new MesWmMaterialStockFreezeReqVO();
        reqVO.setId(materialStockId);
        reqVO.setFrozen(frozen);
        materialStockService.updateMaterialStockFrozen(reqVO);
    }

    private void unfreezeTaskStocks(List<MesWmStockTakingTaskLineDO> lines) {
        if (CollUtil.isEmpty(lines)) {
            return;
        }
        for (MesWmStockTakingTaskLineDO line : lines) {
            if (line.getMaterialStockId() != null) {
                updateFrozen(line.getMaterialStockId(), false);
            }
        }
    }

    private Integer resolveLineStatus(BigDecimal quantity, BigDecimal takingQuantity) {
        int compare = takingQuantity.compareTo(defaultQuantity(quantity));
        if (compare == 0) {
            return MesWmStockTakingTaskLineStatusEnum.NORMAL.getStatus();
        }
        return compare > 0 ? MesWmStockTakingTaskLineStatusEnum.GAIN.getStatus()
                : MesWmStockTakingTaskLineStatusEnum.LOSS.getStatus();
    }

    private BigDecimal differenceQuantity(MesWmStockTakingTaskLineDO line) {
        return defaultQuantity(line.getTakingQuantity()).subtract(defaultQuantity(line.getQuantity()));
    }

    private BigDecimal defaultQuantity(BigDecimal quantity) {
        return quantity == null ? BigDecimal.ZERO : quantity;
    }

    private void validateTaskCodeUnique(Long id, String code) {
        MesWmStockTakingTaskDO task = stockTakingTaskMapper.selectByCode(code);
        if (task == null) {
            return;
        }
        if (id == null || !Objects.equals(task.getId(), id)) {
            throw exception(WM_STOCK_TAKING_TASK_CODE_DUPLICATE);
        }
    }

    private MesWmStockTakingTaskDO validateTaskExists(Long id) {
        MesWmStockTakingTaskDO task = stockTakingTaskMapper.selectById(id);
        if (task == null) {
            throw exception(WM_STOCK_TAKING_TASK_NOT_EXISTS);
        }
        return task;
    }

    private MesWmStockTakingTaskDO validateTaskExistsAndPrepare(Long id) {
        MesWmStockTakingTaskDO task = validateTaskExists(id);
        if (ObjUtil.notEqual(MesWmStockTakingTaskStatusEnum.PREPARE.getStatus(), task.getStatus())) {
            throw exception(WM_STOCK_TAKING_TASK_NOT_PREPARE);
        }
        return task;
    }

}
