package cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - MES 检验结果 Response VO")
@Data
public class MesQcIndicatorResultRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "样品编号", example = "SPL-001")
    private String code;

    @Schema(description = "关联质检单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long qcId;

    @Schema(description = "质检类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer qcType;

    @Schema(description = "产品物料ID", example = "1")
    private Long itemId;

    @Schema(description = "物资SN", example = "SN-001")
    private String sn;

    @Schema(description = "备注", example = "无")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    // ========== 关联查询字段 ==========

    @Schema(description = "质检单编号", example = "IQC-001")
    private String qcCode;

    @Schema(description = "质检单名称", example = "xxx来料检验")
    private String qcName;

    @Schema(description = "产品物料编码", example = "ITEM-001")
    private String itemCode;

    @Schema(description = "产品物料名称", example = "电阻")
    private String itemName;

    @Schema(description = "规格型号", example = "100Ω")
    private String itemSpecification;

    @Schema(description = "单位名称", example = "个")
    private String unitName;

    // ========== 子表：检验结果明细 ==========

    @Schema(description = "检验结果明细列表")
    private List<Item> items;

    @Schema(description = "检验结果明细项")
    @Data
    public static class Item {

        @Schema(description = "编号", example = "1024")
        private Long id;

        @Schema(description = "关联检验结果ID", example = "1")
        private Long resultId;

        @Schema(description = "检测指标ID", example = "1")
        private Long indicatorId;

        @Schema(description = "检测值（统一存为字符串）", example = "3.14")
        private String value;

        @Schema(description = "备注", example = "无")
        private String remark;

        // ========== 关联查询字段（来自 IQC line + indicator） ==========

        @Schema(description = "检测指标编码", example = "IDX-001")
        private String indicatorCode;

        @Schema(description = "检测指标名称", example = "外观检查")
        private String indicatorName;

        @Schema(description = "检测指标类型", example = "QUANTITATIVE")
        private String indicatorType;

        @Schema(description = "质检值类型", example = "1")
        private Integer valueType;

        @Schema(description = "值属性", example = "IMG")
        private String valueSpecification;

        @Schema(description = "检测工具名称", example = "卡尺")
        private String toolName;

        @Schema(description = "检测方法", example = "目视检查")
        private String checkMethod;

        @Schema(description = "标准值", example = "10.0000")
        private BigDecimal standardValue;

        @Schema(description = "计量单位名称", example = "mm")
        private String unitMeasureName;

        @Schema(description = "误差上限", example = "10.5000")
        private BigDecimal maxThreshold;

        @Schema(description = "误差下限", example = "9.5000")
        private BigDecimal minThreshold;

    }

}
