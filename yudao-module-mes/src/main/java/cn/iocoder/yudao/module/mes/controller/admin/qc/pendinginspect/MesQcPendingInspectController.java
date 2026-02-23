package cn.iocoder.yudao.module.mes.controller.admin.qc.pendinginspect;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.qc.pendinginspect.vo.MesQcPendingInspectPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.pendinginspect.vo.MesQcPendingInspectRespVO;
import cn.iocoder.yudao.module.mes.service.qc.pendinginspect.MesQcPendingInspectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 待检任务")
@RestController
@RequestMapping("/mes/qc/pending-inspect")
@Validated
public class MesQcPendingInspectController {

    @Resource
    private MesQcPendingInspectService pendingInspectService;

    @GetMapping("/page")
    @Operation(summary = "获得待检任务分页")
    @PreAuthorize("@ss.hasPermission('mes:qc-pending-inspect:query')")
    public CommonResult<PageResult<MesQcPendingInspectRespVO>> getPendingInspectPage(
            @Valid MesQcPendingInspectPageReqVO pageReqVO) {
        return success(pendingInspectService.getPendingInspectPage(pageReqVO));
    }

}
