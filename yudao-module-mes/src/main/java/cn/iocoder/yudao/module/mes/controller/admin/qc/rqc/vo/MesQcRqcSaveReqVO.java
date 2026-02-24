package cn.iocoder.yudao.module.mes.controller.admin.qc.rqc.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 退货检验单新增/修改 Request VO")
@Data
public class MesQcRqcSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "检验单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "RQC20250101001")
    @NotEmpty(message = "检验单编号不能为空")
    private String code;

    @Schema(description = "检验单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "物料A退货检验")
    @NotEmpty(message = "检验单名称不能为空")
    private String name;

    @Schema(description = "检验模板 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "检验模板不能为空")
    private Long templateId;

    // ========== 来源单据 ==========

    @Schema(description = "来源单据 ID", example = "200")
    private Long sourceDocId;

    @Schema(description = "来源单据类型", example = "RTISSUE")
    private String sourceDocType;

    @Schema(description = "来源单据编号", example = "RT20250101001")
    private String sourceDocCode;

    @Schema(description = "来源单据行 ID", example = "300")
    private Long sourceLineId;

    // ========== 检验类型 ==========

    @Schema(description = "检验类型", example = "1")
    private Integer rqcType;

    // ========== 物料 ==========

    @Schema(description = "产品物料 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    @NotNull(message = "产品物料不能为空")
    private Long itemId;

    @Schema(description = "批次号", example = "BATCH001")
    private String batchCode;

    // ========== 数量 ==========

    // TODO @AI：参考 iqc 的参数校验；

    @Schema(description = "检测数量", example = "100")
    private BigDecimal checkQuantity;

    @Schema(description = "合格品数量", example = "90")
    private BigDecimal qualifiedQuantity;

    @Schema(description = "不合格数量", example = "10")
    private BigDecimal unqualifiedQuantity;

    // ========== 检验 ==========

    @Schema(description = "检测结果", example = "1")
    private Integer checkResult;

    @Schema(description = "检测日期")
    private LocalDateTime inspectDate;

    @Schema(description = "检测人员用户 ID", example = "1")
    private Long inspectorUserId;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
