package cn.iocoder.yudao.module.mes.controller.admin.pro.andon.vo.record;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安灯呼叫记录处置 Request VO")
@Data
public class MesProAndonRecordHandleReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "编号不能为空")
    private Long id;

    @Schema(description = "处置时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "处置时间不能为空")
    private LocalDateTime handleTime;

    @Schema(description = "处置人编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "处置人不能为空")
    private Long handlerUserId;

    @Schema(description = "备注", example = "已修复")
    private String remark;

}
