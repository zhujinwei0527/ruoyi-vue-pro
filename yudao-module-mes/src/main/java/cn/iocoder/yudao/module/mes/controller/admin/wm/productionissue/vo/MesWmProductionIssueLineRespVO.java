package cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 领料出库单行 Response VO")
@Data
public class MesWmProductionIssueLineRespVO {

    @Schema(description = "行ID", example = "1")
    private Long id;

    @Schema(description = "领料单ID", example = "1")
    private Long issueId;

    @Schema(description = "物料ID", example = "1")
    private Long itemId;

    @Schema(description = "领料数量", example = "100.00")
    private BigDecimal quantityIssued;

    @Schema(description = "批次ID", example = "1")
    private Long batchId;

    @Schema(description = "备注", example = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
