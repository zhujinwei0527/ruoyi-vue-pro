package cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicatorresult;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicator.MesQcIndicatorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.tm.tool.MesTmToolDO;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcResultValueTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 检验结果明细记录 DO
 *
 * @author 芋道源码
 */
@TableName("mes_qc_result_detail")
@KeySequence("mes_qc_result_detail_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesQcResultDetailDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 关联检验结果 ID
     *
     * 关联 {@link MesQcResultDO#getId()}
     */
    private Long resultId;
    /**
     * 检测指标 ID
     *
     * 关联 {@link MesQcIndicatorDO#getId()}
     */
    private Long indicatorId;
    // TODO @AI：toolId、unitMeasureId、valueType、valueSpecification 都不存储；
    /**
     * 检测工具 ID
     *
     * 关联 {@link MesTmToolDO#getId()}
     */
    private Long toolId;
    /**
     * 计量单位 ID
     *
     * 关联 {@link MesMdUnitMeasureDO#getId()}
     */
    private Long unitMeasureId;
    /**
     * 质检值类型
     *
     * 枚举 {@link MesQcResultValueTypeEnum}
     */
    private Integer valueType;
    /**
     * 值属性
     *
     * FILE 时存 IMG/FILE；DICT 时存字典类型名
     */
    private String valueSpecification;
    // TODO @AI：是不是可以调整成 String value？
    /**
     * 浮点值
     */
    private BigDecimal valueFloat;
    /**
     * 整数值
     */
    private Integer valueInteger;
    /**
     * 文字值
     */
    private String valueText;
    /**
     * 字典项值
     */
    private String valueDict;
    /**
     * 文件值
     */
    private String valueFile;
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
