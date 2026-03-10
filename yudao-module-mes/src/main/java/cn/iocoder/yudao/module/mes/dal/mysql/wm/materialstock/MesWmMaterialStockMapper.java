package cn.iocoder.yudao.module.mes.dal.mysql.wm.materialstock;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialstock.vo.MesWmMaterialStockListReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialstock.vo.MesWmMaterialStockPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialstock.MesWmMaterialStockDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * MES 库存台账 Mapper
 */
@Mapper
public interface MesWmMaterialStockMapper extends BaseMapperX<MesWmMaterialStockDO> {

    default PageResult<MesWmMaterialStockDO> selectPage(MesWmMaterialStockPageReqVO reqVO,
                                                         Collection<Long> itemTypeIds,
                                                         Collection<Long> itemIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmMaterialStockDO>()
                .inIfPresent(MesWmMaterialStockDO::getItemTypeId, itemTypeIds)
                .inIfPresent(MesWmMaterialStockDO::getItemId, itemIds)
                .eqIfPresent(MesWmMaterialStockDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(MesWmMaterialStockDO::getLocationId, reqVO.getLocationId())
                .eqIfPresent(MesWmMaterialStockDO::getFrozen, reqVO.getFrozen())
                .ne(MesWmMaterialStockDO::getQuantityOnhand, BigDecimal.ZERO)
                .orderByAsc(MesWmMaterialStockDO::getRecptDate));
    }

    default Long selectCountByWarehouseId(Long warehouseId) {
        return selectCount(MesWmMaterialStockDO::getWarehouseId, warehouseId);
    }

    default Long selectCountByLocationId(Long locationId) {
        return selectCount(MesWmMaterialStockDO::getLocationId, locationId);
    }

    default Long selectCountByAreaId(Long areaId) {
        return selectCount(MesWmMaterialStockDO::getAreaId, areaId);
    }

    default List<MesWmMaterialStockDO> selectListByIds(Collection<Long> ids) {
        return selectByIds(ids);
    }

    // TODO @AI：参考别的模块，最好使用 update某个字段，有例子的；然后看看，有没可能，尽量不写字段，而是通过 lamba 表达式获得名字。
    default void incrQuantityOnhand(Long id, BigDecimal quantity) {
        update(null, new LambdaUpdateWrapper<MesWmMaterialStockDO>()
                .eq(MesWmMaterialStockDO::getId, id)
                .setSql("quantity_onhand = quantity_onhand + " + quantity));
    }

    default MesWmMaterialStockDO selectByCompositeKey(Long itemId, Long warehouseId, Long locationId,
                                                       Long areaId, Long batchId) {
        return selectOne(new LambdaQueryWrapperX<MesWmMaterialStockDO>()
                .eqIfPresent(MesWmMaterialStockDO::getItemId, itemId)
                .eqIfPresent(MesWmMaterialStockDO::getWarehouseId, warehouseId)
                .eqIfPresent(MesWmMaterialStockDO::getLocationId, locationId)
                .eqIfPresent(MesWmMaterialStockDO::getAreaId, areaId)
                .eqIfPresent(MesWmMaterialStockDO::getBatchId, batchId));
    }

    default List<MesWmMaterialStockDO> selectListForStockTaking(MesWmMaterialStockListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<MesWmMaterialStockDO>()
                .eqIfPresent(MesWmMaterialStockDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(MesWmMaterialStockDO::getLocationId, reqVO.getLocationId())
                .eqIfPresent(MesWmMaterialStockDO::getAreaId, reqVO.getAreaId())
                .eqIfPresent(MesWmMaterialStockDO::getItemId, reqVO.getItemId())
                .eqIfPresent(MesWmMaterialStockDO::getBatchId, reqVO.getBatchId())
                .geIfPresent(MesWmMaterialStockDO::getUpdateTime, reqVO.getStartTime())
                .leIfPresent(MesWmMaterialStockDO::getUpdateTime, reqVO.getEndTime())
                .ne(MesWmMaterialStockDO::getQuantityOnhand, BigDecimal.ZERO) // TODO @芋艿：需要在考虑下，要不要支持；
                .orderByAsc(MesWmMaterialStockDO::getRecptDate));
    }

}
