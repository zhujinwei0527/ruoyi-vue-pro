package cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.area;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - MES 库位新增/修改 Request VO")
@Data
public class MesWmWarehouseAreaSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "库位编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "A001")
    @NotEmpty(message = "库位编码不能为空")
    private String code;

    @Schema(description = "库位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "默认库位")
    @NotEmpty(message = "库位名称不能为空")
    private String name;

    @Schema(description = "库区编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "库区不能为空")
    private Long locationId;

    @Schema(description = "面积", example = "20.00")
    private BigDecimal area;

    @Schema(description = "最大载重", example = "1000.00")
    private BigDecimal maxLoad;

    @Schema(description = "位置 X", example = "1")
    private Integer positionX;

    @Schema(description = "位置 Y", example = "1")
    private Integer positionY;

    @Schema(description = "位置 Z", example = "1")
    private Integer positionZ;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否启用不能为空")
    private Boolean enabled;

    @Schema(description = "是否冻结", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否冻结不能为空")
    private Boolean frozen;

    @Schema(description = "是否允许物料混放", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否允许物料混放不能为空")
    private Boolean allowItemMixing;

    @Schema(description = "是否允许批次混放", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否允许批次混放不能为空")
    private Boolean allowBatchMixing;

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
