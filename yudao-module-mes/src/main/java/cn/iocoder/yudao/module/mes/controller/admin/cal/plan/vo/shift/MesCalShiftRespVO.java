package cn.iocoder.yudao.module.mes.controller.admin.cal.plan.vo.shift;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 计划班次 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MesCalShiftRespVO {

    @Schema(description = "班次编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("班次编号")
    private Long id;

    @Schema(description = "排班计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("排班计划编号")
    private Long planId;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("显示顺序")
    private Integer sort;

    @Schema(description = "班次名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "白班")
    @ExcelProperty("班次名称")
    private String name;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "08:00")
    @ExcelProperty("开始时间")
    private String startTime;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "17:00")
    @ExcelProperty("结束时间")
    private String endTime;

    @Schema(description = "备注")
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
