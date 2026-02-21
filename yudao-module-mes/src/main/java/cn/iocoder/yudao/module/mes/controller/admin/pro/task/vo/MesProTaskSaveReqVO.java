package cn.iocoder.yudao.module.mes.controller.admin.pro.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 生产任务新增/修改 Request VO")
@Data
public class MesProTaskSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "任务编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "PT202503150001")
    @NotEmpty(message = "任务编码不能为空")
    private String code;

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "注塑任务")
    @NotEmpty(message = "任务名称不能为空")
    private String name;

    @Schema(description = "生产工单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "生产工单不能为空")
    private Long workOrderId;

    @Schema(description = "工作站编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "工作站不能为空")
    private Long workstationId;

    @Schema(description = "工艺路线编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "工艺路线不能为空")
    private Long routeId;

    @Schema(description = "工序编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "工序不能为空")
    private Long processId;

    @Schema(description = "产品物料编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "产品不能为空")
    private Long itemId;

    @Schema(description = "单位编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
    @NotNull(message = "单位不能为空")
    private Long unitMeasureId;

    @Schema(description = "排产数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    @NotNull(message = "排产数量不能为空")
    private BigDecimal quantity;

    @Schema(description = "已生产数量", example = "0")
    private BigDecimal producedQuantity;

    @Schema(description = "合格品数量", example = "0")
    private BigDecimal qualifyQuantity;

    @Schema(description = "不良品数量", example = "0")
    private BigDecimal unqualifyQuantity;

    @Schema(description = "调整数量", example = "0")
    private BigDecimal changedQuantity;

    @Schema(description = "客户编号", example = "300")
    private Long clientId;

    @Schema(description = "开始生产时间")
    private LocalDateTime startTime;

    @Schema(description = "生产时长（工作日）", example = "3")
    private Integer duration;

    @Schema(description = "结束生产时间")
    private LocalDateTime endTime;

    @Schema(description = "甘特图显示颜色", example = "#00AEF3")
    private String colorCode;

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
