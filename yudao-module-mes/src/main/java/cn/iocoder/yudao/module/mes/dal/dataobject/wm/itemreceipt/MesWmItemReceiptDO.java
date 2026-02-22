package cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.vendor.MesMdVendorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDO;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmItemReceiptStatusEnum;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.arrivalnotice.MesWmArrivalNoticeDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MES 采购入库单 DO
 */
@TableName("mes_wm_item_receipt")
@KeySequence("mes_wm_item_receipt_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmItemReceiptDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 入库单编码
     */
    private String code;
    /**
     * 入库单名称
     */
    private String name;
    /**
     * 来料检验单编号
     *
     * 关联 {@link MesQcIqcDO#getId()}
     */
    private Long iqcId;
    /**
     * 到货通知单编号
     *
     * 关联 {@link MesWmArrivalNoticeDO#getId()}
     */
    private Long noticeId;
    // TODO DONE @AI：保留该冗余字段。虽然可通过 noticeId 间接获取，但查询/展示场景频繁使用，冗余存储避免多表关联，提升性能
    // TODO @AI：这个拼接下就好，基本可以忽略不计的性能；并且，它没有筛选的诉求；
    /**
     * 采购订单编号
     */
    private String purchaseOrderCode;
    /**
     * 供应商编号
     *
     * 关联 {@link MesMdVendorDO#getId()}
     */
    private Long vendorId;
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
     * 入库日期
     */
    private LocalDateTime receiptDate;
    /**
     * 状态
     *
     * 枚举 {@link MesWmItemReceiptStatusEnum}
     */
    private Integer status;
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
