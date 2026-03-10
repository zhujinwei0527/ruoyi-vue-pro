package cn.iocoder.yudao.module.mes.service.wm.stocktaking.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialstock.vo.MesWmMaterialStockFreezeReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialstock.vo.MesWmMaterialStockListReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.MesWmStockTakingTaskPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.MesWmStockTakingTaskSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.line.MesWmStockTakingTaskLineBatchUpdateReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialstock.MesWmMaterialStockDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.plan.MesWmStockTakingPlanParamDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskLineDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskResultDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.plan.MesWmStockTakingPlanMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.plan.MesWmStockTakingPlanParamMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.task.MesWmStockTakingTaskLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.task.MesWmStockTakingTaskMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.task.MesWmStockTakingTaskResultMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmStockTakingPlanParamTypeEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmStockTakingTaskLineStatusEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmStockTakingTaskStatusEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmStockTakingTypeEnum;
import cn.iocoder.yudao.module.mes.service.wm.materialstock.MesWmMaterialStockService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
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
    @Resource
    private MesWmStockTakingTaskLineMapper stockTakingTaskLineMapper;
    @Resource
    private MesWmStockTakingTaskResultMapper stockTakingTaskResultMapper;
    @Resource
    private MesWmStockTakingPlanParamMapper stockTakingPlanParamMapper;
    @Resource
    private MesWmStockTakingPlanMapper stockTakingPlanMapper;
    @Resource
    private MesWmMaterialStockService materialStockService;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private cn.iocoder.yudao.module.mes.service.wm.stocktaking.plan.MesWmStockTakingPlanService stockTakingPlanService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStockTakingTask(MesWmStockTakingTaskSaveReqVO createReqVO) {
        // 1.1 校验 code 唯一
        validateTaskCodeUnique(null, createReqVO.getCode());
        // 1.2 校验方案存在
        adminUserApi.validateUser(createReqVO.getUserId());
        // 1.3 校验方案可用
        stockTakingPlanService.validateStockTakingPlanEnabled(createReqVO.getPlanId());

        // 2. 插入任务
        MesWmStockTakingTaskDO task = BeanUtils.toBean(createReqVO, MesWmStockTakingTaskDO.class)
                .setStatus(MesWmStockTakingTaskStatusEnum.PREPARE.getStatus());
        stockTakingTaskMapper.insert(task);

        // 3. 根据方案生成盘点明细行
        generateStockTakingLines(task);
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
        stockTakingPlanService.validateStockTakingPlanEnabled(updateReqVO.getPlanId());

        // 2. 更新任务
        MesWmStockTakingTaskDO updateObj = BeanUtils.toBean(updateReqVO, MesWmStockTakingTaskDO.class);
        stockTakingTaskMapper.updateById(updateObj);

        // 3. 删除老的明细，重新生成新的
        stockTakingTaskLineMapper.deleteByTaskId(updateReqVO.getId());
        generateStockTakingLines(updateObj);
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

    /**
     * 根据盘点方案生成盘点明细行
     *
     * @param task 盘点任务
     */
    private void generateStockTakingLines(MesWmStockTakingTaskDO task) {
        // 1.1 构建查询条件（包含动态盘点时间校验）
        MesWmMaterialStockListReqVO reqVO = buildStockQueryReqVO(task);
        // 1.2 查询物料库存
        List<MesWmMaterialStockDO> stocks = materialStockService.getMaterialStockList(reqVO);
        if (CollUtil.isEmpty(stocks)) {
            throw exception(WM_STOCK_TAKING_TASK_NO_STOCK);
        }

        // 2. 批量生成明细行
        // TODO DONE @AI：把 stocks 作为参数，传递给 stocktakingTaskLineService，更收敛；（当前直接插入更简洁）
        List<MesWmStockTakingTaskLineDO> lines = new ArrayList<>(stocks.size());
        for (MesWmMaterialStockDO stock : stocks) {
            MesWmStockTakingTaskLineDO line = MesWmStockTakingTaskLineDO.builder()
                    .taskId(task.getId())
                    .materialStockId(stock.getId())
                    .itemId(stock.getItemId())
                    .batchId(stock.getBatchId())
                    .quantity(defaultQuantity(stock.getQuantityOnhand()))
                    .warehouseId(stock.getWarehouseId())
                    .locationId(stock.getLocationId())
                    .areaId(stock.getAreaId())
                    .status(MesWmStockTakingTaskLineStatusEnum.LOSS.getStatus())
                    .build();
            lines.add(line);
        }
        stockTakingTaskLineMapper.insertBatch(lines);
    }

    /**
     * 构建库存查询条件
     *
     * @param task 盘点任务
     * @return 查询条件 VO
     */
    private MesWmMaterialStockListReqVO buildStockQueryReqVO(MesWmStockTakingTaskDO task) {
        // 1. 从方案参数中提取过滤条件
        List<MesWmStockTakingPlanParamDO> params = stockTakingPlanParamMapper.selectListByPlanId(task.getPlanId());
        Assert.notEmpty(params, "盘点方案参数不能为空");

        // 2.1 拼接通用参数
        MesWmMaterialStockListReqVO reqVO = new MesWmMaterialStockListReqVO()
                .setWarehouseId(extractMesWmStockTakingPlanParamValueId(params, MesWmStockTakingPlanParamTypeEnum.WAREHOUSE.getType()))
                .setLocationId(extractMesWmStockTakingPlanParamValueId(params, MesWmStockTakingPlanParamTypeEnum.LOCATION.getType()))
                .setAreaId(extractMesWmStockTakingPlanParamValueId(params, MesWmStockTakingPlanParamTypeEnum.AREA.getType()))
                .setItemId(extractMesWmStockTakingPlanParamValueId(params, MesWmStockTakingPlanParamTypeEnum.ITEM.getType()))
                .setBatchId(extractMesWmStockTakingPlanParamValueId(params, MesWmStockTakingPlanParamTypeEnum.BATCH.getType()));
        // 2.2 对于动态盘点，设置时间参数并校验
        if (MesWmStockTakingTypeEnum.DYNAMIC.getType().equals(task.getType())) {
            Assert.notNull(task.getStartTime(), "动态盘点开始时间不能为空");
            Assert.notNull(task.getEndTime(), "动态盘点结束时间不能为空");
            reqVO.setStartTime(task.getStartTime()).setEndTime(task.getEndTime());
        }
        return reqVO;
    }

    /**
     * 从参数列表中提取第一个匹配类型的参数 ID
     *
     * @param params 参数列表
     * @param type 参数类型
     * @return 参数 ID（如果有多个，取第一个）
     */
    private Long extractMesWmStockTakingPlanParamValueId(List<MesWmStockTakingPlanParamDO> params, Integer type) {
        MesWmStockTakingPlanParamDO param = CollUtil.findOne(params,
                item -> Objects.equals(item.getType(), type) && item.getValueId() != null);
        return param != null ? param.getValueId() : null;
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
            if (MesWmStockTakingTaskLineStatusEnum.NORMAL.getStatus().equals(line.getStatus())
                    || MesWmStockTakingTaskLineStatusEnum.UNCOUNTED.getStatus().equals(line.getStatus())) {
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
    @Transactional(rollbackFor = Exception.class)
    public void updateStockTakingTaskLines(MesWmStockTakingTaskLineBatchUpdateReqVO updateReqVO) {
        MesWmStockTakingTaskDO task = validateTaskExists(updateReqVO.getTaskId());
        if (!MesWmStockTakingTaskStatusEnum.APPROVING.getStatus().equals(task.getStatus())) {
            throw exception(WM_STOCK_TAKING_TASK_NOT_APPROVING);
        }

        Set<Long> lineIds = convertSet(updateReqVO.getItems(), MesWmStockTakingTaskLineBatchUpdateReqVO.Item::getId);
        List<MesWmStockTakingTaskLineDO> lines = stockTakingTaskLineMapper.selectListByTaskIdAndIds(updateReqVO.getTaskId(), lineIds);
        if (lines.size() != updateReqVO.getItems().size()) {
            throw exception(WM_STOCK_TAKING_TASK_LINE_NOT_EXISTS);
        }
        Map<Long, MesWmStockTakingTaskLineDO> lineMap = lines.stream()
                .collect(Collectors.toMap(MesWmStockTakingTaskLineDO::getId, item -> item));
        for (MesWmStockTakingTaskLineBatchUpdateReqVO.Item item : updateReqVO.getItems()) {
            MesWmStockTakingTaskLineDO line = lineMap.get(item.getId());
            line.setTakingQuantity(item.getTakingQuantity());
            line.setRemark(item.getRemark());
            line.setStatus(resolveLineStatus(line.getQuantity(), item.getTakingQuantity()));
            stockTakingTaskLineMapper.updateById(line);
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
    public List<MesWmStockTakingTaskDO> getStockTakingTaskList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return stockTakingTaskMapper.selectByIds(ids);
    }

    @Override
    public PageResult<MesWmStockTakingTaskDO> getStockTakingTaskPage(MesWmStockTakingTaskPageReqVO pageReqVO) {
        return stockTakingTaskMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmStockTakingTaskLineDO> getStockTakingTaskLineList(Long taskId) {
        return stockTakingTaskLineMapper.selectListByTaskId(taskId);
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
        if (takingQuantity == null) {
            return MesWmStockTakingTaskLineStatusEnum.UNCOUNTED.getStatus();
        }
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

    private void validatePlanExists(Long id) {
        if (stockTakingPlanMapper.selectById(id) == null) {
            throw exception(WM_STOCK_TAKING_PLAN_NOT_EXISTS);
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
        if (!MesWmStockTakingTaskStatusEnum.PREPARE.getStatus().equals(task.getStatus())) {
            throw exception(WM_STOCK_TAKING_TASK_NOT_PREPARE);
        }
        return task;
    }

}
