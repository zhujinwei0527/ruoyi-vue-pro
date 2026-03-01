package cn.iocoder.yudao.module.mes.dal.mysql.wm.productrecpt;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt.MesWmProductRecptDetailDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 产品收货单明细 Mapper
 */
@Mapper
public interface MesWmProductRecptDetailMapper extends BaseMapperX<MesWmProductRecptDetailDO> {

    default List<MesWmProductRecptDetailDO> selectListByRecptId(Long recptId) {
        return selectList(MesWmProductRecptDetailDO::getRecptId, recptId);
    }

    default List<MesWmProductRecptDetailDO> selectListByLineId(Long lineId) {
        return selectList(MesWmProductRecptDetailDO::getLineId, lineId);
    }

    default void deleteByRecptId(Long recptId) {
        delete(MesWmProductRecptDetailDO::getRecptId, recptId);
    }

    default void deleteByLineId(Long lineId) {
        delete(MesWmProductRecptDetailDO::getLineId, lineId);
    }

}
