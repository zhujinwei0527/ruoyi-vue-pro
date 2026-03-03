package cn.iocoder.yudao.module.mes.controller.admin.wm.outsourceissue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 外协发料单新增/修改 Request VO")
@Data
public class MesWmOutsourceIssueSaveReqVO {

    @Schema(description = "发料单ID", example = "1024")
    private Long id;

    @Schema(description = "发料单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "WOS202603020001")
    @NotEmpty(message = "发料单编号不能为空")
    private String code;

    @Schema(description = "发料单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "外协发料单001")
    @NotEmpty(message = "发料单名称不能为空")
    private String name;

    @Schema(description = "供应商ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "供应商ID不能为空")
    private Long vendorId;

    @Schema(description = "供应商编码", example = "V001")
    private String vendorCode;

    @Schema(description = "供应商名称", example = "供应商A")
    private String vendorName;

    @Schema(description = "生产工单ID", example = "1")
    private Long workorderId;

    @Schema(description = "生产工单编码", example = "WO202603020001")
    private String workorderCode;

    @Schema(description = "生产工单名称", example = "生产工单001")
    private String workorderName;

    @Schema(description = "发料日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "发料日期不能为空")
    private LocalDateTime issueDate;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
