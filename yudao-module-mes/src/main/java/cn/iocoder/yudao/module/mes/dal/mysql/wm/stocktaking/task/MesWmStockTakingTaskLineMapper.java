package cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.task;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.MesWmStockTakingTaskLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * MES 盘点任务行 Mapper
 */
@Mapper
public interface MesWmStockTakingTaskLineMapper extends BaseMapperX<MesWmStockTakingTaskLineDO> {

    default List<MesWmStockTakingTaskLineDO> selectListByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<MesWmStockTakingTaskLineDO>()
                .eq(MesWmStockTakingTaskLineDO::getTaskId, taskId)
                .orderByAsc(MesWmStockTakingTaskLineDO::getId));
    }

    default List<MesWmStockTakingTaskLineDO> selectListByTaskIds(Collection<Long> taskIds) {
        return selectList(MesWmStockTakingTaskLineDO::getTaskId, taskIds);
    }

    default List<MesWmStockTakingTaskLineDO> selectListByIds(Collection<Long> ids) {
        return selectByIds(ids);
    }

    default List<MesWmStockTakingTaskLineDO> selectListByTaskIdAndIds(Long taskId, Set<Long> ids) {
        return selectList(new LambdaQueryWrapperX<MesWmStockTakingTaskLineDO>()
                .eq(MesWmStockTakingTaskLineDO::getTaskId, taskId)
                .inIfPresent(MesWmStockTakingTaskLineDO::getId, ids)
                .orderByAsc(MesWmStockTakingTaskLineDO::getId));
    }

    default void deleteByTaskId(Long taskId) {
        delete(MesWmStockTakingTaskLineDO::getTaskId, taskId);
    }

}
