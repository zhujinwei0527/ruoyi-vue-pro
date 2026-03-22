package cn.iocoder.yudao.module.mes.service.wm.materialstock;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialstock.vo.MesWmMaterialStockFreezeReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialstock.vo.MesWmMaterialStockListReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialstock.vo.MesWmMaterialStockPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialstock.MesWmMaterialStockDO;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * MES 库存台账 Service 接口
 */
public interface MesWmMaterialStockService {

    /**
     * 获得库存记录
     *
     * @param id 编号
     * @return 库存记录
     */
    MesWmMaterialStockDO getMaterialStock(Long id);

    /**
     * 获得库存台账分页
     *
     * @param pageReqVO 分页参数
     * @return 库存台账分页
     */
    PageResult<MesWmMaterialStockDO> getMaterialStockPage(MesWmMaterialStockPageReqVO pageReqVO);

    /**
     * 更新库存冻结状态
     *
     * @param updateReqVO 更新信息
     */
    void updateMaterialStockFrozen(@Valid MesWmMaterialStockFreezeReqVO updateReqVO);

    /**
     * 获得仓库下库存记录数量
     *
     * @param warehouseId 仓库编号
     * @return 记录数量
     */
    Long getMaterialStockCountByWarehouseId(Long warehouseId);

    /**
     * 获得库区下库存记录数量
     *
     * @param locationId 库区编号
     * @return 记录数量
     */
    Long getMaterialStockCountByLocationId(Long locationId);

    /**
     * 获得库位下库存记录数量
     *
     * @param areaId 库位编号
     * @return 记录数量
     */
    Long getMaterialStockCountByAreaId(Long areaId);

    /**
     * 按编号集合获得库存记录列表
     *
     * @param ids 编号集合
     * @return 库存记录列表
     */
    List<MesWmMaterialStockDO> getMaterialStockList(Collection<Long> ids);

    /**
     * 获得物料库存列表（用于盘点等场景）
     *
     * @param reqVO 查询条件
     * @return 物料库存列表
     */
    List<MesWmMaterialStockDO> getMaterialStockList(MesWmMaterialStockListReqVO reqVO);

    default Map<Long, MesWmMaterialStockDO> getMaterialStockMap(Collection<Long> ids) {
        return convertMap(getMaterialStockList(ids), MesWmMaterialStockDO::getId);
    }

    /**
     * 获取或创建库存记录（按组合键唯一）
     *
     * @param itemId         物料编号
     * @param warehouseId    仓库编号
     * @param locationId     库区编号
     * @param areaId         库位编号
     * @param batchId        批次编号
     * @param vendorId       供应商编号
     * @param receiptTime    入库时间（为空则默认当前时间）
     * @return 库存记录编号
     */
    Long getOrCreateMaterialStock(Long itemId, Long warehouseId, Long locationId, Long areaId,
                                  Long batchId, Long vendorId, LocalDateTime receiptTime);

    /**
     * 更新库存数量
     *
     * @param materialStockId 库存记录编号
     * @param quantity        变动数量（正数=增加，负数=扣减）
     * @param checkFlag       是否校验库存充足（为 true 且扣减后为负则报错）
     */
    void updateMaterialStockQuantity(Long materialStockId, BigDecimal quantity, boolean checkFlag);

}
