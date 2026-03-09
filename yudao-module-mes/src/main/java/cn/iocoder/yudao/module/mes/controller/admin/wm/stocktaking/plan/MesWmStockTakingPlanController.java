package cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.MesWmStockTakingPlanPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.MesWmStockTakingPlanRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.MesWmStockTakingPlanSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.plan.MesWmStockTakingPlanDO;
import cn.iocoder.yudao.module.mes.service.wm.stocktaking.plan.MesWmStockTakingPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 盘点方案")
@RestController
@RequestMapping("/mes/wm/stocktaking-plan")
@Validated
public class MesWmStockTakingPlanController {

    @Resource
    private MesWmStockTakingPlanService stockTakingPlanService;

    @PostMapping("/create")
    @Operation(summary = "创建盘点方案")
    @PreAuthorize("@ss.hasPermission('mes:wm-stocktaking-plan:create')")
    public CommonResult<Long> createStockTakingPlan(@Valid @RequestBody MesWmStockTakingPlanSaveReqVO createReqVO) {
        return success(stockTakingPlanService.createStockTakingPlan(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改盘点方案")
    @PreAuthorize("@ss.hasPermission('mes:wm-stocktaking-plan:update')")
    public CommonResult<Boolean> updateStockTakingPlan(@Valid @RequestBody MesWmStockTakingPlanSaveReqVO updateReqVO) {
        stockTakingPlanService.updateStockTakingPlan(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除盘点方案")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-stocktaking-plan:delete')")
    public CommonResult<Boolean> deleteStockTakingPlan(@RequestParam("id") Long id) {
        stockTakingPlanService.deleteStockTakingPlan(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得盘点方案")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-stocktaking-plan:query')")
    public CommonResult<MesWmStockTakingPlanRespVO> getStockTakingPlan(@RequestParam("id") Long id) {
        MesWmStockTakingPlanDO plan = stockTakingPlanService.getStockTakingPlan(id);
        return success(plan == null ? null : BeanUtils.toBean(plan, MesWmStockTakingPlanRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得盘点方案分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-stocktaking-plan:query')")
    public CommonResult<PageResult<MesWmStockTakingPlanRespVO>> getStockTakingPlanPage(@Valid MesWmStockTakingPlanPageReqVO pageReqVO) {
        PageResult<MesWmStockTakingPlanDO> pageResult = stockTakingPlanService.getStockTakingPlanPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesWmStockTakingPlanRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出盘点方案 Excel")
    @PreAuthorize("@ss.hasPermission('mes:wm-stocktaking-plan:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportStockTakingPlanExcel(@Valid MesWmStockTakingPlanPageReqVO pageReqVO,
                                           HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<MesWmStockTakingPlanDO> pageResult = stockTakingPlanService.getStockTakingPlanPage(pageReqVO);
        ExcelUtils.write(response, "盘点方案.xls", "数据", MesWmStockTakingPlanRespVO.class,
                BeanUtils.toBean(pageResult.getList(), MesWmStockTakingPlanRespVO.class));
    }

    private List<MesWmStockTakingPlanRespVO> buildRespVOList(List<MesWmStockTakingPlanDO> list, boolean includeParams) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        final Map<Long, List<MesWmStockTakingPlanParamRespVO>> finalParamMap;
        if (includeParams) {
            Collection<Long> planIds = convertSet(list, MesWmStockTakingPlanDO::getId);
            Map<Long, List<MesWmStockTakingPlanParamRespVO>> paramMap = new java.util.HashMap<>();
            stockTakingPlanParamService.getStockTakingPlanParamMap(planIds)
                    .forEach((planId, params) -> paramMap.put(planId,
                            BeanUtils.toBean(params, MesWmStockTakingPlanParamRespVO.class)));
            finalParamMap = paramMap;
        } else {
            finalParamMap = Collections.emptyMap();
        }
        return BeanUtils.toBean(list, MesWmStockTakingPlanRespVO.class,
                vo -> vo.setParams(finalParamMap.getOrDefault(vo.getId(), Collections.emptyList())));
    }

}
