package cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.bom;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 生产工单 BOM Response VO")
@Data
@ExcelIgnoreUnannotated
public class MesProWorkorderBomRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "生产工单编号", example = "100")
    private Long workorderId;

    @Schema(description = "BOM 物料编号", example = "200")
    private Long itemId;

    @Schema(description = "物料名称", example = "电阻 R1")
    @ExcelProperty("物料名称")
    private String itemName;

    @Schema(description = "物料编码", example = "M-001")
    @ExcelProperty("物料编码")
    private String itemCode;

    @Schema(description = "规格型号", example = "10K 0603")
    @ExcelProperty("规格型号")
    private String itemSpec;

    @Schema(description = "单位编号", example = "300")
    private Long unitMeasureId;

    @Schema(description = "单位名称", example = "个")
    @ExcelProperty("单位")
    private String unitMeasureName;

    @Schema(description = "物料产品标识", example = "ITEM")
    @ExcelProperty("物料产品标识")
    private String itemOrProduct;

    @Schema(description = "预计使用量", example = "10.00")
    @ExcelProperty("预计使用量")
    private BigDecimal quantity;

    @Schema(description = "备注", example = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "预留字段1")
    private String attribute1;

    @Schema(description = "预留字段2")
    private String attribute2;

    @Schema(description = "预留字段3")
    private Integer attribute3;

    @Schema(description = "预留字段4")
    private Integer attribute4;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
