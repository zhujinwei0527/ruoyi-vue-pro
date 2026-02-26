package cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 领料出库单 Response VO")
@Data
public class MesWmProductionIssueRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "领料单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "ISSUE20250226001")
    private String code;

    @Schema(description = "领料单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "生产领料")
    private String name;

    @Schema(description = "工作站 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long workstationId;

    @Schema(description = "工作站名称", example = "装配工作站")
    private String workstationName;

    @Schema(description = "生产工单 ID", example = "1")
    private Long workorderId;

    @Schema(description = "生产工单编号", example = "WO20250226001")
    private String workorderCode;

    @Schema(description = "生产任务 ID", example = "1")
    private Long taskId;

    @Schema(description = "客户 ID", example = "1")
    private Long clientId;

    @Schema(description = "领料日期")
    private LocalDateTime issueDate;

    @Schema(description = "需求时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime requiredTime;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer status;

    @Schema(description = "备注", example = "备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
