package cn.iocoder.yudao.module.mes.controller.admin.cal.team.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - MES 班组成员新增 Request VO")
@Data
public class MesCalTeamMemberSaveReqVO {

    @Schema(description = "班组成员编号", example = "1024")
    private Long id;

    @Schema(description = "班组编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "201")
    @NotNull(message = "班组编号不能为空")
    private Long teamId;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

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

}
