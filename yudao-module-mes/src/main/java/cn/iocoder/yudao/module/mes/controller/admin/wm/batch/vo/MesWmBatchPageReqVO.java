package cn.iocoder.yudao.module.mes.controller.admin.wm.batch.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 批次管理分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesWmBatchPageReqVO extends PageParam {

    @Schema(description = "批次编码", example = "BATCH20250314001")
    private String code;

    @Schema(description = "物料编号", example = "1")
    private Long itemId;

    @Schema(description = "供应商编号", example = "1")
    private Long vendorId;

    @Schema(description = "客户编号", example = "1")
    private Long clientId;

    @Schema(description = "销售订单编号", example = "SO20250314001")
    private String salesOrderCode;

    @Schema(description = "采购订单编号", example = "PO20250314001")
    private String purchaseOrderCode;

    @Schema(description = "生产工单编号", example = "1")
    private Long workOrderId;

}