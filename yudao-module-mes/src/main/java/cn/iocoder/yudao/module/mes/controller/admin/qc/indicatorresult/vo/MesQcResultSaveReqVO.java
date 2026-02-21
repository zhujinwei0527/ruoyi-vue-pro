package cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - MES 检验结果创建/更新 Request VO")
@Data
public class MesQcResultSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "样品编号", example = "SPL-001")
    private String code;

    @Schema(description = "关联质检单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "关联质检单ID不能为空")
    private Long qcId;

    @Schema(description = "质检类型（1=IQC 2=IPQC 3=OQC 4=RQC）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "质检类型不能为空")
    private Integer qcType;

    @Schema(description = "物资SN", example = "SN-001")
    private String sn;

    @Schema(description = "备注", example = "无")
    private String remark;

    // TODO @AI：MesQcResultDetailSaveReqVO 内嵌进来；不用独立类；
    @Schema(description = "检验结果明细列表")
    private List<MesQcResultDetailSaveReqVO> items;

}
