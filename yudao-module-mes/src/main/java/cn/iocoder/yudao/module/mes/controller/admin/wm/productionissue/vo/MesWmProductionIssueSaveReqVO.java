package cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - MES 领料出库单新增/修改 Request VO")
@Data
public class MesWmProductionIssueSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    // TODO @AI：必须传递
    @Schema(description = "领料单编号", example = "ISSUE20250226001")
    private String code;

    // TODO @AI：必须传递
    @Schema(description = "领料单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "生产领料")
    private String name;

    // TODO @AI：必须传递
    // TODO @AI：service 需要校验存在
    @Schema(description = "工作站 ID", example = "1")
    private Long workstationId;

    @Schema(description = "生产工单 ID", example = "1")
    // TODO @AI：service 需要校验存在
    private Long workorderId;

    @Schema(description = "需求时间")
    // TODO @AI：必须传递
    private LocalDateTime requiredTime;

    @Schema(description = "备注", example = "备注")
    private String remark;

    @Schema(description = "预留字段1")
    private String attribute1;

    @Schema(description = "预留字段2")
    private String attribute2;

    @Schema(description = "预留字段3")
    private Integer attribute3;

    @Schema(description = "预留字段4")
    private Integer attribute4;

    // TODO @AI：应该单独接口，传递这个；参考别的模块；
    @Schema(description = "领料单行列表")
    private List<MesWmProductionIssueLineSaveReqVO> lines;

}
