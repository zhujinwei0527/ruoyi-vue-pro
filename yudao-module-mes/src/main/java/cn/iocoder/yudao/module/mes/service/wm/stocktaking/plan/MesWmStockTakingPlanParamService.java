package cn.iocoder.yudao.module.mes.service.wm.stocktaking.plan;

import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.param.MesWmStockTakingPlanParamSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.plan.MesWmStockTakingPlanParamDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMultiMap;

// TODO @AI：必要的注释
/**
 * MES 盘点方案参数 Service 接口
 */
public interface MesWmStockTakingPlanParamService {

    Long createStockTakingPlanParam(@Valid MesWmStockTakingPlanParamSaveReqVO createReqVO);

    void updateStockTakingPlanParam(@Valid MesWmStockTakingPlanParamSaveReqVO updateReqVO);

    void deleteStockTakingPlanParam(Long id);

    MesWmStockTakingPlanParamDO getStockTakingPlanParam(Long id);

    List<MesWmStockTakingPlanParamDO> getStockTakingPlanParamList(Long planId);

    default Map<Long, List<MesWmStockTakingPlanParamDO>> getStockTakingPlanParamMap(Collection<Long> planIds) {
        return convertMultiMap(getStockTakingPlanParamList(planIds), MesWmStockTakingPlanParamDO::getPlanId);
    }

    List<MesWmStockTakingPlanParamDO> getStockTakingPlanParamList(Collection<Long> planIds);

    void deleteStockTakingPlanParamByPlanId(Long planId);

    MesWmStockTakingPlanParamDO validateStockTakingPlanParamExists(Long id);

}
