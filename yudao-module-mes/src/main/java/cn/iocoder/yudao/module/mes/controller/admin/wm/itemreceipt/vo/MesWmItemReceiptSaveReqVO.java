package cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 采购入库单新增/修改 Request VO")
@Data
public class MesWmItemReceiptSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "入库单编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "IR2026020001")
    @NotEmpty(message = "入库单编码不能为空")
    private String code;

    @Schema(description = "入库单名称", example = "钢板入库单")
    private String name;

    @Schema(description = "来料检验单编号", example = "1")
    private Long iqcId;

    @Schema(description = "到货通知单编号", example = "1")
    private Long noticeId;

    @Schema(description = "采购订单编号", example = "PO20260101")
    private String purchaseOrderCode;

    @Schema(description = "供应商编号", example = "1")
    private Long vendorId;

    @Schema(description = "仓库编号", example = "1")
    private Long warehouseId;

    @Schema(description = "库区编号", example = "1")
    private Long locationId;

    @Schema(description = "库位编号", example = "1")
    private Long areaId;

    @Schema(description = "入库日期")
    private LocalDateTime receiptDate;

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
