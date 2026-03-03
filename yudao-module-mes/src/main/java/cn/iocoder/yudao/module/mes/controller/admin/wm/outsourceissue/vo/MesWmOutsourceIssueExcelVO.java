package cn.iocoder.yudao.module.mes.controller.admin.wm.outsourceissue.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 外协发料单 Excel VO")
@Data
@ExcelIgnoreUnannotated
public class MesWmOutsourceIssueExcelVO {

    @ExcelProperty("发料单ID")
    private Long id;

    @ExcelProperty("发料单编号")
    private String code;

    @ExcelProperty("发料单名称")
    private String name;

    @ExcelProperty("供应商编码")
    private String vendorCode;

    @ExcelProperty("供应商名称")
    private String vendorName;

    @ExcelProperty("生产工单编码")
    private String workorderCode;

    @ExcelProperty("生产工单名称")
    private String workorderName;

    @ExcelProperty("发料日期")
    private LocalDateTime issueDate;

    @ExcelProperty("单据状态")
    private Integer status;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
