package cn.iocoder.yudao.module.mes.dal.mysql.wm.productsales;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productsales.MesWmProductSalesLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 销售出库单行 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesWmProductSalesLineMapper extends BaseMapperX<MesWmProductSalesLineDO> {

    default List<MesWmProductSalesLineDO> selectListBySalesId(Long salesId) {
        return selectList(MesWmProductSalesLineDO::getSalesId, salesId);
    }

    default void deleteBySalesId(Long salesId) {
        delete(MesWmProductSalesLineDO::getSalesId, salesId);
    }

    default cn.iocoder.yudao.framework.common.pojo.PageResult<MesWmProductSalesLineDO> selectPage(
            cn.iocoder.yudao.module.mes.controller.admin.wm.productsales.vo.line.MesWmProductSalesLinePageReqVO reqVO) {
        return selectPage(reqVO, new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<MesWmProductSalesLineDO>()
                .eqIfPresent(MesWmProductSalesLineDO::getSalesId, reqVO.getSalesId())
                .orderByDesc(MesWmProductSalesLineDO::getId));
    }

}
