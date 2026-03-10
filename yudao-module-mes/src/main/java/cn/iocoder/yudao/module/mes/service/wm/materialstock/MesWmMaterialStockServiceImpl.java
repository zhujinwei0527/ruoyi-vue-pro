package cn.iocoder.yudao.module.mes.service.wm.materialstock;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.SetUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialstock.vo.MesWmMaterialStockFreezeReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialstock.vo.MesWmMaterialStockListReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialstock.vo.MesWmMaterialStockPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemTypeDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialstock.MesWmMaterialStockDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.materialstock.MesWmMaterialStockMapper;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemTypeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_MATERIAL_STOCK_NOT_EXISTS;

/**
 * MES 库存台账 Service 实现类
 */
@Service
@Validated
public class MesWmMaterialStockServiceImpl implements MesWmMaterialStockService {

    @Resource
    private MesWmMaterialStockMapper materialStockMapper;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesMdItemTypeService itemTypeService;

    @Override
    public MesWmMaterialStockDO getMaterialStock(Long id) {
        return materialStockMapper.selectById(id);
    }

    @Override
    public MesWmMaterialStockDO validateMaterialStockExists(Long id) {
        MesWmMaterialStockDO stock = materialStockMapper.selectById(id);
        if (stock == null) {
            throw exception(WM_MATERIAL_STOCK_NOT_EXISTS);
        }
        return stock;
    }

    @Override
    public PageResult<MesWmMaterialStockDO> getMaterialStockPage(MesWmMaterialStockPageReqVO pageReqVO) {
        // 1.1 解析 itemTypeId：包含子分类
        Set<Long> itemTypeIds = null;
        if (pageReqVO.getItemTypeId() != null) {
            itemTypeIds = new HashSet<>();
            itemTypeIds.add(pageReqVO.getItemTypeId());
            List<MesMdItemTypeDO> children = itemTypeService.getItemTypeChildrenList(pageReqVO.getItemTypeId());
            itemTypeIds.addAll(convertSet(children, MesMdItemTypeDO::getId));
        }
        // 1.2 解析 itemId
        Set<Long> itemIds = null;
        if (pageReqVO.getItemId() != null) {
            itemIds = SetUtils.asSet(pageReqVO.getItemId());
        }

        // 2. 分页查询
        return materialStockMapper.selectPage(pageReqVO, itemTypeIds, itemIds);
    }

    @Override
    public void updateMaterialStockFrozen(MesWmMaterialStockFreezeReqVO updateReqVO) {
        // 校验存在
        validateMaterialStockExists(updateReqVO.getId());
        // 更新冻结状态
        MesWmMaterialStockDO updateObj = new MesWmMaterialStockDO();
        updateObj.setId(updateReqVO.getId());
        updateObj.setFrozen(updateReqVO.getFrozen());
        materialStockMapper.updateById(updateObj);
    }

    @Override
    public Long getMaterialStockCountByWarehouseId(Long warehouseId) {
        return materialStockMapper.selectCountByWarehouseId(warehouseId);
    }

    @Override
    public Long getMaterialStockCountByLocationId(Long locationId) {
        return materialStockMapper.selectCountByLocationId(locationId);
    }

    @Override
    public Long getMaterialStockCountByAreaId(Long areaId) {
        return materialStockMapper.selectCountByAreaId(areaId);
    }

    @Override
    public List<MesWmMaterialStockDO> getMaterialStockList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return materialStockMapper.selectListByIds(ids);
    }

    @Override
    public List<MesWmMaterialStockDO> getMaterialStockList(MesWmMaterialStockListReqVO reqVO) {
        return materialStockMapper.selectListForStockTaking(reqVO);
    }

    // TODO DONE @AI：increaseStock 方法命名合理，语义清晰
    @Override
    public void increaseStock(Long itemId, Long warehouseId, Long locationId, Long areaId,
                              Long batchId, BigDecimal quantity, Long vendorId,
                              LocalDateTime productionDate, LocalDateTime expireDate) {
        // 1. 查找已有库存记录
        MesWmMaterialStockDO stock = materialStockMapper.selectByCompositeKey(
                itemId, warehouseId, locationId, areaId, batchId);

        // 2a. 存在则增加数量
        if (stock != null) {
            materialStockMapper.incrQuantityOnhand(stock.getId(), quantity);
            return;
        }

        // 2. 不存在则新建
        MesMdItemDO item = itemService.validateItemExists(itemId);
        MesWmMaterialStockDO newStock = MesWmMaterialStockDO.builder()
                .itemId(itemId).itemTypeId(item.getItemTypeId()).unitMeasureId(item.getUnitMeasureId())
                .warehouseId(warehouseId).locationId(locationId).areaId(areaId)
                .batchId(batchId).vendorId(vendorId).quantityOnhand(quantity)
                .recptDate(LocalDateTime.now()).productionDate(productionDate).expireDate(expireDate).frozen(false)
                .build();
        materialStockMapper.insert(newStock);
    }

}
