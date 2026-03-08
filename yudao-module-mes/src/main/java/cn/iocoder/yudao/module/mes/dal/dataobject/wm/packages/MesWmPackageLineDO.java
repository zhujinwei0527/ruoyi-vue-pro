package cn.iocoder.yudao.module.mes.dal.dataobject.wm.packages;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * MES 装箱明细 DO
 */
@TableName("mes_wm_package_line")
@KeySequence("mes_wm_package_line_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmPackageLineDO extends BaseDO {

    @TableId
    private Long id;
    /**
     * 装箱单 ID
     *
     * 关联 {@link MesWmPackageDO#getId()}
     */
    private Long packageId;
    /**
     * 库存记录 ID
     */
    private Long materialStockId;
    /**
     * 产品物料 ID
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long itemId;
    /**
     * 装箱数量
     */
    private BigDecimal quantity;
    // DONE @芋艿：需要在确认下；
    /**
     * 生产工单 ID
     *
     * 关联 {@link MesProWorkOrderDO#getId()}
     */
    private Long workOrderId;
    /**
     * 仓库 ID
     *
     * 关联 {@link MesWmWarehouseDO#getId()}
     */
    private Long warehouseId;
    /**
     * 库区 ID
     *
     * 关联 {@link MesWmWarehouseLocationDO#getId()}
     */
    private Long locationId;
    /**
     * 库位 ID
     *
     * 关联 {@link MesWmWarehouseAreaDO#getId()}
     */
    private Long areaId;
    /**
     * 有效期
     */
    private LocalDate expireDate;
    /**
     * 备注
     */
    private String remark;

}
