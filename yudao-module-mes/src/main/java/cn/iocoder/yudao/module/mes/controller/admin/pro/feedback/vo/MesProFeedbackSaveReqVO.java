package cn.iocoder.yudao.module.mes.controller.admin.pro.feedback.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 生产报工新增/修改 Request VO")
@Data
public class MesProFeedbackSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "报工类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "报工类型不能为空")
    private Integer type;

    @Schema(description = "报工途径", example = "PC")
    private String channel;

    @Schema(description = "工作站编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "工作站不能为空")
    private Long workstationId;

    @Schema(description = "工艺路线编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "工艺路线不能为空")
    private Long routeId;

    @Schema(description = "工序编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "工序不能为空")
    private Long processId;

    @Schema(description = "生产工单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "生产工单不能为空")
    private Long workOrderId;

    @Schema(description = "生产任务编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "生产任务不能为空")
    private Long taskId;

    @Schema(description = "产品物料编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "75")
    @NotNull(message = "产品物料不能为空")
    private Long itemId;

    @Schema(description = "单位编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "202")
    @NotNull(message = "单位不能为空")
    private Long unitMeasureId;

    @Schema(description = "过期日期")
    private LocalDateTime expireDate;

    @Schema(description = "批次号", example = "B20250315001")
    private String batchCode;

    @Schema(description = "排产数量", example = "5000.00")
    private BigDecimal scheduledQuantity;

    @Schema(description = "本次报工数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "500.00")
    @NotNull(message = "报工数量不能为空")
    private BigDecimal feedbackQuantity;

    @Schema(description = "合格品数量", example = "490.00")
    private BigDecimal qualifiedQuantity;

    @Schema(description = "不良品数量", example = "10.00")
    private BigDecimal unqualifiedQuantity;

    @Schema(description = "待检测数量", example = "0")
    private BigDecimal uncheckQuantity;

    @Schema(description = "工废数量", example = "6.00")
    private BigDecimal laborScrapQuantity;

    @Schema(description = "料废数量", example = "4.00")
    private BigDecimal materialScrapQuantity;

    @Schema(description = "其他废品数量", example = "0")
    private BigDecimal otherScrapQuantity;

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
