package cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.vendor.MesMdVendorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// TODO @AI：IQC 完整的拼写，也加下；
/**
 * MES 来料检验单（IQC） DO
 *
 * @author 芋道源码
 */
@TableName("mes_qc_iqc")
@KeySequence("mes_qc_iqc_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesQcIqcDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 检验单编号
     */
    private String code;
    /**
     * 检验单名称
     */
    private String name;
    /**
     * 检验模板 ID
     *
     * 关联 {@link MesQcTemplateDO#getId()}
     */
    private Long templateId;

    // ========== 来源单据（TODO @芋艿：WM 模块迁移后接入） ==========

    /**
     * 来源单据 ID
     */
    private Long sourceDocId;
    /**
     * 来源单据类型
     *
     * 字典 mes_qc_source_doc_type
     */
    private String sourceDocType;
    /**
     * 来源单据编号
     */
    private String sourceDocCode;
    /**
     * 来源单据行 ID
     */
    private Long sourceLineId;

    // ========== 供应商 ==========

    /**
     * 供应商 ID
     *
     * 关联 {@link MesMdVendorDO#getId()}
     */
    private Long vendorId;
    /**
     * 供应商批次号
     */
    private String vendorBatch;

    // ========== 物料 ==========

    /**
     * 产品物料 ID
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long itemId;

    // ========== 数量 ==========

    /**
     * 最低检测数
     */
    private Integer minCheckQuantity;
    /**
     * 最大不合格数
     */
    private Integer maxUnqualifiedQuantity;
    /**
     * 本次接收数量
     */
    private BigDecimal receivedQuantity;
    /**
     * 本次检测数量
     */
    private Integer checkQuantity;
    /**
     * 合格品数量
     */
    private Integer qualifiedQuantity;
    /**
     * 不合格品数量
     */
    private Integer unqualifiedQuantity;

    // ========== 缺陷统计 ==========

    /**
     * 致命缺陷率（%）
     */
    private BigDecimal criticalRate;
    /**
     * 严重缺陷率（%）
     */
    private BigDecimal majorRate;
    /**
     * 轻微缺陷率（%）
     */
    private BigDecimal minorRate;
    /**
     * 致命缺陷数量
     */
    private Integer criticalQuantity;
    /**
     * 严重缺陷数量
     */
    private Integer majorQuantity;
    /**
     * 轻微缺陷数量
     */
    private Integer minorQuantity;

    // ========== 检验 ==========

    // TODO @AI：需要搞个枚举么？使用 int 把。现在就搞了；
    /**
     * 检测结果
     *
     * 字典 mes_qc_check_result
     */
    private String checkResult;
    /**
     * 来料日期
     */
    private LocalDateTime receiveDate;
    /**
     * 检测日期
     */
    private LocalDateTime inspectDate;
    /**
     * 检测人员
     */
    // TODO @AI：改成 inspectorUserId；
    private String inspector;
    // TODO @AI：需要搞个枚举么？使用 int 把。现在就搞了；
    /**
     * 状态（0=草稿 1=已完成）
     *
     * 字典 mes_qc_iqc_status
     */
    private Integer status;
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
