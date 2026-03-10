package cn.iocoder.yudao.module.mes.service.wm.stocktaking.plan;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.MesWmStockTakingPlanPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.MesWmStockTakingPlanSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.plan.MesWmStockTakingPlanDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.plan.MesWmStockTakingPlanParamDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.plan.MesWmStockTakingPlanMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 盘点方案 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesWmStockTakingPlanServiceImpl implements MesWmStockTakingPlanService {

    @Resource
    private MesWmStockTakingPlanMapper stockTakingPlanMapper;
    @Resource
    private MesWmStockTakingPlanParamService stockTakingPlanParamService;

    @Override
    public Long createStockTakingPlan(MesWmStockTakingPlanSaveReqVO createReqVO) {
        // 校验 code 的唯一性
        validatePlanCodeUnique(null, createReqVO.getCode());

        // 插入数据
        MesWmStockTakingPlanDO plan = BeanUtils.toBean(createReqVO, MesWmStockTakingPlanDO.class);
        plan.setStatus(CommonStatusEnum.DISABLE.getStatus());
        stockTakingPlanMapper.insert(plan);
        return plan.getId();
    }

    @Override
    public void updateStockTakingPlan(MesWmStockTakingPlanSaveReqVO updateReqVO) {
        // 校验盘点方案存在，并且是可编辑状态
        validatePlanEditable(updateReqVO.getId());
        // 校验 code 的唯一性
        validatePlanCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 更新数据
        MesWmStockTakingPlanDO updateObj = BeanUtils.toBean(updateReqVO, MesWmStockTakingPlanDO.class);
        stockTakingPlanMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStockTakingPlan(Long id) {
        // 校验盘点方案存在，并且是可编辑状态
        validatePlanEditable(id);

        // 删除盘点方案参数
        stockTakingPlanParamService.deleteStockTakingPlanParamByPlanId(id);
        // 删除盘点方案
        stockTakingPlanMapper.deleteById(id);
    }

    @Override
    public void updateStockTakingPlanStatus(Long id, Integer status) {
        // 校验盘点方案存在
        validateStockTakingPlanExists(id);
        // 开启时，需要校验 params 非空
        if (CommonStatusEnum.isEnable(status)) {
            List<MesWmStockTakingPlanParamDO> params = stockTakingPlanParamService.getStockTakingPlanParamListByPlanId(id);
            if (CollUtil.isEmpty(params)) {
                throw exception(WM_STOCK_TAKING_PLAN_PARAM_EMPTY);
            }
        }

        // 更新状态
        stockTakingPlanMapper.updateById(new MesWmStockTakingPlanDO().setId(id).setStatus(status));
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
        return validatePlanEditable(id);
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

    @Override
    public List<MesWmStockTakingPlanDO> getStockTakingPlanListByStatus(Integer status) {
        return stockTakingPlanMapper.selectListByStatus(status);
    }

    private void validatePlanCodeUnique(Long id, String code) {
        MesWmStockTakingPlanDO plan = stockTakingPlanMapper.selectByCode(code);
        if (plan == null) {
            return;
        }
        if (ObjUtil.notEqual(plan.getId(), id)) {
            throw exception(WM_STOCK_TAKING_PLAN_CODE_DUPLICATE);
        }
    }

    @Override
    public MesWmStockTakingPlanDO validateStockTakingPlanEnabled(Long id) {
        MesWmStockTakingPlanDO plan = validateStockTakingPlanExists(id);
        if (!CommonStatusEnum.isEnable(plan.getStatus())) {
            throw exception(WM_STOCK_TAKING_PLAN_NOT_ENABLED);
        }
        return plan;
    }

    private MesWmStockTakingPlanDO validatePlanEditable(Long id) {
        MesWmStockTakingPlanDO plan = validateStockTakingPlanExists(id);
        if (!CommonStatusEnum.isDisable(plan.getStatus())) {
            throw exception(WM_STOCK_TAKING_PLAN_NOT_DISABLED);
        }
        return plan;
    }

}
