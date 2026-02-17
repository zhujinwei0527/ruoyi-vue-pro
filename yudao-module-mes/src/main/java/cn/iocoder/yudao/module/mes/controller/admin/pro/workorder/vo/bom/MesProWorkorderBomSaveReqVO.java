package cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.bom;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - MES 生产工单 BOM 新增/修改 Request VO")
@Data
public class MesProWorkorderBomSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "生产工单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "生产工单编号不能为空")
    private Long workorderId;

    @Schema(description = "BOM 物料编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
    @NotNull(message = "BOM 物料不能为空")
    private Long itemId;

    @Schema(description = "单位编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "300")
    @NotNull(message = "单位不能为空")
    private Long unitMeasureId;

    @Schema(description = "物料产品标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "ITEM")
    @NotEmpty(message = "物料产品标识不能为空")
    private String itemOrProduct;

    @Schema(description = "预计使用量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10.00")
    @NotNull(message = "预计使用量不能为空")
    private BigDecimal quantity;

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
