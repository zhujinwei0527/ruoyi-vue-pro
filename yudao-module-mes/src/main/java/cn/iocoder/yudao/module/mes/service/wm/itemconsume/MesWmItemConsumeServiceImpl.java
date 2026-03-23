package cn.iocoder.yudao.module.mes.service.wm.itemconsume;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.feedback.MesProFeedbackDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProductBomDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemconsume.MesWmItemConsumeDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemconsume.MesWmItemConsumeLineDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.itemconsume.MesWmItemConsumeMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmItemConsumeStatusEnum;
import cn.iocoder.yudao.module.mes.enums.MesBizTypeConstants;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmTransactionTypeEnum;
import cn.iocoder.yudao.module.mes.service.pro.route.MesProRouteProductBomService;
import cn.iocoder.yudao.module.mes.service.wm.transaction.MesWmTransactionService;
import cn.iocoder.yudao.module.mes.service.wm.transaction.dto.MesWmTransactionSaveReqDTO;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseAreaService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseLocationService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

/**
 * MES 物料消耗记录 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesWmItemConsumeServiceImpl implements MesWmItemConsumeService {

    @Resource
    private MesWmItemConsumeMapper itemConsumeMapper;
    @Resource
    private MesWmItemConsumeLineService itemConsumeLineService;
    @Resource
    private MesProRouteProductBomService routeProductBomService;
    @Resource
    private MesWmTransactionService wmTransactionService;
    @Resource
    private MesWmWarehouseService warehouseService;
    @Resource
    private MesWmWarehouseLocationService locationService;
    @Resource
    private MesWmWarehouseAreaService areaService;

    @Override
    public MesWmItemConsumeDO generateItemConsume(MesProFeedbackDO feedback) {
        // 1. 查询当前工序的 BOM 物料配置
        List<MesProRouteProductBomDO> boms = routeProductBomService.getRouteProductBomList(
                feedback.getRouteId(), feedback.getProcessId(), feedback.getItemId());
        if (CollUtil.isEmpty(boms)) {
            return null;
        }

        // 2.1 生成消耗单头
        MesWmItemConsumeDO consume = MesWmItemConsumeDO.builder()
                .workOrderId(feedback.getWorkOrderId()).taskId(feedback.getTaskId())
                .workstationId(feedback.getWorkstationId()).processId(feedback.getProcessId())
                .feedbackId(feedback.getId()).consumeDate(LocalDateTime.now())
                .status(MesWmItemConsumeStatusEnum.PREPARE.getStatus()).build();
        itemConsumeMapper.insert(consume);
        // 2.2 批量生成消耗行（消耗数量 = BOM 用料比例 × 报工数量）
        List<MesWmItemConsumeLineDO> lines = convertList(boms, bom -> MesWmItemConsumeLineDO.builder()
                .consumeId(consume.getId()).itemId(bom.getItemId())
                .quantity(bom.getQuantity().multiply(feedback.getFeedbackQuantity())).build());
        itemConsumeLineService.createItemConsumeLineBatch(lines);
        // TODO @AI（from codex）：对齐，这里还需要按线边库/FIFO 生成消耗明细，并在库存不足时回退为无批次消耗明细。
        return consume;
    }

    @Override
    public void finishItemConsume(Long consumeId) {
        MesWmWarehouseDO virtualWarehouse = warehouseService.getWarehouseByCode(MesWmWarehouseDO.WIP_VIRTUAL_WAREHOUSE);
        MesWmWarehouseLocationDO virtualLocation = locationService.getWarehouseLocationByCode(MesWmWarehouseLocationDO.WIP_VIRTUAL_LOCATION);
        MesWmWarehouseAreaDO virtualArea = areaService.getWarehouseAreaByCode(MesWmWarehouseAreaDO.WIP_VIRTUAL_AREA);

        // 遍历消耗行，创建库存事务（从线边库扣减）
        List<MesWmItemConsumeLineDO> lines = itemConsumeLineService.getItemConsumeLineListByConsumeId(consumeId);
        for (MesWmItemConsumeLineDO line : lines) {
            wmTransactionService.createTransaction(new MesWmTransactionSaveReqDTO()
                    .setType(MesWmTransactionTypeEnum.OUT.getType()).setItemId(line.getItemId())
                    .setQuantity(line.getQuantity().negate()) // 库存减少
                    .setWarehouseId(virtualWarehouse.getId()).setLocationId(virtualLocation.getId()).setAreaId(virtualArea.getId())
                    .setCheckFlag(false) // 线边库允许负库存
                    .setBizType(MesBizTypeConstants.WM_ITEM_CONSUME).setBizId(consumeId)
                    .setBizCode("").setBizLineId(line.getId()));
        }

        // 更新消耗单状态为已完成
        itemConsumeMapper.updateById(MesWmItemConsumeDO.builder()
                .id(consumeId).status(MesWmItemConsumeStatusEnum.FINISHED.getStatus()).build());
    }

}
