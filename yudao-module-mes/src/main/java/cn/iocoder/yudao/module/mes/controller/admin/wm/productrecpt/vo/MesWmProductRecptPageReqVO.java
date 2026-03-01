package cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - MES 产品收货单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesWmProductRecptPageReqVO extends PageParam {

    @Schema(description = "收货单编码", example = "PR202603010001")
    private String code;

    @Schema(description = "收货单名称", example = "产品收货单-工单001")
    private String name;

    @Schema(description = "生产工单编号", example = "1")
    private Long workOrderId;

    @Schema(description = "产品物料编号", example = "1")
    private Long itemId;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "收货日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] receiptDate;

}
