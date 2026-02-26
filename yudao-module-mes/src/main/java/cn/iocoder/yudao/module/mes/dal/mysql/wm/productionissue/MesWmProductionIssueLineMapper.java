package cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * MES 领料出库单行 Mapper
 */
@Mapper
public interface MesWmProductionIssueLineMapper extends BaseMapperX<MesWmProductionIssueLineDO> {

    default List<MesWmProductionIssueLineDO> selectListByIssueId(Long issueId) {
        return selectList(MesWmProductionIssueLineDO::getIssueId, issueId);
    }

    default List<MesWmProductionIssueLineDO> selectListByIssueIds(Collection<Long> issueIds) {
        return selectList(MesWmProductionIssueLineDO::getIssueId, issueIds);
    }

    default void deleteByIssueId(Long issueId) {
        delete(MesWmProductionIssueLineDO::getIssueId, issueId);
    }

}
