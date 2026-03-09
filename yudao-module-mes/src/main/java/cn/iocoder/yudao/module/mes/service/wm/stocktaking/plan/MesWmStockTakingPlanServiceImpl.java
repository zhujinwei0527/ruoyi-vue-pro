package cn.iocoder.yudao.module.mes.service.wm.stocktaking.plan;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.MesWmStockTakingPlanGenerateReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.MesWmStockTakingPlanPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.param.MesWmStockTakingPlanParamSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.MesWmStockTakingPlanSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.vo.task.MesWmStockTakingTaskSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.plan.MesWmStockTakingPlanDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.plan.MesWmStockTakingPlanMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmStockTakingPlanStatusEnum;
import cn.iocoder.yudao.module.mes.service.wm.stocktaking.MesWmStockTakingTaskService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

// TODO @AI：必要的注释
@Service
@Validated
public class MesWmStockTakingPlanServiceImpl implements MesWmStockTakingPlanService {

    @Resource
    private MesWmStockTakingPlanMapper stockTakingPlanMapper;
    @Resource
    private MesWmStockTakingPlanParamService stockTakingPlanParamService;
    @Resource
    private MesWmStockTakingTaskService stockTakingTaskService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStockTakingPlan(MesWmStockTakingPlanSaveReqVO createReqVO) {
        validatePlanCodeUnique(null, createReqVO.getCode());

        MesWmStockTakingPlanDO plan = BeanUtils.toBean(createReqVO, MesWmStockTakingPlanDO.class);
        plan.setStatus(MesWmStockTakingPlanStatusEnum.PREPARE.getStatus());
        stockTakingPlanMapper.insert(plan);
        return plan.getId();
    }

    // TODO @AI：开启后，就不允许编辑、删除了；
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockTakingPlan(MesWmStockTakingPlanSaveReqVO updateReqVO) {
        validatePlanExistsAndPrepare(updateReqVO.getId());
        validatePlanCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        MesWmStockTakingPlanDO updateObj = BeanUtils.toBean(updateReqVO, MesWmStockTakingPlanDO.class);
        stockTakingPlanMapper.updateById(updateObj);
    }

    // TODO @AI：开启后，就不允许编辑、删除了；
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStockTakingPlan(Long id) {
        validatePlanExistsAndPrepare(id);
        stockTakingPlanParamService.deleteStockTakingPlanParamByPlanId(id);
        stockTakingPlanMapper.deleteById(id);
    }

    @Override
    public void confirmStockTakingPlan(Long id) {
        MesWmStockTakingPlanDO plan = validatePlanExistsAndPrepare(id);
        plan.setStatus(MesWmStockTakingPlanStatusEnum.CONFIRMED.getStatus());
        stockTakingPlanMapper.updateById(plan);
    }

    // TODO @AI：这个方法不需要了呀；
    @Override
    public Long generateStockTakingTask(MesWmStockTakingPlanGenerateReqVO reqVO) {
        MesWmStockTakingPlanDO plan = validatePlanExistsAndConfirmed(reqVO.getPlanId());
        MesWmStockTakingTaskSaveReqVO taskSaveReqVO = new MesWmStockTakingTaskSaveReqVO();
        taskSaveReqVO.setCode(reqVO.getCode());
        taskSaveReqVO.setName(reqVO.getName());
        taskSaveReqVO.setTakingDate(reqVO.getTakingDate());
        taskSaveReqVO.setType(plan.getType());
        taskSaveReqVO.setUserId(reqVO.getUserId());
        taskSaveReqVO.setPlanId(plan.getId());
        taskSaveReqVO.setBlindFlag(plan.getBlindFlag());
        taskSaveReqVO.setFrozenFlag(plan.getFrozenFlag());
        taskSaveReqVO.setRemark(reqVO.getRemark());

        List<MesWmStockTakingPlanParamSaveReqVO> params = reqVO.getParams();
        if (CollUtil.isEmpty(params)) {
            params = BeanUtils.toBean(stockTakingPlanParamService.getStockTakingPlanParamList(plan.getId()), MesWmStockTakingPlanParamSaveReqVO.class);
        }
        return stockTakingTaskService.createStockTakingTaskByPlan(reqVO, taskSaveReqVO, params);
    }

    @Override
    public MesWmStockTakingPlanDO getStockTakingPlan(Long id) {
        return stockTakingPlanMapper.selectById(id);
    }

    @Override
    public MesWmStockTakingPlanDO validateStockTakingPlanExists(Long id) {
        MesWmStockTakingPlanDO plan = stockTakingPlanMapper.selectById(id);
        if (plan == null) {
            throw exception(WM_STOCK_TAKING_PLAN_NOT_EXISTS);
        }
        return plan;
    }

    @Override
    public MesWmStockTakingPlanDO validateStockTakingPlanEditable(Long id) {
        return validatePlanExistsAndPrepare(id);
    }

    @Override
    public List<MesWmStockTakingPlanDO> getStockTakingPlanList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return stockTakingPlanMapper.selectByIds(ids);
    }

    @Override
    public PageResult<MesWmStockTakingPlanDO> getStockTakingPlanPage(MesWmStockTakingPlanPageReqVO pageReqVO) {
        return stockTakingPlanMapper.selectPage(pageReqVO);
    }


    private void validatePlanCodeUnique(Long id, String code) {
        MesWmStockTakingPlanDO plan = stockTakingPlanMapper.selectByCode(code);
        if (plan == null) {
            return;
        }
        if (id == null || !Objects.equals(plan.getId(), id)) {
            throw exception(WM_STOCK_TAKING_PLAN_CODE_DUPLICATE);
        }
    }

    private MesWmStockTakingPlanDO validatePlanExistsAndPrepare(Long id) {
        MesWmStockTakingPlanDO plan = validateStockTakingPlanExists(id);
        if (!MesWmStockTakingPlanStatusEnum.PREPARE.getStatus().equals(plan.getStatus())) {
            throw exception(WM_STOCK_TAKING_PLAN_NOT_PREPARE);
        }
        return plan;
    }

    private MesWmStockTakingPlanDO validatePlanExistsAndConfirmed(Long id) {
        MesWmStockTakingPlanDO plan = validateStockTakingPlanExists(id);
        if (!MesWmStockTakingPlanStatusEnum.CONFIRMED.getStatus().equals(plan.getStatus())) {
            throw exception(WM_STOCK_TAKING_PLAN_NOT_CONFIRMED);
        }
        return plan;
    }

}
