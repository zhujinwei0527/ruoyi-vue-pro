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

    MD_ITEM_CODE("MD_ITEM_CODE", "物料编码"),
    MD_VENDOR_CODE("MD_VENDOR_CODE", "供应商编码"),
    MD_CLIENT_CODE("MD_CLIENT_CODE", "客户编码"),
    MD_WORKSTATION_CODE("MD_WORKSTATION_CODE", "工作站编码"),
    WM_SN_CODE("WM_SN_CODE", "SN 码"),
    WM_PACKAGE_CODE("WM_PACKAGE_CODE", "装箱单编码"),
    WM_BATCH_CODE("WM_BATCH_CODE", "批次编码"),
    PRO_TASK_CODE("PRO_TASK_CODE", "生产任务编码"),
    QC_IQC_CODE("QC_IQC_CODE", "来料检验单编码"),
    QC_IPQC_CODE("QC_IPQC_CODE", "过程检验单编码"),
    QC_OQC_CODE("QC_OQC_CODE", "出货检验单编码"),
    QC_RQC_CODE("QC_RQC_CODE", "退货检验单编码"),
    WM_WAREHOUSE_CODE("WM_WAREHOUSE_CODE", "仓库编码"),
    WM_LOCATION_CODE("WM_LOCATION_CODE", "库区编码"),
    WM_AREA_CODE("WM_AREA_CODE", "库位编码");

    /**
     * 规则代码
     */
    private final String code;
    /**
     * 规则名称
     */
    private final String name;

}
