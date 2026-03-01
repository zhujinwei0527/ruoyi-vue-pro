package cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnsales;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 *
 * @author 芋道源码
 * MES 销售退货单行 DO
 */
@TableName("mes_wm_return_sales_line")
@KeySequence("mes_wm_return_sales_line_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmReturnSalesLineDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 退货单 ID
     *
     * 关联 {@link MesWmReturnSalesDO#getId()}
     */
    private Long returnId;
    /**
     * 物料 ID
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long itemId;
    // TODO @AI：这个改成 quantity；简化（听我的）
    /**
     * 退货数量
     */
    private BigDecimal quantityReturned;
    /**
     * 批次 ID
     */
    private Long batchId;
    // TODO @AI：需要 @下对应的枚举类，方便理解；
    /**
     * 质量状态
     */
    private String qualityStatus;
    /**
     * 备注
     */
    private String remark;

}
