package cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.defect;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 来料检验缺陷记录 Response VO")
@Data
public class MesQcIqcDefectRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "来料检验单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long iqcId;

    @Schema(description = "来料检验行 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
    private Long lineId;

    @Schema(description = "缺陷描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "表面划伤")
    private String defectName;

    @Schema(description = "缺陷等级", requiredMode = Schema.RequiredMode.REQUIRED, example = "CRITICAL")
    private String defectLevel;

    @Schema(description = "缺陷数量", example = "1")
    private Integer defectQuantity;

    @Schema(description = "备注", example = "备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
