package cn.iocoder.yudao.module.mes.controller.admin.cal.team;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.cal.team.vo.shift.MesCalTeamShiftListReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.team.vo.shift.MesCalTeamShiftRespVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.team.MesCalTeamShiftDO;
import cn.iocoder.yudao.module.mes.service.cal.team.MesCalTeamShiftService;
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

@Tag(name = "管理后台 - MES 班组排班")
@RestController
@RequestMapping("/mes/cal/team-shift")
@Validated
public class MesCalTeamShiftController {

    @Resource
    private MesCalTeamShiftService teamShiftService;

    @GetMapping("/list")
    @Operation(summary = "获得班组排班列表")
    @PreAuthorize("@ss.hasPermission('mes:cal-team-shift:query')")
    public CommonResult<List<MesCalTeamShiftRespVO>> getTeamShiftList(@Valid MesCalTeamShiftListReqVO reqVO) {
        List<MesCalTeamShiftDO> list = teamShiftService.getTeamShiftList(reqVO);
        return success(BeanUtils.toBean(list, MesCalTeamShiftRespVO.class));
    }

}
