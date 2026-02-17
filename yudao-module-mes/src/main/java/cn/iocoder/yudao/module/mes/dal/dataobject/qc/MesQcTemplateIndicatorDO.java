package cn.iocoder.yudao.module.mes.dal.dataobject.qc;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 质检方案-检测指标项 DO
 *
 * @author 芋道源码
 */
@TableName("mes_qc_template_indicator")
@KeySequence("mes_qc_template_indicator_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesQcTemplateIndicatorDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    // TODO @AI：关联字段
    /**
     * 质检方案ID
     */
    private Long templateId;
    // TODO @AI：关联字段
    /**
     * 质检指标ID（关联 mes_qc_indicator，通过 JOIN 查询指标信息）
     *
     * TODO @芋艿：等 qc_indicator 完成后，完善关联查询和前端选择器
     */
    private Long indicatorId;
    /**
     * 检测方法/检测要求
     */
    private String checkMethod;
    /**
     * 标准值
     */
    private BigDecimal standardValue;
    // TODO @AI：应该关联单位那个表；
    /**
     * 单位
     */
    private String unit;
    /**
     * 误差上限
     */
    private BigDecimal thresholdMax;
    /**
     * 误差下限
     */
    private BigDecimal thresholdMin;
    /**
     * 说明图 URL
     */
    private String docUrl;
    /**
     * 备注
     */
    private String remark;

}
