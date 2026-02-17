package cn.iocoder.yudao.module.mes.service.wm.warehouse;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.MesWmWarehousePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.MesWmWarehouseSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

// TODO @AI：方法注释；
/**
 * MES 仓库 Service 接口
 */
public interface MesWmWarehouseService {

    Long createWarehouse(@Valid MesWmWarehouseSaveReqVO createReqVO);

    void updateWarehouse(@Valid MesWmWarehouseSaveReqVO updateReqVO);

    void deleteWarehouse(Long id);

    MesWmWarehouseDO getWarehouse(Long id);

    PageResult<MesWmWarehouseDO> getWarehousePage(MesWmWarehousePageReqVO pageReqVO);

    List<MesWmWarehouseDO> getWarehouseListByStatus(Integer status);

    List<MesWmWarehouseDO> getWarehouseList(Collection<Long> ids);

    default Map<Long, MesWmWarehouseDO> getWarehouseMap(Collection<Long> ids) {
        return convertMap(getWarehouseList(ids), MesWmWarehouseDO::getId);
    }

}
