package cn.iocoder.yudao.module.mes.controller.admin.cal.holiday;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.cal.holiday.vo.MesCalHolidayPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.holiday.vo.MesCalHolidayRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.holiday.vo.MesCalHolidaySaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.holiday.MesCalHolidayDO;
import cn.iocoder.yudao.module.mes.service.cal.holiday.MesCalHolidayService;
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

@Tag(name = "管理后台 - MES 假期设置")
@RestController
@RequestMapping("/mes/cal/holiday")
@Validated
public class MesCalHolidayController {

    @Resource
    private MesCalHolidayService holidayService;

    // TODO @AI：是不是只有 save，没有 update 和 delete，以及 create？

    @PostMapping("/create")
    @Operation(summary = "创建假期设置", description = "如果该日期已存在记录，则更新")
    @PreAuthorize("@ss.hasPermission('mes:cal-holiday:create')")
    public CommonResult<Long> createHoliday(@Valid @RequestBody MesCalHolidaySaveReqVO createReqVO) {
        return success(holidayService.createHoliday(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新假期设置")
    @PreAuthorize("@ss.hasPermission('mes:cal-holiday:update')")
    public CommonResult<Boolean> updateHoliday(@Valid @RequestBody MesCalHolidaySaveReqVO updateReqVO) {
        holidayService.updateHoliday(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除假期设置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:cal-holiday:delete')")
    public CommonResult<Boolean> deleteHoliday(@RequestParam("id") Long id) {
        holidayService.deleteHoliday(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得假期设置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:cal-holiday:query')")
    public CommonResult<MesCalHolidayRespVO> getHoliday(@RequestParam("id") Long id) {
        MesCalHolidayDO holiday = holidayService.getHoliday(id);
        return success(BeanUtils.toBean(holiday, MesCalHolidayRespVO.class));
    }

    // TODO @AI：对齐下，/Users/yunai/Java/ktg-mes 界面（不是后端），是否需要该接口。
    @GetMapping("/page")
    @Operation(summary = "获得假期设置分页")
    @PreAuthorize("@ss.hasPermission('mes:cal-holiday:query')")
    public CommonResult<PageResult<MesCalHolidayRespVO>> getHolidayPage(@Valid MesCalHolidayPageReqVO pageReqVO) {
        PageResult<MesCalHolidayDO> pageResult = holidayService.getHolidayPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesCalHolidayRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得所有假期设置列表", description = "日历组件使用，返回全量数据")
    @PreAuthorize("@ss.hasPermission('mes:cal-holiday:query')")
    public CommonResult<List<MesCalHolidayRespVO>> getHolidayList() {
        List<MesCalHolidayDO> list = holidayService.getHolidayList();
        return success(BeanUtils.toBean(list, MesCalHolidayRespVO.class));
    }

    // TODO @AI：对齐下，/Users/yunai/Java/ktg-mes 界面（不是后端），是否需要该接口。
    @GetMapping("/export-excel")
    @Operation(summary = "导出假期设置 Excel")
    @PreAuthorize("@ss.hasPermission('mes:cal-holiday:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportHolidayExcel(@Valid MesCalHolidayPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MesCalHolidayDO> list = holidayService.getHolidayPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "假期设置.xls", "数据", MesCalHolidayRespVO.class,
                BeanUtils.toBean(list, MesCalHolidayRespVO.class));
    }

}
