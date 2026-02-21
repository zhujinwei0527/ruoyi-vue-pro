package cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicatorresult;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDO;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * MES 检验结果记录 DO
 *
 * @author 芋道源码
 */
@TableName("mes_qc_indicator_result")
@KeySequence("mes_qc_indicator_result_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesQcIndicatorResultDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 样品编号
     */
    private String code;
    /**
     * 关联质检单 ID（IQC/IPQC/OQC/RQC 的 id）
     *
     * 关联 IQC {@link MesQcIqcDO#getId()}
     * TODO @芋艿：IPQC/OQC/RQC 关联补充
     */
    private Long qcId;
    /**
     * 质检类型
     *
     * 枚举 {@link MesQcTypeEnum}
     */
    private Integer qcType;
    /**
     * 产品物料 ID
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long itemId;
    /**
     * 物资 SN
     */
    private String sn;
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
