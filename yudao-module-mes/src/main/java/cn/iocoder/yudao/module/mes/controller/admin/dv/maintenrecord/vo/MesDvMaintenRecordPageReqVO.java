package cn.iocoder.yudao.module.mes.controller.admin.dv.maintenrecord.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - MES 设备保养记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesDvMaintenRecordPageReqVO extends PageParam {

    @Schema(description = "计划名称", example = "计划1")
    private String planName;

    @Schema(description = "设备编码", example = "M1")
    private String machineryCode;

    @Schema(description = "设备名称", example = "设备1")
    private String machineryName;

    @Schema(description = "保养人名称", example = "张三")
    private String nickname;

    @Schema(description = "保养时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] maintenTime;

}
