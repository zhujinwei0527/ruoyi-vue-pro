package cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.area;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 库位分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesWmWarehouseAreaPageReqVO extends PageParam {

    @Schema(description = "库位编码", example = "A001")
    private String code;

    @Schema(description = "库位名称", example = "默认库位")
    private String name;

    @Schema(description = "库区编号", example = "1")
    private Long locationId;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "是否冻结", example = "false")
    private Boolean frozen;

}
