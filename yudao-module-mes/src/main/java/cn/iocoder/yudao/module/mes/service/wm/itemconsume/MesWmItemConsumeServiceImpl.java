package cn.iocoder.yudao.module.mes.service.wm.itemconsume;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.feedback.MesProFeedbackDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProductBomDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemconsume.MesWmItemConsumeDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemconsume.MesWmItemConsumeLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.itemconsume.MesWmItemConsumeMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmItemConsumeStatusEnum;
import cn.iocoder.yudao.module.mes.service.pro.route.MesProRouteProductBomService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

/**
 * MES 物料消耗记录 Service 实现类
 *
 * 参考 KTG {@code WmItemConsumeServiceImpl#generateItemConsume}
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

    // TODO @芋艿：待 MaterialStockService 补充 decreaseStock 后，注入并调用
    // @Resource
    // private MesWmMaterialStockService materialStockService;

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
        return consume;
    }

    @Override
    public void finishItemConsume(Long consumeId) {
        // TODO @芋艿：待 MaterialStockService 补充 decreaseStock 后实现库存扣减
        // List<MesWmItemConsumeLineDO> lines = itemConsumeLineService.getItemConsumeLineListByConsumeId(consumeId);
        // for (MesWmItemConsumeLineDO line : lines) {
        //     materialStockService.decreaseStock(line.getItemId(), ..., line.getQuantity());
        // }
        // 更新消耗单状态为已完成
        itemConsumeMapper.updateById(MesWmItemConsumeDO.builder()
                .id(consumeId).status(MesWmItemConsumeStatusEnum.FINISHED.getStatus()).build());
    }

}
