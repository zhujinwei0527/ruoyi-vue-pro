package cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourcereceipt;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.iqc.MesQcIqcDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 外协入库单行 DO
 */
@TableName("mes_wm_outsource_receipt_line")
@KeySequence("mes_wm_outsource_receipt_line_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmOutsourceReceiptLineDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 入库单编号
     *
     * 关联 {@link MesWmOutsourceReceiptDO#getId()}
     */
    private Long receiptId;
    /**
     * 物料编号
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long itemId;
    /**
     * 入库数量
     */
    private BigDecimal quantity;
    /**
     * 批次编号
     *
     * DONE @芋艿：保留。待 mes_wm_batch 模块迁移后补充 @link 关联（AI 未修复原因：标注为后续处理，需人工介入）
     */
    private Long batchId;
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
    private String lotNumber;
    /**
     * 备注
     */
    private String remark;
    /**
     * 来料检验单编号
     *
     * 关联 {@link MesQcIqcDO#getId()}
     */
    private Long iqcId;
    /**
     * 是否需要质检
     */
    private Boolean iqcCheckFlag;
    /**
     * 质量状态
     *
     * 枚举 {@link cn.iocoder.yudao.module.mes.enums.wm.MesWmQualityStatusEnum}
     */
    private Integer qualityStatus;

}
