package cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.defect;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - MES 来料检验缺陷记录新增/修改 Request VO")
@Data
public class MesQcIqcDefectSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "来料检验单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "来料检验单 ID 不能为空")
    private Long iqcId;

    @Schema(description = "来料检验行 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
    @NotNull(message = "来料检验行 ID 不能为空")
    private Long lineId;

    @Schema(description = "缺陷描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "表面划伤")
    @NotEmpty(message = "缺陷描述不能为空")
    private String defectName;

    @Schema(description = "缺陷等级", requiredMode = Schema.RequiredMode.REQUIRED, example = "CRITICAL")
    @NotEmpty(message = "缺陷等级不能为空")
    private String defectLevel;

    @Schema(description = "缺陷数量", example = "1")
    private Integer defectQuantity;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
