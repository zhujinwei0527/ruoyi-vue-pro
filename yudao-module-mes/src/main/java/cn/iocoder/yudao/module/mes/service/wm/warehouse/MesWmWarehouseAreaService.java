package cn.iocoder.yudao.module.mes.service.wm.warehouse;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.area.MesWmWarehouseAreaPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.area.MesWmWarehouseAreaSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

// TODO @AI：方法注释；
/**
 * MES 库位 Service 接口
 */
public interface MesWmWarehouseAreaService {

    Long createWarehouseArea(@Valid MesWmWarehouseAreaSaveReqVO createReqVO);

    void updateWarehouseArea(@Valid MesWmWarehouseAreaSaveReqVO updateReqVO);

    void deleteWarehouseArea(Long id);

    MesWmWarehouseAreaDO getWarehouseArea(Long id);

    PageResult<MesWmWarehouseAreaDO> getWarehouseAreaPage(MesWmWarehouseAreaPageReqVO pageReqVO);

    List<MesWmWarehouseAreaDO> getWarehouseAreaList(Long locationId, Integer status);

    List<MesWmWarehouseAreaDO> getWarehouseAreaList(Collection<Long> ids);

    default Map<Long, MesWmWarehouseAreaDO> getWarehouseAreaMap(Collection<Long> ids) {
        return convertMap(getWarehouseAreaList(ids), MesWmWarehouseAreaDO::getId);
    }

}
