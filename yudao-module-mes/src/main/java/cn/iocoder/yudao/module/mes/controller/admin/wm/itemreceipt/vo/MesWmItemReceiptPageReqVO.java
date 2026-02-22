package cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 采购入库单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesWmItemReceiptPageReqVO extends PageParam {

    @Schema(description = "入库单编码", example = "IR2026020001")
    private String code;

    @Schema(description = "入库单名称", example = "钢板入库单")
    private String name;

    @Schema(description = "采购订单编号", example = "PO20260101")
    private String purchaseOrderCode;

    @Schema(description = "供应商编号", example = "1")
    private Long vendorId;

    @Schema(description = "状态", example = "0")
    private Integer status;

}
