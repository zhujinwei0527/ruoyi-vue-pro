package cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 质检方案 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MesQcTemplateRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "方案编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "QCT001")
    @ExcelProperty("方案编号")
    private String code;

    @Schema(description = "方案名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "成品出货检验方案")
    @ExcelProperty("方案名称")
    private String name;

    // TODO @AI：会用 List<String>；这里不用 （逗号分隔：IQC,IPQC,OQC,RQC） 注释；
    @Schema(description = "检测种类（逗号分隔：IQC,IPQC,OQC,RQC）", requiredMode = Schema.RequiredMode.REQUIRED, example = "IQC,OQC")
    @ExcelProperty("检测种类")
    private String types;

    // TODO @AI：Boolean；
    @Schema(description = "是否启用（Y/N）", requiredMode = Schema.RequiredMode.REQUIRED, example = "Y")
    @ExcelProperty("是否启用")
    private String enableFlag;

    @Schema(description = "备注", example = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
