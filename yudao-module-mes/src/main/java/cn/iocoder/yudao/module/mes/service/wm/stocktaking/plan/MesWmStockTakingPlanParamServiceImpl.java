package cn.iocoder.yudao.module.mes.service.wm.stocktaking.plan;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.param.MesWmStockTakingPlanParamSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.plan.MesWmStockTakingPlanDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.plan.MesWmStockTakingPlanParamDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.plan.MesWmStockTakingPlanParamMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_STOCK_TAKING_PLAN_PARAM_NOT_EXISTS;

// TODO @AI：必要的注释
/**
 * MES 盘点方案参数 Service 实现类
 */
@Service
@Validated
public class MesWmStockTakingPlanParamServiceImpl implements MesWmStockTakingPlanParamService {

    @Resource
    private MesWmStockTakingPlanParamMapper stockTakingPlanParamMapper;
    @Resource
    @Lazy
    private MesWmStockTakingPlanService stockTakingPlanService;

    @Override
    public Long createStockTakingPlanParam(MesWmStockTakingPlanParamSaveReqVO createReqVO) {
        MesWmStockTakingPlanDO plan = stockTakingPlanService.validateStockTakingPlanEditable(createReqVO.getPlanId());

        MesWmStockTakingPlanParamDO param = BeanUtils.toBean(createReqVO, MesWmStockTakingPlanParamDO.class);
        param.setPlanId(plan.getId());
        stockTakingPlanParamMapper.insert(param);
        return param.getId();
    }

    @Override
    public void updateStockTakingPlanParam(MesWmStockTakingPlanParamSaveReqVO updateReqVO) {
        MesWmStockTakingPlanParamDO param = validateStockTakingPlanParamExists(updateReqVO.getId());
        stockTakingPlanService.validateStockTakingPlanEditable(param.getPlanId());

        MesWmStockTakingPlanParamDO updateObj = BeanUtils.toBean(updateReqVO, MesWmStockTakingPlanParamDO.class);
        updateObj.setPlanId(param.getPlanId());
        stockTakingPlanParamMapper.updateById(updateObj);
    }

    @Override
    public void deleteStockTakingPlanParam(Long id) {
        MesWmStockTakingPlanParamDO param = validateStockTakingPlanParamExists(id);
        stockTakingPlanService.validateStockTakingPlanEditable(param.getPlanId());
        stockTakingPlanParamMapper.deleteById(id);
    }

    @Override
    public MesWmStockTakingPlanParamDO getStockTakingPlanParam(Long id) {
        return stockTakingPlanParamMapper.selectById(id);
    }

    @Override
    public List<MesWmStockTakingPlanParamDO> getStockTakingPlanParamList(Long planId) {
        return stockTakingPlanParamMapper.selectListByPlanId(planId);
    }

    @Override
    public List<MesWmStockTakingPlanParamDO> getStockTakingPlanParamList(Collection<Long> planIds) {
        if (CollUtil.isEmpty(planIds)) {
            return Collections.emptyList();
        }
        return stockTakingPlanParamMapper.selectListByPlanIds(planIds);
    }

    @Override
    public void deleteStockTakingPlanParamByPlanId(Long planId) {
        stockTakingPlanParamMapper.deleteByPlanId(planId);
    }

    @Override
    public MesWmStockTakingPlanParamDO validateStockTakingPlanParamExists(Long id) {
        MesWmStockTakingPlanParamDO param = stockTakingPlanParamMapper.selectById(id);
        if (param == null) {
            throw exception(WM_STOCK_TAKING_PLAN_PARAM_NOT_EXISTS);
        }
        return param;
    }

}
