package cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssuePageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 领料出库单 Mapper
 */
@Mapper
public interface MesWmProductionIssueMapper extends BaseMapperX<MesWmProductionIssueDO> {

    default PageResult<MesWmProductionIssueDO> selectPage(MesWmProductionIssuePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmProductionIssueDO>()
                .likeIfPresent(MesWmProductionIssueDO::getCode, reqVO.getCode())
                .likeIfPresent(MesWmProductionIssueDO::getName, reqVO.getName())
                .eqIfPresent(MesWmProductionIssueDO::getWorkstationId, reqVO.getWorkstationId())
                .eqIfPresent(MesWmProductionIssueDO::getWorkOrderId, reqVO.getWorkOrderId())
                .eqIfPresent(MesWmProductionIssueDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MesWmProductionIssueDO::getIssueDate, reqVO.getIssueDate())
                .orderByDesc(MesWmProductionIssueDO::getId));
    }

}
