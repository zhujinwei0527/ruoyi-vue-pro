package cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo;

import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.param.MesWmStockTakingPlanParamSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - MES 盘点方案生成任务 Request VO")
@Data
public class MesWmStockTakingPlanGenerateReqVO {

    @Schema(description = "方案编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "方案编号不能为空")
    private Long planId;

    @Schema(description = "任务编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "ST202603080001")
    @NotEmpty(message = "任务编码不能为空")
    private String code;

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "原料仓月度盘点任务")
    @NotEmpty(message = "任务名称不能为空")
    private String name;

    @Schema(description = "盘点日期")
    private LocalDateTime takingDate;

    @Schema(description = "盘点人 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "盘点人不能为空")
    private Long userId;

    @Schema(description = "备注", example = "备注")
    private String remark;

    @Schema(description = "方案参数列表，为空时使用方案自身参数")
    private List<MesWmStockTakingPlanParamSaveReqVO> params;

}
