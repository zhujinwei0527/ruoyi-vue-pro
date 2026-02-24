package cn.iocoder.yudao.module.mes.controller.admin.qc.oqc.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 出货检验单新增/修改 Request VO")
@Data
public class MesQcOqcSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "检验单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "OQC20250101001")
    @NotEmpty(message = "检验单编号不能为空")
    private String code;

    @Schema(description = "检验单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "物料A出货检验")
    @NotEmpty(message = "检验单名称不能为空")
    private String name;

    @Schema(description = "检验模板 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "检验模板不能为空")
    private Long templateId;

    // ========== 来源单据 ==========

    @Schema(description = "来源单据 ID", example = "200")
    private Long sourceDocId;

    @Schema(description = "来源单据类型", example = "SALES")
    private String sourceDocType;

    @Schema(description = "来源单据编号", example = "SO20250101001")
    private String sourceDocCode;

    @Schema(description = "来源单据行 ID", example = "300")
    private Long sourceLineId;

    // ========== 客户 ==========

    @Schema(description = "客户 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "客户不能为空")
    private Long clientId;

    @Schema(description = "批次号", example = "BC20250101")
    private String batchCode;

    // ========== 物料 ==========

    @Schema(description = "产品物料 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    @NotNull(message = "产品物料不能为空")
    private Long itemId;

    // ========== 数量 ==========

    // TODO @AI：这 2 个字段去掉，minCheckQuantity、maxUnqualifiedQuantity；前后端都是；
    @Schema(description = "最低检测数", example = "5")
    private Integer minCheckQuantity;

    @Schema(description = "最大不合格数", example = "0")
    private Integer maxUnqualifiedQuantity;

    @Schema(description = "本次出货数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "本次出货数量不能为空")
    private BigDecimal outQuantity;

    // TODO @AI：checkQuantity = qualifiedQuantity + unqualifiedQuantity；校验下相等；必须的；参考 iqc

    @Schema(description = "本次检测数量", example = "10")
    private Integer checkQuantity;

    @Schema(description = "合格品数量", example = "9")
    private Integer qualifiedQuantity;

    @Schema(description = "不合格品数量", example = "1")
    private Integer unqualifiedQuantity;

    // ========== 检验 ==========

    @Schema(description = "检测结果", example = "1")
    private Integer checkResult;

    @Schema(description = "出货日期")
    private LocalDateTime outDate;

    @Schema(description = "检测日期")
    private LocalDateTime inspectDate;

    @Schema(description = "检测人员用户 ID", example = "1")
    private Long inspectorUserId;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
