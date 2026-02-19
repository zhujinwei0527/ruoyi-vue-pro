package cn.iocoder.yudao.module.mes.controller.admin.cal.calendar;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.mes.controller.admin.cal.calendar.vo.MesCalCalendarListReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.calendar.vo.MesCalCalendarRespVO;
import cn.iocoder.yudao.module.mes.service.cal.calendar.MesCalCalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 排班日历")
@RestController
@RequestMapping("/mes/cal/calendar")
@Validated
public class MesCalCalendarController {

    @Resource
    private MesCalCalendarService calendarService;

    @GetMapping("/list")
    @Operation(summary = "查询排班日历")
    @PreAuthorize("@ss.hasPermission('mes:cal-team-shift:query')")
    public CommonResult<List<MesCalCalendarRespVO>> getCalendarList(@Valid MesCalCalendarListReqVO reqVO) {
        List<MesCalCalendarRespVO> list = calendarService.getCalendarList(reqVO);
        return success(list);
    }

}
