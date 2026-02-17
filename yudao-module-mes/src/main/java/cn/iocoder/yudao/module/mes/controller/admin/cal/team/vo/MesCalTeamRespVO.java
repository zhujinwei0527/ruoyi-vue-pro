package cn.iocoder.yudao.module.mes.controller.admin.cal.team.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 班组 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MesCalTeamRespVO {

    @Schema(description = "班组编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("班组编号")
    private Long id;

    @Schema(description = "班组编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "TEAM-A")
    @ExcelProperty("班组编码")
    private String code;

    @Schema(description = "班组名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "注塑A组")
    @ExcelProperty("班组名称")
    private String name;

    @Schema(description = "班组类型", example = "1")
    @ExcelProperty("班组类型")
    private Integer calendarType;

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
