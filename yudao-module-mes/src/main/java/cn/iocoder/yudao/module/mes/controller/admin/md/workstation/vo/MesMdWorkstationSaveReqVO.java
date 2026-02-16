package cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - MES 工位新增/修改 Request VO")
@Data
public class MesMdWorkstationSaveReqVO {

    @Schema(description = "工位编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "工位编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "WK001")
    @NotEmpty(message = "工位编码不能为空")
    private String code;

    @Schema(description = "工位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "一号工位")
    @NotEmpty(message = "工位名称不能为空")
    private String name;

    @Schema(description = "工位地点", example = "A区1号线")
    private String address;

    @Schema(description = "所在车间编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "所在车间不能为空")
    private Long workshopId;

    @Schema(description = "工序编号", example = "1")
    private Long processId;

    @Schema(description = "线边库编号", example = "1")
    private Long warehouseId;

    @Schema(description = "库区编号", example = "1")
    private Long locationId;

    @Schema(description = "库位编号", example = "1")
    private Long areaId;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "备注", example = "备注")
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
