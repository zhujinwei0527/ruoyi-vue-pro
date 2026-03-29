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
                .likeIfPresent(MesWmMaterialStockDO::getBatchCode, reqVO.getBatchCode())
                .eqIfPresent(MesWmMaterialStockDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(MesWmMaterialStockDO::getLocationId, reqVO.getLocationId())
                .eqIfPresent(MesWmMaterialStockDO::getFrozen, reqVO.getFrozen())
                .ne(MesWmMaterialStockDO::getQuantity, BigDecimal.ZERO)
                .orderByAsc(MesWmMaterialStockDO::getReceiptTime));
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

    /**
     * 增量更新库存数量
     *
     * @param id        库存记录编号
     * @param count     变动数量（正数=增加，负数=扣减）
     * @param checkFlag 是否校验库存充足（为 true 时扣减不允许变为负数）
     * @return 影响行数
     */
    default int updateQuantity(Long id, BigDecimal count, boolean checkFlag) {
        LambdaUpdateWrapper<MesWmMaterialStockDO> updateWrapper = new LambdaUpdateWrapper<MesWmMaterialStockDO>()
                .eq(MesWmMaterialStockDO::getId, id)
                .setSql("quantity = quantity + " + count);
        if (checkFlag && count.compareTo(BigDecimal.ZERO) < 0) {
            updateWrapper.ge(MesWmMaterialStockDO::getQuantity, count.abs()); // CAS 防负库存
        }
        return update(null, updateWrapper);
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
                .ne(MesWmMaterialStockDO::getQuantity, BigDecimal.ZERO) // TODO @芋艿：需要在考虑下，要不要支持；
                .orderByAsc(MesWmMaterialStockDO::getReceiptTime));
    }

}
