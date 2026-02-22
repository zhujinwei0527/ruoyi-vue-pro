package cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.detail;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 采购入库明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesWmItemReceiptDetailPageReqVO extends PageParam {

    @Schema(description = "入库单编号", example = "1")
    private Long receiptId;

    @Schema(description = "入库单行编号", example = "1")
    private Long lineId;

}
