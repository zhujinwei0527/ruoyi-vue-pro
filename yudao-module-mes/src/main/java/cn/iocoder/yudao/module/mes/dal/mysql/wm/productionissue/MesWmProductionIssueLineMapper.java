package cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.line.MesWmProductionIssueLinePageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 领料出库单行 Mapper
 */
@Mapper
public interface MesWmProductionIssueLineMapper extends BaseMapperX<MesWmProductionIssueLineDO> {

    default PageResult<MesWmProductionIssueLineDO> selectPage(MesWmProductionIssueLinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmProductionIssueLineDO>()
                .eqIfPresent(MesWmProductionIssueLineDO::getIssueId, reqVO.getIssueId())
                .orderByDesc(MesWmProductionIssueLineDO::getId));
    }

    default List<MesWmProductionIssueLineDO> selectListByIssueId(Long issueId) {
        return selectList(MesWmProductionIssueLineDO::getIssueId, issueId);
    }

    default void deleteByIssueId(Long issueId) {
        delete(MesWmProductionIssueLineDO::getIssueId, issueId);
    }

}
