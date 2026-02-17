package cn.iocoder.yudao.module.mes.controller.admin.cal.plan.vo.team;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 计划班组关联 Response VO")
@Data
public class MesCalPlanTeamRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "排班计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long planId;

    @Schema(description = "班组编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long teamId;

    // TODO @芋艿：等 cal_team 迁移后，通过 teamId 关联查询填充
    @Schema(description = "班组编码", example = "T001")
    private String teamCode;

    // TODO @芋艿：等 cal_team 迁移后，通过 teamId 关联查询填充
    @Schema(description = "班组名称", example = "A组")
    private String teamName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "预留字段1")
    private String attribute1;

    @Schema(description = "预留字段2")
    private String attribute2;

    @Schema(description = "预留字段3")
    private Integer attribute3;

    @Schema(description = "预留字段4")
    private Integer attribute4;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
