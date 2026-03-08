package cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - MES 调拨明细 Save Request VO")
@Data
public class MesWmTransferDetailSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "转移单行编号", example = "1")
    private Long lineId;

    @Schema(description = "转移单编号", example = "1")
    private Long transferId;

    @Schema(description = "物料编号", example = "1")
    private Long itemId;

    @Schema(description = "上架数量", example = "100")
    private BigDecimal quantity;

    @Schema(description = "批次编号", example = "1")
    private Long batchId;

    @Schema(description = "移入仓库编号", example = "1")
    private Long toWarehouseId;

    @Schema(description = "移入库区编号", example = "1")
    private Long toLocationId;

    @Schema(description = "移入库位编号", example = "1")
    private Long toAreaId;

    @Schema(description = "备注", example = "备注信息")
    private String remark;

}
