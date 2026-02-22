package cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialstock;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemTypeDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.vendor.MesMdVendorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// TODO DONE @芋艿：当前包名 materialstock 与表名 mes_wm_material_stock 保持一致，语义清晰，暂不调整
/**
 * MES 库存台账（仓库现有量）DO
 */
@TableName("mes_wm_material_stock")
@KeySequence("mes_wm_material_stock_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmMaterialStockDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    // TODO DONE @AI：保留冗余字段。库存台账查询频繁，冗余存储可避免关联 item 表，提升查询性能
    // TODO @AI：还是不记录；因为它没有筛选的诉求；并且，它可以通过 itemId 间接获取；冗余存储反而增加了维护成本；
    /**
     * 物料分类编号
     *
     * 关联 {@link MesMdItemTypeDO#getId()}
     */
    private Long itemTypeId;
    /**
     * 物料编号
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long itemId;
    // TODO DONE @AI：保留冗余字段。库存台账查询频繁，冗余存储可避免关联 item 表，提升查询性能
    // TODO @AI：还是不记录；因为它没有筛选的诉求；并且，它可以通过 itemId 间接获取；冗余存储反而增加了维护成本；
    /**
     * 计量单位编号
     *
     * 关联 {@link MesMdUnitMeasureDO#getId()}
     */
    private Long unitMeasureId;
    /**
     * 批次编号
     *
     * TODO DONE @芋艿：保留。待 mes_wm_batch 模块迁移后补充 @link 关联
     */
    private Long batchId;
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
     * 供应商编号
     *
     * 关联 {@link MesMdVendorDO#getId()}
     */
    private Long vendorId;
    /**
     * 生产工单编号
     *
     * 关联 {@link MesProWorkOrderDO#getId()}
     */
    private Long workOrderId;
    // TODO @AI：onhand_quantity 更合理；更符合现在项目的风格。改下；
    /**
     * 在库数量
     */
    private BigDecimal quantityOnhand;
    /**
     * 入库时间
     */
    private LocalDateTime recptDate;
    /**
     * 库存有效期
     */
    private LocalDateTime expireDate;
    /**
     * 生产日期
     */
    private LocalDateTime productionDate;
    /**
     * 是否冻结
     */
    private Boolean frozen;
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
