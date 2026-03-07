package cn.iocoder.yudao.module.mes.service.wm.warehouse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.location.MesWmWarehouseLocationPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.location.MesWmWarehouseLocationSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.warehouse.MesWmWarehouseLocationMapper;
import cn.iocoder.yudao.module.mes.enums.wm.BarcodeBizTypeEnum;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.wm.barcode.MesWmBarcodeService;
import cn.iocoder.yudao.module.mes.service.wm.materialstock.MesWmMaterialStockService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 库区 Service 实现类
 */
@Service
@Validated
public class MesWmWarehouseLocationServiceImpl implements MesWmWarehouseLocationService {

    @Resource
    private MesWmWarehouseLocationMapper locationMapper;

    @Resource
    private MesWmWarehouseAreaService areaService;
    @Resource
    @Lazy
    private MesMdWorkstationService workstationService;
    @Resource
    private MesWmMaterialStockService materialStockService;
    @Resource
    @Lazy
    private MesWmWarehouseService warehouseService;
    @Resource
    private MesWmBarcodeService barcodeService;

    @Override
    public Long createWarehouseLocation(MesWmWarehouseLocationSaveReqVO createReqVO) {
        // 校验仓库存在
        warehouseService.validateWarehouseExists(createReqVO.getWarehouseId());
        // 校验编码唯一
        validateWarehouseLocationCodeUnique(null, createReqVO.getWarehouseId(), createReqVO.getCode());
        // 校验名称唯一
        validateWarehouseLocationNameUnique(null, createReqVO.getWarehouseId(), createReqVO.getName());

        // 插入
        MesWmWarehouseLocationDO location = BeanUtils.toBean(createReqVO, MesWmWarehouseLocationDO.class);
        locationMapper.insert(location);

        // 自动生成条码
        barcodeService.autoGenerateBarcode(BarcodeBizTypeEnum.LOCATION.getValue(),
                location.getId(), location.getCode(), location.getName());
        return location.getId();
    }

    @Override
    public void updateWarehouseLocation(MesWmWarehouseLocationSaveReqVO updateReqVO) {
        // 校验存在
        validateWarehouseLocationExists(updateReqVO.getId());
        // 校验仓库存在
        warehouseService.validateWarehouseExists(updateReqVO.getWarehouseId());
        // 校验编码唯一
        validateWarehouseLocationCodeUnique(updateReqVO.getId(), updateReqVO.getWarehouseId(), updateReqVO.getCode());
        // 校验名称唯一
        validateWarehouseLocationNameUnique(updateReqVO.getId(), updateReqVO.getWarehouseId(), updateReqVO.getName());

        // 更新
        MesWmWarehouseLocationDO updateObj = BeanUtils.toBean(updateReqVO, MesWmWarehouseLocationDO.class);
        locationMapper.updateById(updateObj);
    }

    @Override
    public void deleteWarehouseLocation(Long id) {
        // 校验存在
        validateWarehouseLocationExists(id);
        // 校验是否有库位
        if (areaService.getWarehouseAreaCountByLocationId(id) > 0) {
            throw exception(WM_WAREHOUSE_LOCATION_HAS_AREA);
        }
        // 校验是否被工作站引用
        if (workstationService.getWorkstationCountByLocationId(id) > 0) {
            throw exception(WM_WAREHOUSE_LOCATION_HAS_WORKSTATION);
        }
        // 校验是否有库存记录
        if (materialStockService.getMaterialStockCountByLocationId(id) > 0) {
            throw exception(WM_WAREHOUSE_LOCATION_HAS_MATERIAL_STOCK);
        }

        // 删除
        locationMapper.deleteById(id);
    }

    @Override
    public MesWmWarehouseLocationDO validateWarehouseLocationExists(Long id) {
        MesWmWarehouseLocationDO location = locationMapper.selectById(id);
        if (location == null) {
            throw exception(WM_WAREHOUSE_LOCATION_NOT_EXISTS);
        }
        return location;
    }

    private void validateWarehouseLocationCodeUnique(Long id, Long warehouseId, String code) {
        MesWmWarehouseLocationDO location = locationMapper.selectByCode(warehouseId, code);
        if (location == null) {
            return;
        }
        if (ObjUtil.notEqual(location.getId(), id)) {
            throw exception(WM_WAREHOUSE_LOCATION_CODE_DUPLICATE);
        }
    }

    private void validateWarehouseLocationNameUnique(Long id, Long warehouseId, String name) {
        MesWmWarehouseLocationDO location = locationMapper.selectByName(warehouseId, name);
        if (location == null) {
            return;
        }
        if (ObjUtil.notEqual(location.getId(), id)) {
            throw exception(WM_WAREHOUSE_LOCATION_NAME_DUPLICATE);
        }
    }

    @Override
    public MesWmWarehouseLocationDO getWarehouseLocation(Long id) {
        return locationMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmWarehouseLocationDO> getWarehouseLocationPage(MesWmWarehouseLocationPageReqVO pageReqVO) {
        return locationMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmWarehouseLocationDO> getWarehouseLocationSimpleList(Long warehouseId) {
        return locationMapper.selectSimpleList(warehouseId);
    }

    @Override
    public List<MesWmWarehouseLocationDO> getWarehouseLocationList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return locationMapper.selectListByIds(ids);
    }

    @Override
    public Long getWarehouseLocationCountByWarehouseId(Long warehouseId) {
        return locationMapper.selectCountByWarehouseId(warehouseId);
    }

}
