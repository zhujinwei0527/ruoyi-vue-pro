package cn.iocoder.yudao.module.mes.service.cal.plan;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.cal.plan.vo.MesCalPlanPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.plan.vo.MesCalPlanSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.MesCalPlanDO;
import cn.iocoder.yudao.module.mes.dal.mysql.cal.plan.MesCalPlanMapper;
import cn.iocoder.yudao.module.mes.enums.cal.MesCalPlanStatusEnum;
import cn.iocoder.yudao.module.mes.enums.cal.MesCalShiftTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 排班计划 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesCalPlanServiceImpl implements MesCalPlanService {

    @Resource
    private MesCalPlanMapper planMapper;
    @Resource
    @Lazy
    private MesCalShiftService shiftService;
    @Resource
    @Lazy
    private MesCalPlanTeamService planTeamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPlan(MesCalPlanSaveReqVO createReqVO) {
        // 1. 校验编码唯一
        validatePlanCodeUnique(null, createReqVO.getCode());

        // 2. 插入计划
        MesCalPlanDO plan = BeanUtils.toBean(createReqVO, MesCalPlanDO.class);
        plan.setStatus(MesCalPlanStatusEnum.PREPARE.getStatus()); // 默认草稿
        planMapper.insert(plan);

        // 3. 自动生成默认班次
        if (plan.getShiftType() != null) {
            shiftService.addDefaultShift(plan.getId(), plan.getShiftType());
        }
        return plan.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlan(MesCalPlanSaveReqVO updateReqVO) {
        // 1.1 校验存在
        MesCalPlanDO existPlan = validatePlanExists(updateReqVO.getId());
        // 1.2 校验编码唯一
        validatePlanCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 1.3 若 status 变为 CONFIRMED，校验班组数量
        if (MesCalPlanStatusEnum.CONFIRMED.getStatus().equals(updateReqVO.getStatus())
                && !MesCalPlanStatusEnum.CONFIRMED.getStatus().equals(existPlan.getStatus())) {
            validateTeamCountForConfirm(updateReqVO.getId(), updateReqVO.getShiftType());
        }

        // 2. 更新
        MesCalPlanDO updateObj = BeanUtils.toBean(updateReqVO, MesCalPlanDO.class);
        planMapper.updateById(updateObj);

        // TODO @芋艿：确认时调用 calTeamshiftService.genRecords(planId) 生成排班记录，等 cal_team_shift 迁移后
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePlan(Long id) {
        // 1.1 校验存在
        MesCalPlanDO plan = validatePlanExists(id);
        // 1.2 校验状态为草稿
        // TODO @AI：notEquals 更好一点；
        if (!MesCalPlanStatusEnum.PREPARE.getStatus().equals(plan.getStatus())) {
            throw exception(CAL_PLAN_NOT_PREPARE);
        }

        // 2.1 级联删除班次 + 班组关联
        shiftService.deleteShiftByPlanId(id);
        planTeamService.deleteByPlanId(id);
        // 2.2 删除计划
        planMapper.deleteById(id);
    }

    @Override
    public MesCalPlanDO getPlan(Long id) {
        return planMapper.selectById(id);
    }

    @Override
    public PageResult<MesCalPlanDO> getPlanPage(MesCalPlanPageReqVO pageReqVO) {
        return planMapper.selectPage(pageReqVO);
    }

    private MesCalPlanDO validatePlanExists(Long id) {
        MesCalPlanDO plan = planMapper.selectById(id);
        if (plan == null) {
            throw exception(CAL_PLAN_NOT_EXISTS);
        }
        return plan;
    }

    private void validatePlanCodeUnique(Long id, String code) {
        MesCalPlanDO plan = planMapper.selectByCode(code);
        if (plan == null) {
            return;
        }
        if (id == null || !id.equals(plan.getId())) {
            throw exception(CAL_PLAN_CODE_DUPLICATE);
        }
    }

    /**
     * 确认排班计划时，校验班组数量是否与轮班方式匹配
     */
    private void validateTeamCountForConfirm(Long planId, Integer shiftType) {
        Long teamCount = planTeamService.getPlanTeamCountByPlanId(planId);
        // TODO @AI：startTime、endTime 可以放到枚举类里么？List<hutool Pair>；这样就可以判断数量；
        if (MesCalShiftTypeEnum.TWO.getType().equals(shiftType) && teamCount < 2) {
            throw exception(CAL_PLAN_TEAM_COUNT_NOT_MATCH);
        } else if (MesCalShiftTypeEnum.THREE.getType().equals(shiftType) && teamCount < 3) {
            throw exception(CAL_PLAN_TEAM_COUNT_NOT_MATCH);
        }
        // TODO @AI：兜底下，其它，则抛出异常参数 IllegalArgumentException，提示不支持的轮班方式；
    }

}
