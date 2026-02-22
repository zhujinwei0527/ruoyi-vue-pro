package cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.line;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - MES 领料申请单行分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MesWmMaterialRequestLinePageReqVO extends PageParam {

    @Schema(description = "领料申请单ID", example = "1")
    private Long materialRequestId;

}
