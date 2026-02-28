package cn.iocoder.yudao.module.mes.dal.mysql.wm.returnvendor;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnvendor.vo.line.MesWmReturnVendorLinePageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnvendor.MesWmReturnVendorLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 供应商退货单行 Mapper
 */
@Mapper
public interface MesWmReturnVendorLineMapper extends BaseMapperX<MesWmReturnVendorLineDO> {

    default PageResult<MesWmReturnVendorLineDO> selectPage(MesWmReturnVendorLinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmReturnVendorLineDO>()
                .eqIfPresent(MesWmReturnVendorLineDO::getReturnVendorId, reqVO.getReturnVendorId())
                .orderByDesc(MesWmReturnVendorLineDO::getId));
    }

    default List<MesWmReturnVendorLineDO> selectListByReturnVendorId(Long returnVendorId) {
        return selectList(MesWmReturnVendorLineDO::getReturnVendorId, returnVendorId);
    }

    default void deleteByReturnVendorId(Long returnVendorId) {
        delete(MesWmReturnVendorLineDO::getReturnVendorId, returnVendorId);
    }

}
