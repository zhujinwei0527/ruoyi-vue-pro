package cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.line;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 采购入库单行分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesWmItemReceiptLinePageReqVO extends PageParam {

    @Schema(description = "入库单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long receiptId;

}
