package cn.iocoder.yudao.module.mes.dal.mysql.pro.workorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.MesProWorkorderPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkorderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 生产工单 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesProWorkorderMapper extends BaseMapperX<MesProWorkorderDO> {

    default PageResult<MesProWorkorderDO> selectPage(MesProWorkorderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesProWorkorderDO>()
                .likeIfPresent(MesProWorkorderDO::getCode, reqVO.getCode())
                .likeIfPresent(MesProWorkorderDO::getName, reqVO.getName())
                .eqIfPresent(MesProWorkorderDO::getType, reqVO.getType())
                .eqIfPresent(MesProWorkorderDO::getOrderSourceType, reqVO.getOrderSourceType())
                .eqIfPresent(MesProWorkorderDO::getProductId, reqVO.getProductId())
                .eqIfPresent(MesProWorkorderDO::getClientId, reqVO.getClientId())
                .eqIfPresent(MesProWorkorderDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MesProWorkorderDO::getRequestDate, reqVO.getRequestDate())
                .orderByDesc(MesProWorkorderDO::getId));
    }

    default MesProWorkorderDO selectByCode(String code) {
        return selectOne(MesProWorkorderDO::getCode, code);
    }

}
