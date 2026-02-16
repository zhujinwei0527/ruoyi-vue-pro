package cn.iocoder.yudao.module.mes.controller.admin.cal.holiday.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.module.mes.enums.DictTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 假期设置 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MesCalHolidayRespVO {

    @Schema(description = "编号")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "日期")
    @ExcelProperty("日期")
    private LocalDateTime theDay;

    @Schema(description = "日期类型")
    @ExcelProperty(value = "日期类型", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.MES_CAL_HOLIDAY_TYPE)
    private String type;

    @Schema(description = "开始时间")
    @ExcelProperty("开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @ExcelProperty("结束时间")
    private LocalDateTime endTime;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
