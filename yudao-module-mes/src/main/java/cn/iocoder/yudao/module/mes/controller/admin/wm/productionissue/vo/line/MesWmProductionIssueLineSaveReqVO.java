package cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.line;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - MES 领料出库单行 Save Request VO")
@Data
public class MesWmProductionIssueLineSaveReqVO {

    @Schema(description = "行ID", example = "1")
    private Long id;

    @Schema(description = "物料ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long itemId;

    @Schema(description = "领料数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    private BigDecimal quantity;

    @Schema(description = "批次ID", example = "1")
    private Long batchId;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
