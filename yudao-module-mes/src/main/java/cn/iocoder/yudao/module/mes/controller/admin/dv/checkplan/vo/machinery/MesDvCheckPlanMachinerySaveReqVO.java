package cn.iocoder.yudao.module.mes.controller.admin.dv.checkplan.vo.machinery;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - MES 点检保养方案设备新增 Request VO")
@Data
public class MesDvCheckPlanMachinerySaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "方案编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "方案编号不能为空")
    private Long planId;

    @Schema(description = "设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "设备不能为空")
    private Long machineryId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "预留字段1")
    private String attribute1;

    @Schema(description = "预留字段2")
    private String attribute2;

    @Schema(description = "预留字段3")
    private Integer attribute3;

    @Schema(description = "预留字段4")
    private Integer attribute4;

}
