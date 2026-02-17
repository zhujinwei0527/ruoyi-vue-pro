package cn.iocoder.yudao.module.mes.service.wm.warehouse;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.location.MesWmWarehouseLocationPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.location.MesWmWarehouseLocationSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

// TODO @AI：方法注释；
/**
 * MES 库区 Service 接口
 */
public interface MesWmWarehouseLocationService {

    Long createWarehouseLocation(@Valid MesWmWarehouseLocationSaveReqVO createReqVO);

    void updateWarehouseLocation(@Valid MesWmWarehouseLocationSaveReqVO updateReqVO);

    void deleteWarehouseLocation(Long id);

    MesWmWarehouseLocationDO getWarehouseLocation(Long id);

    PageResult<MesWmWarehouseLocationDO> getWarehouseLocationPage(MesWmWarehouseLocationPageReqVO pageReqVO);

    List<MesWmWarehouseLocationDO> getWarehouseLocationList(Long warehouseId, Integer status);

    List<MesWmWarehouseLocationDO> getWarehouseLocationList(Collection<Long> ids);

    default Map<Long, MesWmWarehouseLocationDO> getWarehouseLocationMap(Collection<Long> ids) {
        return convertMap(getWarehouseLocationList(ids), MesWmWarehouseLocationDO::getId);
    }

}
