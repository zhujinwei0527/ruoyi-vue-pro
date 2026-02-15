package cn.iocoder.yudao.module.mes.service.md.item;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.md.item.vo.MesMdItemPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.item.vo.MesMdItemRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.item.vo.MesMdItemSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemTypeDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.item.MesMdItemMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 物料产品 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesMdItemServiceImpl implements MesMdItemService {

    @Resource
    private MesMdItemMapper itemMapper;

    @Resource
    private MesMdItemTypeService itemTypeService;

    @Override
    public Long createItem(MesMdItemSaveReqVO createReqVO) {
        // 校验物料编码的唯一性
        validateItemCodeUnique(null, createReqVO.getCode());
        // 校验物料名称的唯一性
        validateItemNameUnique(null, createReqVO.getName());
        // 校验物料分类存在
        validateItemTypeExists(createReqVO.getItemTypeId());

        // 插入
        MesMdItemDO item = BeanUtils.toBean(createReqVO, MesMdItemDO.class);
        // 如果未启用安全库存，清零库存上下限
        clearStockIfNotSafe(item);
        itemMapper.insert(item);
        // 返回
        return item.getId();
    }

    @Override
    public void updateItem(MesMdItemSaveReqVO updateReqVO) {
        // 校验存在
        validateItemExists(updateReqVO.getId());
        // 校验物料编码的唯一性
        validateItemCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 校验物料名称的唯一性
        validateItemNameUnique(updateReqVO.getId(), updateReqVO.getName());
        // 校验物料分类存在
        validateItemTypeExists(updateReqVO.getItemTypeId());

        // 更新
        MesMdItemDO updateObj = BeanUtils.toBean(updateReqVO, MesMdItemDO.class);
        // 如果未启用安全库存，清零库存上下限
        clearStockIfNotSafe(updateObj);
        itemMapper.updateById(updateObj);
    }

    @Override
    public void deleteItem(Long id) {
        // 校验存在
        validateItemExists(id);
        // 删除
        itemMapper.deleteById(id);
    }

    private void validateItemExists(Long id) {
        if (itemMapper.selectById(id) == null) {
            throw exception(MD_ITEM_NOT_EXISTS);
        }
    }

    private void validateItemCodeUnique(Long id, String code) {
        MesMdItemDO item = itemMapper.selectByCode(code);
        if (item == null) {
            return;
        }
        if (id == null) {
            throw exception(MD_ITEM_CODE_DUPLICATE);
        }
        if (!Objects.equals(item.getId(), id)) {
            throw exception(MD_ITEM_CODE_DUPLICATE);
        }
    }

    private void validateItemNameUnique(Long id, String name) {
        MesMdItemDO item = itemMapper.selectByName(name);
        if (item == null) {
            return;
        }
        if (id == null) {
            throw exception(MD_ITEM_NAME_DUPLICATE);
        }
        if (!Objects.equals(item.getId(), id)) {
            throw exception(MD_ITEM_NAME_DUPLICATE);
        }
    }

    private void validateItemTypeExists(Long itemTypeId) {
        if (itemTypeService.getItemType(itemTypeId) == null) {
            throw exception(MD_ITEM_TYPE_NOT_EXISTS);
        }
    }

    /**
     * 如果未启用安全库存，清零库存上下限
     */
    private void clearStockIfNotSafe(MesMdItemDO item) {
        if (Boolean.TRUE.equals(item.getSafeStockFlag())) {
            return;
        }
        item.setMinStock(BigDecimal.ZERO);
        item.setMaxStock(BigDecimal.ZERO);
    }

    @Override
    public MesMdItemDO getItem(Long id) {
        return itemMapper.selectById(id);
    }

    @Override
    public PageResult<MesMdItemRespVO> getItemVOPage(MesMdItemPageReqVO pageReqVO) {
        PageResult<MesMdItemDO> pageResult = itemMapper.selectPage(pageReqVO);
        return new PageResult<>(buildItemVOList(pageResult.getList()), pageResult.getTotal());
    }

    @Override
    public List<MesMdItemRespVO> getItemVOListByStatus(Integer status) {
        List<MesMdItemDO> list = itemMapper.selectListByStatus(status);
        return buildItemVOList(list);
    }

    @Override
    public List<MesMdItemRespVO> getItemVOList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<MesMdItemDO> list = itemMapper.selectByIds(ids);
        return buildItemVOList(list);
    }

    private List<MesMdItemRespVO> buildItemVOList(List<MesMdItemDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Map<Long, MesMdItemTypeDO> itemTypeMap = itemTypeService.getItemTypeMap(
                convertSet(list, MesMdItemDO::getItemTypeId));
        return BeanUtils.toBean(list, MesMdItemRespVO.class, item -> {
            MapUtils.findAndThen(itemTypeMap, item.getItemTypeId(),
                    itemType -> {
                        item.setItemTypeName(itemType.getName());
                        item.setItemOrProduct(itemType.getItemOrProduct());
                    });
        });
    }

    @Override
    public Long getItemCountByItemTypeId(Long itemTypeId) {
        return itemMapper.selectCountByItemTypeId(itemTypeId);
    }

}
