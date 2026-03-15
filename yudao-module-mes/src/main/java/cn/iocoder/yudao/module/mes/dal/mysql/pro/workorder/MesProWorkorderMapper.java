package cn.iocoder.yudao.module.mes.dal.mysql.pro.workorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.MesProWorkOrderPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 生产工单 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesProWorkOrderMapper extends BaseMapperX<MesProWorkOrderDO> {

    default PageResult<MesProWorkOrderDO> selectPage(MesProWorkOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesProWorkOrderDO>()
                .likeIfPresent(MesProWorkOrderDO::getCode, reqVO.getCode())
                .likeIfPresent(MesProWorkOrderDO::getName, reqVO.getName())
                .eqIfPresent(MesProWorkOrderDO::getType, reqVO.getType())
                .eqIfPresent(MesProWorkOrderDO::getStatus, reqVO.getStatus())
                .likeIfPresent(MesProWorkOrderDO::getOrderSourceCode, reqVO.getOrderSourceCode())
                .eqIfPresent(MesProWorkOrderDO::getProductId, reqVO.getProductId())
                .eqIfPresent(MesProWorkOrderDO::getClientId, reqVO.getClientId())
                .betweenIfPresent(MesProWorkOrderDO::getRequestDate, reqVO.getRequestDate())
                .orderByDesc(MesProWorkOrderDO::getId));
    }

    default MesProWorkOrderDO selectByCode(String code) {
        return selectOne(MesProWorkOrderDO::getCode, code);
    }

}
