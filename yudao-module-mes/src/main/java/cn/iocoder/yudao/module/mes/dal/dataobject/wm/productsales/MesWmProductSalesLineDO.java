package cn.iocoder.yudao.module.mes.dal.dataobject.wm.productsales;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 销售出库单行 DO
 *
 * @author 芋道源码
 */
@TableName("mes_wm_product_sales_line")
@KeySequence("mes_wm_product_sales_line_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmProductSalesLineDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 出库单ID
     *
     * 关联 {@link MesWmProductSalesDO#getId()}
     */
    private Long salesId;
    /**
     * 物料ID
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long itemId;
    /**
     * 出库数量
     */
    private BigDecimal quantity;
    // TODO @AI：去掉 pickedQuantity 字段；（不存储）
    /**
     * 已拣货数量
     */
    private BigDecimal pickedQuantity;
    /**
     * 批次ID
     */
    private Long batchId;
    /**
     * 库存记录ID
     */
    private Long materialStockId;
    // TODO @AI：换成 Boolean；
    /**
     * 是否出厂检验（Y/N）
     */
    private String oqcCheck;
    /**
     * 出厂检验单 ID
     */
    // TODO @AI：字段关联
    private Long oqcId;
    // TODO @AI：不用 oqcCode 字段；
    /**
     * 出厂检验单编号
     */
    private String oqcCode;
    // TODO @AI：关联枚举类；
    /**
     * 质量状态
     */
    private String qualityStatus;
    /**
     * 备注
     */
    private String remark;

}
