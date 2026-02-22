package cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 领料申请单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MesWmMaterialRequestRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "工作站ID", example = "1")
    private Long workstationId;

    @Schema(description = "工作站编码", example = "WS0001")
    @ExcelProperty("工作站编码")
    private String workstationCode;

    @Schema(description = "工作站名称", example = "1#注塑工作站")
    @ExcelProperty("工作站名称")
    private String workstationName;

    @Schema(description = "生产工单ID", example = "1")
    private Long workOrderId;

    @Schema(description = "生产工单编码", example = "MO20260222001")
    @ExcelProperty("生产工单编码")
    private String workOrderCode;

    @Schema(description = "需求人用户ID", example = "1")
    private Long userId;

    @Schema(description = "需求人昵称", example = "管理员")
    @ExcelProperty("需求人昵称")
    private String userNickname;

    @Schema(description = "需求时间")
    @ExcelProperty("需求时间")
    private LocalDateTime requestTime;

    @Schema(description = "开始备料时间")
    @ExcelProperty("开始备料时间")
    private LocalDateTime startTime;

    @Schema(description = "完成时间")
    @ExcelProperty("完成时间")
    private LocalDateTime endTime;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "备注", example = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "预留字段1")
    private String attribute1;

    @Schema(description = "预留字段2")
    private String attribute2;

    @Schema(description = "预留字段3")
    private Integer attribute3;

    @Schema(description = "预留字段4")
    private Integer attribute4;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
