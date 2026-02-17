package cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - MES 质检方案新增/修改 Request VO")
@Data
public class MesQcTemplateSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "方案编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "QCT001")
    @NotEmpty(message = "方案编号不能为空")
    private String code;

    @Schema(description = "方案名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "成品出货检验方案")
    @NotEmpty(message = "方案名称不能为空")
    private String name;

    // TODO @AI：会用 List<String>；这里不用 （逗号分隔：IQC,IPQC,OQC,RQC） 注释；
    @Schema(description = "检测种类（逗号分隔：IQC,IPQC,OQC,RQC）", requiredMode = Schema.RequiredMode.REQUIRED, example = "IQC,OQC")
    @NotEmpty(message = "检测种类不能为空")
    private String types;

    // TODO @AI：Boolean；
    @Schema(description = "是否启用（Y/N）", requiredMode = Schema.RequiredMode.REQUIRED, example = "Y")
    @NotEmpty(message = "是否启用不能为空")
    private String enableFlag;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
