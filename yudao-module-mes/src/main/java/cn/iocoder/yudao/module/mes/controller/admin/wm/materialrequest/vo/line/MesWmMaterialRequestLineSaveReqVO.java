package cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.line;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - MES 领料申请单行新增/修改 Request VO")
@Data
public class MesWmMaterialRequestLineSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "领料申请单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "领料申请单ID不能为空")
    private Long materialRequestId;

    @Schema(description = "产品物料ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "产品物料ID不能为空")
    private Long itemId;

    @Schema(description = "计量单位ID", example = "1")
    private Long unitMeasureId;

    @Schema(description = "需求数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10.00")
    @NotNull(message = "需求数量不能为空")
    @DecimalMin(value = "0.01", message = "需求数量必须大于 0")
    private BigDecimal quantity;

    @Schema(description = "备注", example = "备注")
    private String remark;

    }
