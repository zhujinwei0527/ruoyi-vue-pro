package cn.iocoder.yudao.module.mes.controller.admin.cal.team;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.cal.team.vo.member.MesCalTeamMemberPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.team.vo.member.MesCalTeamMemberRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.team.vo.member.MesCalTeamMemberSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.team.MesCalTeamMemberDO;
import cn.iocoder.yudao.module.mes.service.cal.team.MesCalTeamMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 班组成员")
@RestController
@RequestMapping("/mes/cal/team-member")
@Validated
public class MesCalTeamMemberController {

    @Resource
    private MesCalTeamMemberService teamMemberService;

    @PostMapping("/create")
    @Operation(summary = "创建班组成员")
    @PreAuthorize("@ss.hasPermission('mes:cal-team:create')")
    public CommonResult<Long> createTeamMember(@Valid @RequestBody MesCalTeamMemberSaveReqVO createReqVO) {
        return success(teamMemberService.createTeamMember(createReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除班组成员")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:cal-team:delete')")
    public CommonResult<Boolean> deleteTeamMember(@RequestParam("id") Long id) {
        teamMemberService.deleteTeamMember(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得班组成员")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:cal-team:query')")
    public CommonResult<MesCalTeamMemberRespVO> getTeamMember(@RequestParam("id") Long id) {
        MesCalTeamMemberDO member = teamMemberService.getTeamMember(id);
        return success(BeanUtils.toBean(member, MesCalTeamMemberRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得班组成员分页")
    @PreAuthorize("@ss.hasPermission('mes:cal-team:query')")
    public CommonResult<PageResult<MesCalTeamMemberRespVO>> getTeamMemberPage(@Valid MesCalTeamMemberPageReqVO pageReqVO) {
        PageResult<MesCalTeamMemberDO> pageResult = teamMemberService.getTeamMemberPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesCalTeamMemberRespVO.class));
    }

    // TODO @AI：getTeamMemberListByTeam、getTeamMemberListByTeamIds 融合下，允许传递 teamId、teamIds；

    @GetMapping("/list-by-team")
    @Operation(summary = "获得指定班组的成员列表")
    @Parameter(name = "teamId", description = "班组编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:cal-team:query')")
    public CommonResult<List<MesCalTeamMemberRespVO>> getTeamMemberListByTeam(@RequestParam("teamId") Long teamId) {
        List<MesCalTeamMemberDO> list = teamMemberService.getTeamMemberListByTeamId(teamId);
        return success(BeanUtils.toBean(list, MesCalTeamMemberRespVO.class));
    }

    @GetMapping("/list-by-team-ids")
    @Operation(summary = "获得指定多个班组的成员列表")
    @Parameter(name = "teamIds", description = "班组编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('mes:cal-team:query')")
    public CommonResult<List<MesCalTeamMemberRespVO>> getTeamMemberListByTeamIds(
            @RequestParam("teamIds") Collection<Long> teamIds) {
        List<MesCalTeamMemberDO> list = teamMemberService.getTeamMemberListByTeamIds(teamIds);
        return success(BeanUtils.toBean(list, MesCalTeamMemberRespVO.class));
    }

}
