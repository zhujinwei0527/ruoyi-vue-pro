package cn.iocoder.yudao.module.mes.controller.admin.cal.plan;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.cal.plan.vo.shift.MesCalShiftPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.plan.vo.shift.MesCalShiftRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.plan.vo.shift.MesCalShiftSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.MesCalShiftDO;
import cn.iocoder.yudao.module.mes.service.cal.plan.MesCalShiftService;
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
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

// TODO @AI：它是不是改成 MesCalPlanShiftController 更好？毕竟它是排班计划的班次，而不是单纯的班次
@Tag(name = "管理后台 - MES 计划班次")
@RestController
@RequestMapping("/mes/cal/shift")
@Validated
public class MesCalShiftController {

    @Resource
    private MesCalShiftService shiftService;

    @PostMapping("/create")
    @Operation(summary = "创建计划班次")
    @PreAuthorize("@ss.hasPermission('mes:cal-shift:create')")
    public CommonResult<Long> createShift(@Valid @RequestBody MesCalShiftSaveReqVO createReqVO) {
        return success(shiftService.createShift(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新计划班次")
    @PreAuthorize("@ss.hasPermission('mes:cal-shift:update')")
    public CommonResult<Boolean> updateShift(@Valid @RequestBody MesCalShiftSaveReqVO updateReqVO) {
        shiftService.updateShift(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除计划班次")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:cal-shift:delete')")
    public CommonResult<Boolean> deleteShift(@RequestParam("id") Long id) {
        shiftService.deleteShift(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得计划班次")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:cal-shift:query')")
    public CommonResult<MesCalShiftRespVO> getShift(@RequestParam("id") Long id) {
        MesCalShiftDO shift = shiftService.getShift(id);
        return success(BeanUtils.toBean(shift, MesCalShiftRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得计划班次分页")
    @PreAuthorize("@ss.hasPermission('mes:cal-shift:query')")
    public CommonResult<PageResult<MesCalShiftRespVO>> getShiftPage(@Valid MesCalShiftPageReqVO pageReqVO) {
        PageResult<MesCalShiftDO> pageResult = shiftService.getShiftPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesCalShiftRespVO.class));
    }

    @GetMapping("/list-by-plan")
    @Operation(summary = "获得指定排班计划的班次列表")
    @Parameter(name = "planId", description = "排班计划编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:cal-shift:query')")
    public CommonResult<List<MesCalShiftRespVO>> getShiftListByPlan(@RequestParam("planId") Long planId) {
        List<MesCalShiftDO> list = shiftService.getShiftListByPlanId(planId);
        return success(BeanUtils.toBean(list, MesCalShiftRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出计划班次 Excel")
    @PreAuthorize("@ss.hasPermission('mes:cal-shift:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportShiftExcel(@Valid MesCalShiftPageReqVO pageReqVO,
                                 HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MesCalShiftDO> list = shiftService.getShiftPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "计划班次.xls", "数据", MesCalShiftRespVO.class,
                BeanUtils.toBean(list, MesCalShiftRespVO.class));
    }

}
