package cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.line;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 生产入库单行 Save Request VO")
@Data
public class MesWmProductProduceLineSaveReqVO {

    @Schema(description = "行ID", example = "1")
    private Long id;

    @Schema(description = "入库单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "入库单ID不能为空")
    private Long produceId;

    @Schema(description = "报工单 ID", example = "1")
    private Long feedbackId;

    @Schema(description = "物料ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "物料ID不能为空")
    private Long itemId;

    @Schema(description = "入库数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    @NotNull(message = "入库数量不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "入库数量必须大于 0")
    private BigDecimal quantity;

    @Schema(description = "批次ID", example = "1")
    private Long batchId;

    @Schema(description = "批次号", example = "BATCH20260101001")
    private String batchCode;

    @Schema(description = "有效期")
    private LocalDateTime expireDate;

    @Schema(description = "批号", example = "LOT001")
    private String lotNumber;

    @Schema(description = "质检状态", example = "1")
    private Integer qualityStatus;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
