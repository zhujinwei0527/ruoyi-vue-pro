package cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - MES 检验结果 Response VO")
@Data
public class MesQcResultRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "样品编号", example = "SPL-001")
    private String code;

    @Schema(description = "关联质检单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long qcId;

    @Schema(description = "质检类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer qcType;

    @Schema(description = "产品物料ID", example = "1")
    private Long itemId;

    @Schema(description = "物资SN", example = "SN-001")
    private String sn;

    @Schema(description = "备注", example = "无")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    // ========== 关联查询字段 ==========

    @Schema(description = "质检单编号（关联查询）", example = "IQC-001")
    private String qcCode;

    @Schema(description = "质检单名称（关联查询）", example = "xxx来料检验")
    private String qcName;

    @Schema(description = "产品物料编码（关联查询）", example = "ITEM-001")
    private String itemCode;

    @Schema(description = "产品物料名称（关联查询）", example = "电阻")
    private String itemName;

    @Schema(description = "规格型号（关联查询）", example = "100Ω")
    private String itemSpecification;

    @Schema(description = "单位名称（关联查询）", example = "个")
    private String unitName;

    // ========== 子表：检验结果明细 ==========

    // TODO @AI：内嵌进来，不用队里类文件；
    @Schema(description = "检验结果明细列表")
    private List<MesQcResultDetailRespVO> items;

}
