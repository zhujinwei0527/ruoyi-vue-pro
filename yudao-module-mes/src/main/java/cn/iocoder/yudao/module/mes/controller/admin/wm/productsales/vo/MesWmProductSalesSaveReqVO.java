package cn.iocoder.yudao.module.mes.controller.admin.wm.productsales.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 销售出库单新增/修改 Request VO")
@Data
public class MesWmProductSalesSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "出库单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "PS2026030001")
    @NotEmpty(message = "出库单号不能为空")
    private String code;

    @Schema(description = "出库单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "产品出库单")
    @NotEmpty(message = "出库单名称不能为空")
    private String name;

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "客户不能为空")
    private Long clientId;

    @Schema(description = "销售订单号", example = "SO2026030001")
    private String salesOrderCode;

    @Schema(description = "计划发货日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "计划发货日期不能为空")
    private LocalDateTime shipmentDate;

    @Schema(description = "联系人", example = "张三")
    private String contactName;

    @Schema(description = "联系电话", example = "13800138000")
    private String contactTelephone;

    @Schema(description = "收货地址", example = "北京市朝阳区xxx")
    private String contactAddress;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
