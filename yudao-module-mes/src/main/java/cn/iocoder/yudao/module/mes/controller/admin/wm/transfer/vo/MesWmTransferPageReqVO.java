package cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - MES 转移单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesWmTransferPageReqVO extends PageParam {

    @Schema(description = "转移单编号", example = "TR2026020001")
    private String code;

    @Schema(description = "转移单名称", example = "钢板转移单")
    private String name;

    @Schema(description = "转移单类型", example = "1")
    private Integer type;

    @Schema(description = "转移日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] transferDate;

    @Schema(description = "单据状态", example = "0")
    private Integer status;

}
