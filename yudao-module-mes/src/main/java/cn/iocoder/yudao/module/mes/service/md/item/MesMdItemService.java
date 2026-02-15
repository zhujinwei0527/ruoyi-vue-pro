package cn.iocoder.yudao.module.mes.service.md.item;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.md.item.vo.MesMdItemPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.item.vo.MesMdItemRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.item.vo.MesMdItemSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * MES 物料产品 Service 接口
 *
 * @author 芋道源码
 */
public interface MesMdItemService {

    /**
     * 创建物料产品
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createItem(@Valid MesMdItemSaveReqVO createReqVO);

    /**
     * 更新物料产品
     *
     * @param updateReqVO 更新信息
     */
    void updateItem(@Valid MesMdItemSaveReqVO updateReqVO);

    /**
     * 删除物料产品
     *
     * @param id 编号
     */
    void deleteItem(Long id);

    /**
     * 获得物料产品
     *
     * @param id 编号
     * @return 物料产品
     */
    MesMdItemDO getItem(Long id);

    /**
     * 获得物料产品 VO 分页
     *
     * @param pageReqVO 分页查询
     * @return 物料产品分页
     */
    PageResult<MesMdItemRespVO> getItemVOPage(MesMdItemPageReqVO pageReqVO);

    /**
     * 获得指定状态的物料产品 VO 列表
     *
     * @param status 状态
     * @return 物料产品 VO 列表
     */
    List<MesMdItemRespVO> getItemVOListByStatus(Integer status);

    /**
     * 获得物料产品 VO 列表
     *
     * @param ids 编号数组
     * @return 物料产品 VO 列表
     */
    List<MesMdItemRespVO> getItemVOList(Collection<Long> ids);

    /**
     * 获得物料产品 VO Map
     *
     * @param ids 编号数组
     * @return 物料产品 VO Map
     */
    default Map<Long, MesMdItemRespVO> getItemVOMap(Collection<Long> ids) {
        return convertMap(getItemVOList(ids), MesMdItemRespVO::getId);
    }

    /**
     * 基于物料分类编号，获得物料数量
     *
     * @param itemTypeId 物料分类编号
     * @return 物料数量
     */
    Long getItemCountByItemTypeId(Long itemTypeId);

}
