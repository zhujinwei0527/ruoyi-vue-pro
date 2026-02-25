package cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.line;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 采购入库单行新增/修改 Request VO")
@Data
public class MesWmItemReceiptLineSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "入库单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "入库单编号不能为空")
    private Long receiptId;

    @Schema(description = "到货通知单行编号", example = "1")
    private Long noticeLineId;

    @Schema(description = "物料编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "物料编号不能为空")
    private Long itemId;

    @Schema(description = "入库数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "500.00")
    @NotNull(message = "入库数量不能为空")
    private BigDecimal receivedQuantity;

    // TODO @芋艿：【暂时不调整】这个换成 batchCode；无论是前端、后端；数据库还是 batchId；后续在调整；
    @Schema(description = "批次编号", example = "1")
    private Long batchId;

    // TODO @AI：是不是这里不用 warehouseId、locationId、areaId
    @Schema(description = "仓库编号", example = "1")
    private Long warehouseId;

    @Schema(description = "库区编号", example = "1")
    private Long locationId;

    @Schema(description = "库位编号", example = "1")
    private Long areaId;

    @Schema(description = "生产日期")
    private LocalDateTime productionDate;

    @Schema(description = "有效期")
    private LocalDateTime expireDate;

    @Schema(description = "生产批号", example = "PB20260110")
    private String productionBatchNumber;

    // TODO @AI：这个字段去掉；
    @Schema(description = "是否需要来料检验", example = "true")
    private Boolean iqcCheckFlag;

    // TODO @AI：这个字段去掉；不是这里保存出来的；你也思考下；
    @Schema(description = "来料检验单编号", example = "1")
    private Long iqcId;

    @Schema(description = "备注", example = "备注")
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
