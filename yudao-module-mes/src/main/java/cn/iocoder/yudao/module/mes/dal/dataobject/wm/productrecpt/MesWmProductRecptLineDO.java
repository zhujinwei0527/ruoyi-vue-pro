package cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 产品收货（入库）单行 DO
 */
@TableName("mes_wm_product_recpt_line")
@KeySequence("mes_wm_product_recpt_line_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmProductRecptLineDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 收货单编号
     *
     * 关联 {@link MesWmProductRecptDO#getId()}
     */
    private Long recptId;
    /**
     * 物料编号
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long itemId;
    /**
     * 收货数量
     */
    private BigDecimal quantity;
    /**
     * 批次编号
     *
     * TODO DONE @芋艿：保留。待 mes_wm_batch 模块迁移后补充 @link 关联
     */
    private Long batchId;
    /**
     * 批次号
     */
    private String batchCode;
    /**
     * 备注
     */
    private String remark;

}
