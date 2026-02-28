package cn.iocoder.yudao.module.mes.controller.admin.wm.returnvendor.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - MES 供应商退货单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MesWmReturnVendorPageReqVO extends PageParam {

    @Schema(description = "退货单编号", example = "RV20250226001")
    private String code;

    @Schema(description = "退货单名称", example = "供应商退货")
    private String name;

    @Schema(description = "供应商 ID", example = "1")
    private Long vendorId;

    @Schema(description = "状态", example = "10")
    private Integer status;

    @Schema(description = "退货日期范围")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] returnDate;

}
