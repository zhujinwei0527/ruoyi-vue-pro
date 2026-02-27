package cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssuePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssueRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssueSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDO;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import cn.iocoder.yudao.module.mes.service.wm.productionissue.MesWmProductionIssueService;
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

@Tag(name = "管理后台 - MES 领料出库单")
@RestController
@RequestMapping("/mes/wm/production-issue")
@Validated
public class MesWmProductionIssueController {

    @Resource
    private MesWmProductionIssueService issueService;
    @Resource
    private MesMdWorkstationService workstationService;
    @Resource
    private MesProWorkOrderService workOrderService;

    @PostMapping("/create")
    @Operation(summary = "创建领料出库单")
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:create')")
    public CommonResult<Long> createProductionIssue(@Valid @RequestBody MesWmProductionIssueSaveReqVO createReqVO) {
        return success(issueService.createProductionIssue(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改领料出库单")
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:update')")
    public CommonResult<Boolean> updateProductionIssue(@Valid @RequestBody MesWmProductionIssueSaveReqVO updateReqVO) {
        issueService.updateProductionIssue(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除领料出库单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:delete')")
    public CommonResult<Boolean> deleteProductionIssue(@RequestParam("id") Long id) {
        issueService.deleteProductionIssue(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得领料出库单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:query')")
    public CommonResult<MesWmProductionIssueRespVO> getProductionIssue(@RequestParam("id") Long id) {
        MesWmProductionIssueDO issue = issueService.getProductionIssue(id);
        if (issue == null) {
            return success(null);
        }
        return success(buildRespVOList(Collections.singletonList(issue)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得领料出库单分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:query')")
    public CommonResult<PageResult<MesWmProductionIssueRespVO>> getProductionIssuePage(
            @Valid MesWmProductionIssuePageReqVO pageReqVO) {
        PageResult<MesWmProductionIssueDO> pageResult = issueService.getProductionIssuePage(pageReqVO);
        return success(new PageResult<>(buildRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出领料出库单 Excel")
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportProductionIssueExcel(@Valid MesWmProductionIssuePageReqVO pageReqVO,
                                            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<MesWmProductionIssueDO> pageResult = issueService.getProductionIssuePage(pageReqVO);
        ExcelUtils.write(response, "领料出库单.xls", "数据", MesWmProductionIssueRespVO.class,
                buildRespVOList(pageResult.getList()));
    }

    @PutMapping("/finish")
    @Operation(summary = "完成领料出库单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:execute')")
    public CommonResult<Boolean> finishProductionIssue(@RequestParam("id") Long id) {
        issueService.finishProductionIssue(id);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交领料出库单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:update')")
    public CommonResult<Boolean> submitProductionIssue(@RequestParam("id") Long id) {
        issueService.submitProductionIssue(id);
        return success(true);
    }

    @PutMapping("/stock")
    @Operation(summary = "执行拣货")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:update')")
    public CommonResult<Boolean> stockProductionIssue(@RequestParam("id") Long id) {
        issueService.stockProductionIssue(id);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消领料出库单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:update')")
    public CommonResult<Boolean> cancelProductionIssue(@RequestParam("id") Long id) {
        issueService.cancelProductionIssue(id);
        return success(true);
    }

    @GetMapping("/check-quantity")
    @Operation(summary = "校验领料出库单数量", description = "校验每行明细数量之和是否等于行领料数量")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:query')")
    public CommonResult<Boolean> checkProductionIssueQuantity(@RequestParam("id") Long id) {
        return success(issueService.checkProductionIssueQuantity(id));
    }

    // ==================== 拼接 VO ====================

    private List<MesWmProductionIssueRespVO> buildRespVOList(List<MesWmProductionIssueDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 获得关联数据
        Map<Long, MesMdWorkstationDO> workstationMap = workstationService.getWorkstationMap(
                convertSet(list, MesWmProductionIssueDO::getWorkstationId));
        Map<Long, MesProWorkOrderDO> workOrderMap = workOrderService.getWorkOrderMap(
                convertSet(list, MesWmProductionIssueDO::getWorkOrderId));
        // 2. 构建结果
        return BeanUtils.toBean(list, MesWmProductionIssueRespVO.class, vo -> {
            // 2.1 填充工作站名称
            MapUtils.findAndThen(workstationMap, vo.getWorkstationId(),
                    workstation -> vo.setWorkstationName(workstation.getName()));
            // 2.2 填充工单编号
            MapUtils.findAndThen(workOrderMap, vo.getWorkOrderId(),
                    workOrder -> vo.setWorkOrderCode(workOrder.getCode()));
        });
    }

}
