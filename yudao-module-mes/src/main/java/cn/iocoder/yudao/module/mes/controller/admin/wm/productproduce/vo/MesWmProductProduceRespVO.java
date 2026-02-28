package cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 生产入库单 Response VO")
@Data
public class MesWmProductProduceRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "生产工单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long workOrderId;

    @Schema(description = "生产工单编号", example = "WO20250226001")
    private String workOrderCode;

    @Schema(description = "报工单 ID", example = "1")
    private Long feedbackId;

    @Schema(description = "生产任务 ID", example = "1")
    private Long taskId;

    @Schema(description = "生产任务编号", example = "TASK20250226001")
    private String taskCode;

    @Schema(description = "工作站 ID", example = "1")
    private Long workstationId;

    @Schema(description = "工作站名称", example = "装配工作站")
    private String workstationName;

    @Schema(description = "工序 ID", example = "1")
    private Long processId;

    @Schema(description = "工序名称", example = "组装工序")
    private String processName;

    @Schema(description = "入库日期")
    private LocalDateTime produceDate;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer status;

    @Schema(description = "备注", example = "备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
