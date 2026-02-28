package cn.iocoder.yudao.module.mes.dal.mysql.wm.returnvendor;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnvendor.vo.MesWmReturnVendorPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnvendor.MesWmReturnVendorDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 供应商退货单 Mapper
 */
@Mapper
public interface MesWmReturnVendorMapper extends BaseMapperX<MesWmReturnVendorDO> {

    default PageResult<MesWmReturnVendorDO> selectPage(MesWmReturnVendorPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmReturnVendorDO>()
                .likeIfPresent(MesWmReturnVendorDO::getCode, reqVO.getCode())
                .likeIfPresent(MesWmReturnVendorDO::getName, reqVO.getName())
                .eqIfPresent(MesWmReturnVendorDO::getVendorId, reqVO.getVendorId())
                .eqIfPresent(MesWmReturnVendorDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MesWmReturnVendorDO::getReturnDate, reqVO.getReturnDate())
                .orderByDesc(MesWmReturnVendorDO::getId));
    }

}
