package cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.arrivalnotice.MesWmArrivalNoticeLineDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 采购入库单行 DO
 */
@TableName("mes_wm_item_receipt_line")
@KeySequence("mes_wm_item_receipt_line_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmItemReceiptLineDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 入库单编号
     *
     * 关联 {@link MesWmItemReceiptDO#getId()}
     */
    private Long receiptId;
    // TODO @AI：字段要不完整点，更好维护；arrivalNoticeLineId；因为 noticeLineId 关联到 arrivalNoticeLineId；不然会有歧义；
    /**
     * 到货通知单行编号
     *
     * 关联 {@link MesWmArrivalNoticeLineDO#getId()}
     */
    private Long noticeLineId;
    /**
     * 物料编号
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long itemId;
    /**
     * 入库数量
     */
    private BigDecimal receivedQuantity;
    /**
     * 批次编号
     *
     * TODO DONE @芋艿：保留。待 mes_wm_batch 模块迁移后补充 @link 关联
     */
    private Long batchId;
    // TODO @AI：warehouseId、locationId、areaId 这几个字段，是不是不需要；只需要在 detaildo 里存储就好了；
    /**
     * 仓库编号
     *
     * 关联 {@link MesWmWarehouseDO#getId()}
     */
    private Long warehouseId;
    /**
     * 库区编号
     *
     * 关联 {@link MesWmWarehouseLocationDO#getId()}
     */
    private Long locationId;
    /**
     * 库位编号
     *
     * 关联 {@link MesWmWarehouseAreaDO#getId()}
     */
    private Long areaId;
    /**
     * 生产日期
     */
    private LocalDateTime productionDate;
    /**
     * 有效期
     */
    private LocalDateTime expireDate;
    /**
     * 生产批号
     */
    private String productionBatchNumber;
    // TODO @AI：貌似不用 iqcCheckFlag 存储；因为 noticeLineId 可以关联到
    /**
     * 是否需要来料检验
     */
    private Boolean iqcCheckFlag;
    // TODO DONE @AI：已添加关联注释。因 QC 模块 DO 类尚未迁移，暂用表名标识
    // TODO @AI：可以添加了；
    /**
     * 来料检验单编号
     *
     * 关联 mes_qc_iqc.id（待 QC 模块 DO 迁移后改为 @link）
     */
    private Long iqcId;
    /**
     * 备注
     */
    private String remark;

    /**
     * 预留字段1
     */
    private String attribute1;
    /**
     * 预留字段2
     */
    private String attribute2;
    /**
     * 预留字段3
     */
    private Integer attribute3;
    /**
     * 预留字段4
     */
    private Integer attribute4;

}
