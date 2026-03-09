package cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.*;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.param.MesWmStockTakingPlanParamRespVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.plan.MesWmStockTakingPlanDO;
import cn.iocoder.yudao.module.mes.service.wm.stocktaking.plan.MesWmStockTakingPlanParamService;
import cn.iocoder.yudao.module.mes.service.wm.stocktaking.plan.MesWmStockTakingPlanService;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - MES 盘点方案")
@RestController
@RequestMapping("/mes/wm/stocktaking-plan")
@Validated
public class MesWmStockTakingPlanController {

    @Resource
    private MesWmStockTakingPlanService stockTakingPlanService;
    @Resource
    private MesWmStockTakingPlanParamService stockTakingPlanParamService;

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
        if (plan == null) {
            return success(null);
        }
        return success(buildRespVOList(Collections.singletonList(plan), true).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得盘点方案分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-stocktaking-plan:query')")
    public CommonResult<PageResult<MesWmStockTakingPlanRespVO>> getStockTakingPlanPage(@Valid MesWmStockTakingPlanPageReqVO pageReqVO) {
        PageResult<MesWmStockTakingPlanDO> pageResult = stockTakingPlanService.getStockTakingPlanPage(pageReqVO);
        return success(new PageResult<>(buildRespVOList(pageResult.getList(), false), pageResult.getTotal()));
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
                buildRespVOList(pageResult.getList(), false));
    }

    // TODO @AI：去掉 confirmStockTakingPlan 方法；改成 updateStatus 这种接口；
    @PutMapping("/confirm")
    @Operation(summary = "确认盘点方案")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-stocktaking-plan:update')")
    public CommonResult<Boolean> confirmStockTakingPlan(@RequestParam("id") Long id) {
        stockTakingPlanService.confirmStockTakingPlan(id);
        return success(true);
    }

    // TODO @AI：去掉 generateStockTakingTask 方法
    @PostMapping("/generate-task")
    @Operation(summary = "根据盘点方案生成盘点任务")
    @PreAuthorize("@ss.hasPermission('mes:wm-stocktaking-task:create')")
    public CommonResult<Long> generateStockTakingTask(@Valid @RequestBody MesWmStockTakingPlanGenerateReqVO reqVO) {
        return success(stockTakingPlanService.generateStockTakingTask(reqVO));
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
