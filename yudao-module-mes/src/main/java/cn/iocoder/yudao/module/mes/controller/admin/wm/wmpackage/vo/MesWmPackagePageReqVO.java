package cn.iocoder.yudao.module.mes.controller.admin.wm.wmpackage.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - MES 装箱单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesWmPackagePageReqVO extends PageParam {

    @Schema(description = "装箱单编号", example = "PKG")
    private String code;

    @Schema(description = "销售订单编号", example = "SO")
    private String soCode;

    @Schema(description = "客户 ID", example = "1")
    private Long clientId;

    @Schema(description = "装箱日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] packageDate;

    @Schema(description = "状态", example = "0")
    private Integer status;

}
