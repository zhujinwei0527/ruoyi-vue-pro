package cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDetailDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * MES 领料出库明细 Mapper
 */
@Mapper
public interface MesWmProductionIssueDetailMapper extends BaseMapperX<MesWmProductionIssueDetailDO> {

    default List<MesWmProductionIssueDetailDO> selectListByIssueId(Long issueId) {
        return selectList(MesWmProductionIssueDetailDO::getIssueId, issueId);
    }

    default List<MesWmProductionIssueDetailDO> selectListByIssueIds(Collection<Long> issueIds) {
        return selectList(MesWmProductionIssueDetailDO::getIssueId, issueIds);
    }

    default List<MesWmProductionIssueDetailDO> selectListByLineId(Long lineId) {
        return selectList(MesWmProductionIssueDetailDO::getLineId, lineId);
    }

    default void deleteByIssueId(Long issueId) {
        delete(MesWmProductionIssueDetailDO::getIssueId, issueId);
    }

}
