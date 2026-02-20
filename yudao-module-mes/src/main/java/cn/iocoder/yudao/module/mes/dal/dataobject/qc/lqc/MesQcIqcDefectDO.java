package cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * MES 来料检验缺陷记录 DO
 *
 * @author 芋道源码
 */
@TableName("mes_qc_iqc_defect")
@KeySequence("mes_qc_iqc_defect_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesQcIqcDefectDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 来料检验单 ID
     *
     * 关联 {@link MesQcIqcDO#getId()}
     */
    private Long iqcId;
    /**
     * 来料检验行 ID
     *
     * 关联 {@link MesQcIqcLineDO#getId()}
     */
    private Long lineId;
    /**
     * 缺陷描述
     */
    private String defectName;
    // TODO @AI：要不枚举值也加下，也改成 int；
    /**
     * 缺陷等级
     *
     * 字典 mes_defect_level
     */
    private String defectLevel;
    /**
     * 缺陷数量
     */
    private Integer defectQuantity;
    /**
     * 备注
     */
    private String remark;

    // ========== 预留字段 ==========

    /**
     * 预留字段 1
     */
    private String attribute1;
    /**
     * 预留字段 2
     */
    private String attribute2;
    /**
     * 预留字段 3
     */
    private Integer attribute3;
    /**
     * 预留字段 4
     */
    private Integer attribute4;

}
