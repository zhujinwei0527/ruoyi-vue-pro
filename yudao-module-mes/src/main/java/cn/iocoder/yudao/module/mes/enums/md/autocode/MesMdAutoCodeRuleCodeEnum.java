package cn.iocoder.yudao.module.mes.enums.md.autocode;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * MES 编码规则代码枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesMdAutoCodeRuleCodeEnum {

    ITEM_CODE("MD_ITEM_CODE", "物料编码"),
    SN_CODE("WM_SN_CODE", "SN 码"),
    PACKAGE_CODE("WM_PACKAGE_CODE", "装箱单编码"),
    BATCH_CODE("WM_BATCH_CODE", "批次编码"),
    TASK_CODE("PRO_TASK_CODE", "生产任务编码"),
    QC_IQC_CODE("QC_IQC_CODE", "来料检验单编码"),
    QC_IPQC_CODE("QC_IPQC_CODE", "过程检验单编码"),
    QC_RQC_CODE("QC_RQC_CODE", "退货检验单编码");

    /**
     * 规则代码
     */
    private final String code;
    /**
     * 规则名称
     */
    private final String name;

}
