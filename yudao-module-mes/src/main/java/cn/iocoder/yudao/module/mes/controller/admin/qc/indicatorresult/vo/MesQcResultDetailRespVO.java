package cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - MES 检验结果明细 Response VO")
@Data
public class MesQcResultDetailRespVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "关联检验结果ID", example = "1")
    private Long resultId;

    @Schema(description = "检测指标ID", example = "1")
    private Long indicatorId;

    @Schema(description = "检测工具ID", example = "1")
    private Long toolId;

    @Schema(description = "计量单位ID", example = "1")
    private Long unitMeasureId;

    @Schema(description = "质检值类型（1=浮点 2=整数 3=文本 4=字典 5=文件）", example = "1")
    private Integer valueType;

    @Schema(description = "值属性", example = "IMG")
    private String valueSpecification;

    @Schema(description = "浮点值", example = "3.14")
    private BigDecimal valueFloat;

    @Schema(description = "整数值", example = "100")
    private Integer valueInteger;

    @Schema(description = "文字值", example = "合格")
    private String valueText;

    @Schema(description = "字典项值", example = "PASS")
    private String valueDict;

    @Schema(description = "文件值", example = "https://xxx.com/file.jpg")
    private String valueFile;

    @Schema(description = "备注", example = "无")
    private String remark;

    // ========== 关联查询字段（来自 IQC line + indicator） ==========

    @Schema(description = "检测指标编码（关联查询）", example = "IDX-001")
    private String indicatorCode;

    @Schema(description = "检测指标名称（关联查询）", example = "外观检查")
    private String indicatorName;

    @Schema(description = "检测指标类型（关联查询）", example = "QUANTITATIVE")
    private String indicatorType;

    @Schema(description = "检测工具名称（关联查询）", example = "卡尺")
    private String toolName;

    @Schema(description = "检测方法（关联查询）", example = "目视检查")
    private String checkMethod;

    @Schema(description = "标准值（关联查询）", example = "10.0000")
    private BigDecimal standardValue;

    @Schema(description = "计量单位名称（关联查询）", example = "mm")
    private String unitMeasureName;

    @Schema(description = "误差上限（关联查询）", example = "10.5000")
    private BigDecimal maxThreshold;

    @Schema(description = "误差下限（关联查询）", example = "9.5000")
    private BigDecimal minThreshold;

}
