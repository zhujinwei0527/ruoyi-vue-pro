package cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - MES 领料申请单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MesWmMaterialRequestPageReqVO extends PageParam {

    @Schema(description = "工作站ID", example = "1")
    private Long workstationId;

    @Schema(description = "生产工单ID", example = "1")
    private Long workOrderId;

    @Schema(description = "需求人用户ID", example = "1")
    private Long userId;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "需求时间范围")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] requestTime;

}
