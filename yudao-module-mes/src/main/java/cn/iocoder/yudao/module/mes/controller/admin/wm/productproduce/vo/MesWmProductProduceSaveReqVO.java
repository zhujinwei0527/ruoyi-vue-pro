package cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 生产入库单新增/修改 Request VO")
@Data
public class MesWmProductProduceSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "生产工单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "生产工单 ID 不能为空")
    private Long workOrderId;

    @Schema(description = "报工单 ID", example = "1")
    private Long feedbackId;

    @Schema(description = "生产任务 ID", example = "1")
    private Long taskId;

    @Schema(description = "工作站 ID", example = "1")
    private Long workstationId;

    @Schema(description = "工序 ID", example = "1")
    private Long processId;

    @Schema(description = "入库日期")
    private LocalDateTime produceDate;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
