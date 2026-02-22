package cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 领料申请单新增/修改 Request VO")
@Data
public class MesWmMaterialRequestSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "工作站ID", example = "1")
    private Long workstationId;

    @Schema(description = "生产工单ID", example = "1")
    private Long workOrderId;

    @Schema(description = "需求人用户ID", example = "1")
    private Long userId;

    @Schema(description = "需求时间")
    private LocalDateTime requestTime;

    @Schema(description = "开始备料时间")
    private LocalDateTime startTime;

    @Schema(description = "完成时间")
    private LocalDateTime endTime;

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
