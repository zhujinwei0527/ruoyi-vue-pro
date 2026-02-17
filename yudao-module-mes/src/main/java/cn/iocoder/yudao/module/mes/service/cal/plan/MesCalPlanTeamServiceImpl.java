package cn.iocoder.yudao.module.mes.service.cal.plan;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.cal.plan.vo.team.MesCalPlanTeamSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.MesCalPlanTeamDO;
import cn.iocoder.yudao.module.mes.dal.mysql.cal.plan.MesCalPlanTeamMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 计划班组关联 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesCalPlanTeamServiceImpl implements MesCalPlanTeamService {

    @Resource
    private MesCalPlanTeamMapper planTeamMapper;

    @Override
    public Long createPlanTeam(MesCalPlanTeamSaveReqVO createReqVO) {
        // 校验排班计划存在
        validatePlanTeamDuplicate(createReqVO.getPlanId(), createReqVO.getTeamId());

        // 插入
        MesCalPlanTeamDO planTeam = BeanUtils.toBean(createReqVO, MesCalPlanTeamDO.class);
        planTeamMapper.insert(planTeam);
        return planTeam.getId();
    }

    @Override
    public void deletePlanTeam(Long id) {
        // 校验存在
        validatePlanTeamExists(id);
        // 删除
        planTeamMapper.deleteById(id);
    }

    private void validatePlanTeamExists(Long id) {
        if (planTeamMapper.selectById(id) == null) {
            throw exception(CAL_PLAN_TEAM_NOT_EXISTS);
        }
    }

    private void validatePlanTeamDuplicate(Long planId, Long teamId) {
        MesCalPlanTeamDO planTeam = planTeamMapper.selectByPlanIdAndTeamId(planId, teamId);
        if (planTeam != null) {
            throw exception(CAL_PLAN_TEAM_DUPLICATE);
        }
    }

    @Override
    public List<MesCalPlanTeamDO> getPlanTeamListByPlanId(Long planId) {
        return planTeamMapper.selectListByPlanId(planId);
    }

    @Override
    public Long getPlanTeamCountByPlanId(Long planId) {
        return planTeamMapper.selectCountByPlanId(planId);
    }

    @Override
    public void deleteByPlanId(Long planId) {
        planTeamMapper.deleteByPlanId(planId);
    }

}
