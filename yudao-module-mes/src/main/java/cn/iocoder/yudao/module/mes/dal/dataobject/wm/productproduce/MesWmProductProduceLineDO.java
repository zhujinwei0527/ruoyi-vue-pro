package cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 生产入库单行 DO
 *
 * @author 芋道源码
 */
@TableName("mes_wm_product_produce_line")
@KeySequence("mes_wm_product_produce_line_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmProductProduceLineDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 入库单 ID
     *
     * 关联 {@link MesWmProductProduceDO#getId()}
     */
    private Long produceId;
    /**
     * 报工记录 ID
     *
     * TODO @芋艿：关联 ProFeedback，待 ProFeedback 联调时补充
     */
    private Long feedbackId;
    /**
     * 物料 ID
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long itemId;
    /**
     * 入库数量
     */
    private BigDecimal quantity;
    /**
     * 批次 ID
     */
    private Long batchId;
    /**
     * 批次号
     */
    private String batchCode;
    /**
     * 过期日期
     */
    private LocalDateTime expireDate;
    /**
     * 生产批号
     */
    private String lotNumber;
    // TODO @AI：待定
    /**
     * 质量状态
     *
     * 0-待检, 1-合格, 2-不合格
     * 字典类型 mes_wm_quality_status
     */
    private Integer qualityStatus;
    /**
     * 备注
     */
    private String remark;

}
