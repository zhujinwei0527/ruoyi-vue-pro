package cn.iocoder.yudao.module.mes.service.wm.stocktaking.plan;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.MesWmStockTakingPlanGenerateReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.MesWmStockTakingPlanPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.plan.vo.MesWmStockTakingPlanSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.plan.MesWmStockTakingPlanDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

// TODO @AI：必要的注释
/**
 * MES 盘点方案 Service 接口
 */
public interface MesWmStockTakingPlanService {

    Long createStockTakingPlan(@Valid MesWmStockTakingPlanSaveReqVO createReqVO);

    void updateStockTakingPlan(@Valid MesWmStockTakingPlanSaveReqVO updateReqVO);

    void deleteStockTakingPlan(Long id);

    void confirmStockTakingPlan(Long id);

    Long generateStockTakingTask(@Valid MesWmStockTakingPlanGenerateReqVO reqVO);

    MesWmStockTakingPlanDO getStockTakingPlan(Long id);

    MesWmStockTakingPlanDO validateStockTakingPlanExists(Long id);

    MesWmStockTakingPlanDO validateStockTakingPlanEditable(Long id);

    List<MesWmStockTakingPlanDO> getStockTakingPlanList(Collection<Long> ids);

    default Map<Long, MesWmStockTakingPlanDO> getStockTakingPlanMap(Collection<Long> ids) {
        return convertMap(getStockTakingPlanList(ids), MesWmStockTakingPlanDO::getId);
    }

    PageResult<MesWmStockTakingPlanDO> getStockTakingPlanPage(MesWmStockTakingPlanPageReqVO pageReqVO);

}
