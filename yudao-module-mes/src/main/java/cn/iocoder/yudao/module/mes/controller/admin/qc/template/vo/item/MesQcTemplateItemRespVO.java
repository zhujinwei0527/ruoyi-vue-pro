package cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 质检方案-产品关联 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MesQcTemplateItemRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "质检方案ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long templateId;

    @Schema(description = "产品物料ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("产品物料ID")
    private Long itemId;

    @Schema(description = "最低检测数", example = "5")
    @ExcelProperty("最低检测数")
    private Integer quantityCheck;

    // TODO @AI：VO 里，（0=不启用） 去掉
    @Schema(description = "最大不合格数（0=不启用）", example = "0")
    @ExcelProperty("最大不合格数")
    private Integer quantityUnqualified;

    // TODO @AI：VO 里，（0=不允许） 去掉
    @Schema(description = "最大致命缺陷率（%，0=不允许）", example = "0")
    @ExcelProperty("致命缺陷率%")
    private BigDecimal criticalRate;

    // TODO @AI：VO 里，（0=不允许） 去掉
    @Schema(description = "最大严重缺陷率（%，0=不允许）", example = "0")
    @ExcelProperty("严重缺陷率%")
    private BigDecimal majorRate;

    @Schema(description = "最大轻微缺陷率（%）", example = "100")
    @ExcelProperty("轻微缺陷率%")
    private BigDecimal minorRate;

    @Schema(description = "备注", example = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
