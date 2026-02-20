package cn.iocoder.yudao.module.mes.service.wm.warehouse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.MesWmWarehousePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.MesWmWarehouseSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.warehouse.MesWmWarehouseMapper;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.wm.materialstock.MesWmMaterialStockService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 仓库 Service 实现类
 */
@Service
@Validated
public class MesWmWarehouseServiceImpl implements MesWmWarehouseService {

    @Resource
    private MesWmWarehouseMapper warehouseMapper;

    @Resource
    private MesWmWarehouseLocationService locationService;
    @Resource
    private MesMdWorkstationService workstationService;
    @Resource
    private MesWmMaterialStockService materialStockService;

    @Override
    public Long createWarehouse(MesWmWarehouseSaveReqVO createReqVO) {
        // 校验编码唯一
        validateWarehouseCodeUnique(null, createReqVO.getCode());
        // 校验名称唯一
        validateWarehouseNameUnique(null, createReqVO.getName());

        // 插入
        MesWmWarehouseDO warehouse = BeanUtils.toBean(createReqVO, MesWmWarehouseDO.class);
        warehouseMapper.insert(warehouse);
        return warehouse.getId();
    }

    @Override
    public void updateWarehouse(MesWmWarehouseSaveReqVO updateReqVO) {
        // 校验存在
        validateWarehouseExists(updateReqVO.getId());
        // 校验编码唯一
        validateWarehouseCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 校验名称唯一
        validateWarehouseNameUnique(updateReqVO.getId(), updateReqVO.getName());

        // 更新
        MesWmWarehouseDO updateObj = BeanUtils.toBean(updateReqVO, MesWmWarehouseDO.class);
        warehouseMapper.updateById(updateObj);
    }

    @Override
    public void deleteWarehouse(Long id) {
        // 校验存在
        validateWarehouseExists(id);
        // 校验是否有库区
        if (locationService.getWarehouseLocationCountByWarehouseId(id) > 0) {
            throw exception(WM_WAREHOUSE_HAS_LOCATION);
        }
        // 校验是否被工作站引用
        if (workstationService.getWorkstationCountByWarehouseId(id) > 0) {
            throw exception(WM_WAREHOUSE_HAS_WORKSTATION);
        }
        // 校验是否有库存记录
        if (materialStockService.getMaterialStockCountByWarehouseId(id) > 0) {
            throw exception(WM_WAREHOUSE_HAS_MATERIAL_STOCK);
        }

        // 删除
        warehouseMapper.deleteById(id);
    }

    @Override
    public MesWmWarehouseDO validateWarehouseExists(Long id) {
        MesWmWarehouseDO warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw exception(WM_WAREHOUSE_NOT_EXISTS);
        }
        return warehouse;
    }

    private void validateWarehouseCodeUnique(Long id, String code) {
        MesWmWarehouseDO warehouse = warehouseMapper.selectByCode(code);
        if (warehouse == null) {
            return;
        }
        if (ObjUtil.notEqual(warehouse.getId(), id)) {
            throw exception(WM_WAREHOUSE_CODE_DUPLICATE);
        }
    }

    private void validateWarehouseNameUnique(Long id, String name) {
        MesWmWarehouseDO warehouse = warehouseMapper.selectByName(name);
        if (warehouse == null) {
            return;
        }
        if (ObjUtil.notEqual(warehouse.getId(), id)) {
            throw exception(WM_WAREHOUSE_NAME_DUPLICATE);
        }
    }

    @Override
    public MesWmWarehouseDO getWarehouse(Long id) {
        return warehouseMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmWarehouseDO> getWarehousePage(MesWmWarehousePageReqVO pageReqVO) {
        return warehouseMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmWarehouseDO> getWarehouseSimpleList() {
        return warehouseMapper.selectSimpleList();
    }

    @Override
    public List<MesWmWarehouseDO> getWarehouseList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return warehouseMapper.selectListByIds(ids);
    }

}
