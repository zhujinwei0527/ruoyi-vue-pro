package cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.line;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - MES 转移单行 Save Request VO")
@Data
public class MesWmTransferLineSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "转移单编号", example = "1")
    private Long transferId;

    @Schema(description = "库存记录编号", example = "1")
    private Long materialStockId;

    @Schema(description = "物料编号", example = "1")
    private Long itemId;

    @Schema(description = "转移数量", example = "100")
    private BigDecimal quantity;

    @Schema(description = "批次编号", example = "1")
    private Long batchId;

    @Schema(description = "移出仓库编号", example = "1")
    private Long fromWarehouseId;

    @Schema(description = "移出库区编号", example = "1")
    private Long fromLocationId;

    @Schema(description = "移出库位编号", example = "1")
    private Long fromAreaId;

    @Schema(description = "备注", example = "备注信息")
    private String remark;

}
