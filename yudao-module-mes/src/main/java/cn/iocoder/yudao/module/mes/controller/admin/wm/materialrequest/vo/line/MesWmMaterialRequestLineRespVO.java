package cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.line;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 领料申请单行 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MesWmMaterialRequestLineRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "领料申请单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long materialRequestId;

    @Schema(description = "产品物料ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long itemId;

    @Schema(description = "产品物料编码", example = "IF2022082432")
    @ExcelProperty("物料编码")
    private String itemCode;

    @Schema(description = "产品物料名称", example = "PVC 颗粒")
    @ExcelProperty("物料名称")
    private String itemName;

    @Schema(description = "规格型号", example = "透明")
    @ExcelProperty("规格型号")
    private String specification;

    @Schema(description = "计量单位ID", example = "1")
    private Long unitMeasureId;

    @Schema(description = "计量单位名称", example = "公斤")
    @ExcelProperty("计量单位")
    private String unitMeasureName;

    @Schema(description = "需求数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10.00")
    @ExcelProperty("需求数量")
    private BigDecimal quantity;

    @Schema(description = "备注", example = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
