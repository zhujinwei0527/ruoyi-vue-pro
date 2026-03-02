package cn.iocoder.yudao.module.mes.controller.admin.wm.productsales.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 销售出库单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MesWmProductSalesRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "出库单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "PS2026030001")
    @ExcelProperty("出库单号")
    private String code;

    @Schema(description = "出库单名称", example = "产品出库单")
    @ExcelProperty("出库单名称")
    private String name;

    @Schema(description = "客户ID", example = "1")
    private Long clientId;

    @Schema(description = "客户名称", example = "某客户")
    @ExcelProperty("客户")
    private String clientName;

    @Schema(description = "销售订单号", example = "SO2026030001")
    @ExcelProperty("销售订单号")
    private String salesOrderCode;

    @Schema(description = "计划发货日期")
    @ExcelProperty("计划发货日期")
    private LocalDateTime shipmentDate;

    @Schema(description = "联系人", example = "张三")
    @ExcelProperty("联系人")
    private String contactName;

    @Schema(description = "联系电话", example = "13800138000")
    @ExcelProperty("联系电话")
    private String contactTelephone;

    @Schema(description = "收货地址", example = "北京市朝阳区xxx")
    @ExcelProperty("收货地址")
    private String contactAddress;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "备注", example = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
