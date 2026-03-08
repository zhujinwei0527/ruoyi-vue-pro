package cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "管理后台 - MES 转移单 Save Request VO")
@Data
public class MesWmTransferSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    // TODO @AI：validator 参数校验需要添加
    @Schema(description = "转移单编号", requiredMode = REQUIRED, example = "TR2026020001")
    private String code;

    @Schema(description = "转移单名称", requiredMode = REQUIRED, example = "钢板转移单")
    private String name;

    @Schema(description = "转移单类型", requiredMode = REQUIRED, example = "1")
    private Integer type;

    @Schema(description = "是否配送", requiredMode = REQUIRED, example = "true")
    private Boolean deliveryFlag;

    @Schema(description = "收货人", example = "张三")
    private String recipientName;

    @Schema(description = "联系方式", example = "13800138000")
    private String recipientTelephone;

    @Schema(description = "目的地", example = "上海市浦东新区")
    private String destinationAddress;

    @Schema(description = "承运商", example = "顺丰速运")
    private String carrier;

    @Schema(description = "运输单号", example = "SF123456789")
    private String shippingNumber;

    @Schema(description = "转移日期", requiredMode = REQUIRED)
    private LocalDateTime transferDate;

    @Schema(description = "备注", example = "备注信息")
    private String remark;

}
