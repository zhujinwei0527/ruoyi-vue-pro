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
     * 校验库存记录存在
     *
     * @param id 编号
     * @return 库存记录
     */
    MesWmMaterialStockDO validateMaterialStockExists(Long id);

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
     * 增加库存
     *
     * 按 itemId + warehouseId + locationId + areaId + batchId 查找已有库存，
     * 存在则增加 quantityOnhand；不存在则新建记录。
     *
     * @param itemId         物料编号
     * @param warehouseId    仓库编号
     * @param locationId     库区编号
     * @param areaId         库位编号
     * @param batchId        批次编号
     * @param quantity       增加数量
     * @param vendorId       供应商编号
     * @param productionDate 生产日期
     * @param expireDate     过期日期
     */
    void increaseStock(Long itemId, Long warehouseId, Long locationId, Long areaId,
                       Long batchId, BigDecimal quantity, Long vendorId,
                       LocalDateTime productionDate, LocalDateTime expireDate);

}
