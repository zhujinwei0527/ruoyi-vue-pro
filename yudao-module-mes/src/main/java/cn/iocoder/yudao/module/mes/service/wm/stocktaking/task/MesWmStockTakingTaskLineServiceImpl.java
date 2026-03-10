package cn.iocoder.yudao.module.mes.service.wm.stocktaking.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialstock.vo.MesWmMaterialStockListReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.line.MesWmStockTakingTaskLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.line.MesWmStockTakingTaskLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialstock.MesWmMaterialStockDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.plan.MesWmStockTakingPlanParamDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.plan.MesWmStockTakingPlanParamMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.task.MesWmStockTakingTaskLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.task.MesWmStockTakingTaskMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmStockTakingPlanParamTypeEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmStockTakingTaskLineStatusEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmStockTakingTaskStatusEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmStockTakingTypeEnum;
import cn.iocoder.yudao.module.mes.service.wm.materialstock.MesWmMaterialStockService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 盘点任务行 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesWmStockTakingTaskLineServiceImpl implements MesWmStockTakingTaskLineService {

    @Resource
    private MesWmStockTakingTaskLineMapper stockTakingTaskLineMapper;
    @Resource
    private MesWmStockTakingPlanParamMapper stockTakingPlanParamMapper;
    @Resource
    private MesWmMaterialStockService materialStockService;

    @Resource
    private MesWmStockTakingTaskMapper stockTakingTaskMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateStockTakingLines(MesWmStockTakingTaskDO task) {
        // 1.1 构建查询条件（包含动态盘点时间校验）
        MesWmMaterialStockListReqVO reqVO = buildStockQueryReqVO(task);
        // 1.2 查询物料库存
        List<MesWmMaterialStockDO> stocks = materialStockService.getMaterialStockList(reqVO);
        if (CollUtil.isEmpty(stocks)) {
            throw exception(WM_STOCK_TAKING_TASK_NO_STOCK);
        }

        // 2. 批量生成明细行
        // TODO DONE @AI：使用 CollectionUtils convertList 方法简化代码
        List<MesWmStockTakingTaskLineDO> lines = convertList(stocks, stock ->
                MesWmStockTakingTaskLineDO.builder()
                        .taskId(task.getId()).materialStockId(stock.getId()).itemId(stock.getItemId())
                        .batchId(stock.getBatchId()).quantity(defaultQuantity(stock.getQuantityOnhand()))
                        .warehouseId(stock.getWarehouseId()).locationId(stock.getLocationId()).areaId(stock.getAreaId())
                        .status(MesWmStockTakingTaskLineStatusEnum.LOSS.getStatus()).build());
        stockTakingTaskLineMapper.insertBatch(lines);
    }

    @Override
    public PageResult<MesWmStockTakingTaskLineDO> getStockTakingTaskLinePage(MesWmStockTakingTaskLinePageReqVO pageReqVO) {
        return stockTakingTaskLineMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStockTakingTaskLine(MesWmStockTakingTaskLineSaveReqVO createReqVO) {
        // 1. 校验盘点任务存在，并且处于【准备中】状态
        validateTaskExistsAndPrepare(createReqVO.getTaskId());

        // 2. 创建盘点任务行
        MesWmStockTakingTaskLineDO line = BeanUtils.toBean(createReqVO, MesWmStockTakingTaskLineDO.class);
        stockTakingTaskLineMapper.insert(line);
        return line.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockTakingTaskLine(MesWmStockTakingTaskLineSaveReqVO updateReqVO) {
        // TODO @AI：补全注释
        // TODO @AI：搞个 validate exists 方法，这样 update 和 delete 可以服用；
        MesWmStockTakingTaskLineDO line = stockTakingTaskLineMapper.selectById(updateReqVO.getId());
        if (line == null) {
            throw exception(WM_STOCK_TAKING_TASK_LINE_NOT_EXISTS);
        }
        validateTaskExistsAndPrepare(updateReqVO.getTaskId());

        // 更新
        MesWmStockTakingTaskLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmStockTakingTaskLineDO.class);
        stockTakingTaskLineMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStockTakingTaskLine(Long id) {
        // TODO @AI：补全注释
        MesWmStockTakingTaskLineDO line = stockTakingTaskLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_STOCK_TAKING_TASK_LINE_NOT_EXISTS);
        }
        validateTaskExistsAndPrepare(line.getTaskId());

        // 2. 删除
        stockTakingTaskLineMapper.deleteById(id);
    }

    // TODO @AI：应该在对方的 service 里提供；
    private MesWmStockTakingTaskDO validateTaskExistsAndPrepare(Long id) {
        MesWmStockTakingTaskDO task = stockTakingTaskMapper.selectById(id);
        if (task == null) {
            throw exception(WM_STOCK_TAKING_TASK_NOT_EXISTS);
        }
        if (ObjUtil.notEqual(MesWmStockTakingTaskStatusEnum.PREPARE.getStatus(), task.getStatus())) {
            throw exception(WM_STOCK_TAKING_TASK_NOT_PREPARE);
        }
        return task;
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

    // TODO @芋艿：在确认下这个逻辑；
    private BigDecimal defaultQuantity(BigDecimal quantity) {
        return quantity == null ? BigDecimal.ZERO : quantity;
    }

}
