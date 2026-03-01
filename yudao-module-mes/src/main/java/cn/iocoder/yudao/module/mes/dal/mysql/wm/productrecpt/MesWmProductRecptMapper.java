package cn.iocoder.yudao.module.mes.dal.mysql.wm.productrecpt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.MesWmProductRecptPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt.MesWmProductRecptDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 产品收货单 Mapper
 */
@Mapper
public interface MesWmProductRecptMapper extends BaseMapperX<MesWmProductRecptDO> {

    default PageResult<MesWmProductRecptDO> selectPage(MesWmProductRecptPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmProductRecptDO>()
                .likeIfPresent(MesWmProductRecptDO::getCode, reqVO.getCode())
                .likeIfPresent(MesWmProductRecptDO::getName, reqVO.getName())
                .eqIfPresent(MesWmProductRecptDO::getWorkOrderId, reqVO.getWorkOrderId())
                .eqIfPresent(MesWmProductRecptDO::getItemId, reqVO.getItemId())
                .orderByDesc(MesWmProductRecptDO::getId));
    }

    default MesWmProductRecptDO selectByCode(String code) {
        return selectOne(MesWmProductRecptDO::getCode, code);
    }

}
