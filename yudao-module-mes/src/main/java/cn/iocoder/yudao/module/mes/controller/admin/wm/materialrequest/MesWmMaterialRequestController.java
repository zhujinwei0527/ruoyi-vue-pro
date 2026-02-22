package cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.MesWmMaterialRequestPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.MesWmMaterialRequestRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.MesWmMaterialRequestSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialrequest.MesWmMaterialRequestDO;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import cn.iocoder.yudao.module.mes.service.wm.materialrequest.MesWmMaterialRequestService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.MapUtils.findAndThen;

@Tag(name = "管理后台 - MES 领料申请单")
@RestController
@RequestMapping("/mes/wm/material-request")
@Validated
public class MesWmMaterialRequestController {

    @Resource
    private MesWmMaterialRequestService materialRequestService;
    @Resource
    private MesMdWorkstationService workstationService;
    @Resource
    private MesProWorkOrderService workOrderService;
    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/create")
    @Operation(summary = "创建领料申请单")
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:create')")
    public CommonResult<Long> createMaterialRequest(@Valid @RequestBody MesWmMaterialRequestSaveReqVO createReqVO) {
        return success(materialRequestService.createMaterialRequest(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改领料申请单")
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:update')")
    public CommonResult<Boolean> updateMaterialRequest(@Valid @RequestBody MesWmMaterialRequestSaveReqVO updateReqVO) {
        materialRequestService.updateMaterialRequest(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除领料申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:delete')")
    public CommonResult<Boolean> deleteMaterialRequest(@RequestParam("id") Long id) {
        materialRequestService.deleteMaterialRequest(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得领料申请单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:query')")
    public CommonResult<MesWmMaterialRequestRespVO> getMaterialRequest(@RequestParam("id") Long id) {
        MesWmMaterialRequestDO materialRequest = materialRequestService.getMaterialRequest(id);
        if (materialRequest == null) {
            return success(null);
        }
        return success(buildRespVOList(Collections.singletonList(materialRequest)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得领料申请单分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:query')")
    public CommonResult<PageResult<MesWmMaterialRequestRespVO>> getMaterialRequestPage(
            @Valid MesWmMaterialRequestPageReqVO pageReqVO) {
        PageResult<MesWmMaterialRequestDO> pageResult = materialRequestService.getMaterialRequestPage(pageReqVO);
        return success(new PageResult<>(buildRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出领料申请单 Excel")
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMaterialRequestExcel(@Valid MesWmMaterialRequestPageReqVO pageReqVO,
                                           HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<MesWmMaterialRequestDO> pageResult = materialRequestService.getMaterialRequestPage(pageReqVO);
        ExcelUtils.write(response, "领料申请单.xls", "数据", MesWmMaterialRequestRespVO.class,
                buildRespVOList(pageResult.getList()));
    }

    @PutMapping("/submit")
    @Operation(summary = "提交领料申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:update')")
    public CommonResult<Boolean> submitMaterialRequest(@RequestParam("id") Long id) {
        materialRequestService.submitMaterialRequest(id);
        return success(true);
    }

    @PutMapping("/approve")
    @Operation(summary = "审批领料申请单（开始备料）")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:update')")
    public CommonResult<Boolean> approveMaterialRequest(@RequestParam("id") Long id) {
        materialRequestService.approveMaterialRequest(id);
        return success(true);
    }

    @PutMapping("/finish")
    @Operation(summary = "完成领料申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:update')")
    public CommonResult<Boolean> finishMaterialRequest(@RequestParam("id") Long id) {
        materialRequestService.finishMaterialRequest(id);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消领料申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:update')")
    public CommonResult<Boolean> cancelMaterialRequest(@RequestParam("id") Long id) {
        materialRequestService.cancelMaterialRequest(id);
        return success(true);
    }

    // ==================== 拼接 VO ====================

    private List<MesWmMaterialRequestRespVO> buildRespVOList(List<MesWmMaterialRequestDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 获得关联数据
        Map<Long, MesMdWorkstationDO> workstationMap = workstationService.getWorkstationMap(
                convertSet(list, MesWmMaterialRequestDO::getWorkstationId));
        Map<Long, MesProWorkOrderDO> workOrderMap = workOrderService.getWorkOrderMap(
                convertSet(list, MesWmMaterialRequestDO::getWorkOrderId));
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(
                convertSet(list, MesWmMaterialRequestDO::getUserId));
        // 2. 构建结果
        return BeanUtils.toBean(list, MesWmMaterialRequestRespVO.class, vo -> {
            findAndThen(workstationMap, vo.getWorkstationId(), ws ->
                    vo.setWorkstationCode(ws.getCode()).setWorkstationName(ws.getName()));
            findAndThen(workOrderMap, vo.getWorkOrderId(), wo ->
                    vo.setWorkOrderCode(wo.getCode()));
            findAndThen(userMap, vo.getUserId(), user ->
                    vo.setUserNickname(user.getNickname()));
        });
    }

}
