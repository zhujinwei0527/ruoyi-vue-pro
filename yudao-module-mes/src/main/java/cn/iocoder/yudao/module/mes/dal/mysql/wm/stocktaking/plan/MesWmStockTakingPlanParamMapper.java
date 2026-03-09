package cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.plan;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.plan.MesWmStockTakingPlanParamDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * MES 盘点方案参数 Mapper
 */
@Mapper
public interface MesWmStockTakingPlanParamMapper extends BaseMapperX<MesWmStockTakingPlanParamDO> {

    default List<MesWmStockTakingPlanParamDO> selectListByPlanId(Long planId) {
        return selectList(MesWmStockTakingPlanParamDO::getPlanId, planId);
    }

    default List<MesWmStockTakingPlanParamDO> selectListByPlanIds(Collection<Long> planIds) {
        return selectList(MesWmStockTakingPlanParamDO::getPlanId, planIds);
    }

    default void deleteByPlanId(Long planId) {
        delete(MesWmStockTakingPlanParamDO::getPlanId, planId);
    }

    default List<MesWmStockTakingPlanParamDO> selectListByPlanIdAndType(Long planId, Integer type) {
        return selectList(new LambdaQueryWrapperX<MesWmStockTakingPlanParamDO>()
                .eq(MesWmStockTakingPlanParamDO::getPlanId, planId)
                .eq(MesWmStockTakingPlanParamDO::getType, type)
                .orderByAsc(MesWmStockTakingPlanParamDO::getId));
    }

}
