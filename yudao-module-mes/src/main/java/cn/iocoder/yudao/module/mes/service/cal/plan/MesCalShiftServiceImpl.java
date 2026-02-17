package cn.iocoder.yudao.module.mes.service.cal.plan;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.cal.plan.vo.shift.MesCalShiftPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.plan.vo.shift.MesCalShiftSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.MesCalPlanDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.MesCalShiftDO;
import cn.iocoder.yudao.module.mes.dal.mysql.cal.plan.MesCalPlanMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.cal.plan.MesCalShiftMapper;
import cn.iocoder.yudao.module.mes.enums.cal.CalConstants;
import cn.iocoder.yudao.module.mes.enums.cal.MesCalShiftTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 计划班次 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesCalShiftServiceImpl implements MesCalShiftService {

    @Resource
    private MesCalShiftMapper shiftMapper;
    @Resource
    private MesCalPlanMapper planMapper;

    @Override
    public Long createShift(MesCalShiftSaveReqVO createReqVO) {
        // 校验班次数量限制
        validateShiftCount(createReqVO.getPlanId());

        // 插入
        MesCalShiftDO shift = BeanUtils.toBean(createReqVO, MesCalShiftDO.class);
        shiftMapper.insert(shift);
        return shift.getId();
    }

    @Override
    public void updateShift(MesCalShiftSaveReqVO updateReqVO) {
        // 校验存在
        validateShiftExists(updateReqVO.getId());
        // 更新
        MesCalShiftDO updateObj = BeanUtils.toBean(updateReqVO, MesCalShiftDO.class);
        shiftMapper.updateById(updateObj);
    }

    @Override
    public void deleteShift(Long id) {
        // 校验存在
        validateShiftExists(id);
        // 删除
        shiftMapper.deleteById(id);
    }

    private void validateShiftExists(Long id) {
        if (shiftMapper.selectById(id) == null) {
            throw exception(CAL_SHIFT_NOT_EXISTS);
        }
    }

    /**
     * 校验班次数量是否超限
     * <p>
     * 通过 planId 查询已有班次数量，根据排班计划的轮班方式校验
     */
    private void validateShiftCount(Long planId) {
        MesCalPlanDO plan = planMapper.selectById(planId);
        if (plan == null) {
            return;
        }
        Long count = shiftMapper.selectCountByPlanId(planId);
        Integer shiftType = plan.getShiftType();
        // TODO @AI：startTime、endTime 可以放到枚举类里么？List<hutool Pair>；这样可以 count 来判断。更简洁；
        if (MesCalShiftTypeEnum.SINGLE.getType().equals(shiftType) && count >= 1) {
            throw exception(CAL_SHIFT_COUNT_EXCEED_SINGLE);
        } else if (MesCalShiftTypeEnum.TWO.getType().equals(shiftType) && count >= 2) {
            throw exception(CAL_SHIFT_COUNT_EXCEED_TWO);
        } else if (MesCalShiftTypeEnum.THREE.getType().equals(shiftType) && count >= 3) {
            throw exception(CAL_SHIFT_COUNT_EXCEED_THREE);
        }
    }

    @Override
    public MesCalShiftDO getShift(Long id) {
        return shiftMapper.selectById(id);
    }

    @Override
    public PageResult<MesCalShiftDO> getShiftPage(MesCalShiftPageReqVO pageReqVO) {
        return shiftMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesCalShiftDO> getShiftListByPlanId(Long planId) {
        return shiftMapper.selectListByPlanId(planId);
    }

    @Override
    public Long getShiftCountByPlanId(Long planId) {
        return shiftMapper.selectCountByPlanId(planId);
    }

    @Override
    public void addDefaultShift(Long planId, Integer shiftType) {
        // 根据轮班方式，添加默认班次
        // TODO @AI：startTime、endTime 可以放到枚举类里么？List<hutool Pair>；这样就可以 for 循环；
        if (MesCalShiftTypeEnum.SINGLE.getType().equals(shiftType)) {
            // 单白班：1 个班次
            insertShift(planId, 1, CalConstants.SHIFT_NAME_DAY, "08:00", "17:00");
        } else if (MesCalShiftTypeEnum.TWO.getType().equals(shiftType)) {
            // 两班倒：2 个班次
            insertShift(planId, 1, CalConstants.SHIFT_NAME_DAY, "08:00", "20:00");
            insertShift(planId, 2, CalConstants.SHIFT_NAME_NIGHT, "20:00", "08:00");
        } else if (MesCalShiftTypeEnum.THREE.getType().equals(shiftType)) {
            // 三班倒：3 个班次
            insertShift(planId, 1, CalConstants.SHIFT_NAME_DAY, "08:00", "16:00");
            insertShift(planId, 2, CalConstants.SHIFT_NAME_MID, "16:00", "00:00");
            insertShift(planId, 3, CalConstants.SHIFT_NAME_NIGHT, "00:00", "08:00");
        }
    }

    private void insertShift(Long planId, Integer sort, String name, String startTime, String endTime) {
        // TODO @AI：链式 set，想通放在一样；
        MesCalShiftDO shift = new MesCalShiftDO();
        shift.setPlanId(planId);
        shift.setSort(sort);
        shift.setName(name);
        shift.setStartTime(startTime);
        shift.setEndTime(endTime);
        shiftMapper.insert(shift);
    }

    @Override
    public void deleteShiftByPlanId(Long planId) {
        shiftMapper.deleteByPlanId(planId);
    }

}
