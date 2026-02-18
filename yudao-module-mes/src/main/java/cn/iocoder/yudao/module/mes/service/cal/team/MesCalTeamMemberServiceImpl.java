package cn.iocoder.yudao.module.mes.service.cal.team;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.cal.team.vo.member.MesCalTeamMemberPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.team.vo.member.MesCalTeamMemberSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.team.MesCalTeamMemberDO;
import cn.iocoder.yudao.module.mes.dal.mysql.cal.team.MesCalTeamMemberMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 班组成员 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesCalTeamMemberServiceImpl implements MesCalTeamMemberService {

    @Resource
    private MesCalTeamMemberMapper teamMemberMapper;
    @Resource
    @Lazy
    private MesCalTeamService teamService;

    @Override
    public Long createTeamMember(MesCalTeamMemberSaveReqVO createReqVO) {
        // 1.1 校验班组存在
        teamService.validateTeamExists(createReqVO.getTeamId());
        // 1.2 校验用户未分配到其他班组
        validateUserUnique(null, createReqVO.getUserId());

        // 2. 插入
        MesCalTeamMemberDO member = BeanUtils.toBean(createReqVO, MesCalTeamMemberDO.class);
        teamMemberMapper.insert(member);
        return member.getId();
    }

    @Override
    public void deleteTeamMember(Long id) {
        // 校验存在
        validateTeamMemberExists(id);
        // 删除
        teamMemberMapper.deleteById(id);
    }

    @Override
    public MesCalTeamMemberDO getTeamMember(Long id) {
        return teamMemberMapper.selectById(id);
    }

    @Override
    public PageResult<MesCalTeamMemberDO> getTeamMemberPage(MesCalTeamMemberPageReqVO pageReqVO) {
        return teamMemberMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesCalTeamMemberDO> getTeamMemberListByTeamId(Long teamId) {
        return teamMemberMapper.selectListByTeamId(teamId);
    }

    @Override
    public List<MesCalTeamMemberDO> getTeamMemberListByTeamIds(Collection<Long> teamIds) {
        return teamMemberMapper.selectListByTeamIds(teamIds);
    }

    @Override
    public void deleteByTeamId(Long teamId) {
        teamMemberMapper.deleteByTeamId(teamId);
    }

    // TODO @AI：无需返回；
    private MesCalTeamMemberDO validateTeamMemberExists(Long id) {
        MesCalTeamMemberDO member = teamMemberMapper.selectById(id);
        if (member == null) {
            throw exception(CAL_TEAM_MEMBER_NOT_EXISTS);
        }
        return member;
    }

    // TODO @AI：这里的 excludeId 可以去掉
    private void validateUserUnique(Long excludeId, Long userId) {
        MesCalTeamMemberDO member = teamMemberMapper.selectByUserIdExcludeId(userId, excludeId);
        if (member != null) {
            throw exception(CAL_TEAM_MEMBER_USER_DUPLICATE);
        }
    }

}
