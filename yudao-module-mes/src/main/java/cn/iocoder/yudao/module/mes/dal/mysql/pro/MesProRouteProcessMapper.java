package cn.iocoder.yudao.module.mes.dal.mysql.pro;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProcessDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 工艺路线工序 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesProRouteProcessMapper extends BaseMapperX<MesProRouteProcessDO> {

    default List<MesProRouteProcessDO> selectListByRouteId(Long routeId) {
        return selectList(new LambdaQueryWrapperX<MesProRouteProcessDO>()
                .eq(MesProRouteProcessDO::getRouteId, routeId)
                .orderByAsc(MesProRouteProcessDO::getSort));
    }

    default MesProRouteProcessDO selectByRouteIdAndSort(Long routeId, Integer sort) {
        return selectOne(new LambdaQueryWrapperX<MesProRouteProcessDO>()
                .eq(MesProRouteProcessDO::getRouteId, routeId)
                .eq(MesProRouteProcessDO::getSort, sort));
    }

    default MesProRouteProcessDO selectByRouteIdAndProcessId(Long routeId, Long processId) {
        return selectOne(new LambdaQueryWrapperX<MesProRouteProcessDO>()
                .eq(MesProRouteProcessDO::getRouteId, routeId)
                .eq(MesProRouteProcessDO::getProcessId, processId));
    }

    default MesProRouteProcessDO selectKeyProcessByRouteId(Long routeId) {
        return selectOne(new LambdaQueryWrapperX<MesProRouteProcessDO>()
                .eq(MesProRouteProcessDO::getRouteId, routeId)
                .eq(MesProRouteProcessDO::getKeyFlag, true));
    }

    default void deleteByRouteId(Long routeId) {
        delete(new LambdaQueryWrapperX<MesProRouteProcessDO>()
                .eq(MesProRouteProcessDO::getRouteId, routeId));
    }

    default List<MesProRouteProcessDO> selectListByProcessId(Long processId) {
        return selectList(MesProRouteProcessDO::getProcessId, processId);
    }

    default MesProRouteProcessDO findPreProcess(Long routeId, Integer sort) {
        return selectOne(new LambdaQueryWrapperX<MesProRouteProcessDO>()
                .eq(MesProRouteProcessDO::getRouteId, routeId)
                .lt(MesProRouteProcessDO::getSort, sort)
                .orderByDesc(MesProRouteProcessDO::getSort)
                // TODO @AI：limit 1，使用 mybatis utils
                .last("LIMIT 1"));
    }

    default MesProRouteProcessDO findNextProcess(Long routeId, Integer sort) {
        return selectOne(new LambdaQueryWrapperX<MesProRouteProcessDO>()
                .eq(MesProRouteProcessDO::getRouteId, routeId)
                .gt(MesProRouteProcessDO::getSort, sort)
                .orderByAsc(MesProRouteProcessDO::getSort)
                // TODO @AI：limit 1，使用 mybatis utils
                .last("LIMIT 1"));
    }

}
