package cn.iocoder.yudao.module.mes.service.wm.warehouse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.area.MesWmWarehouseAreaPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.area.MesWmWarehouseAreaSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkstationMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.warehouse.MesWmWarehouseAreaMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 库位 Service 实现类
 */
@Service
@Validated
public class MesWmWarehouseAreaServiceImpl implements MesWmWarehouseAreaService {

    @Resource
    private MesWmWarehouseAreaMapper areaMapper;

    @Resource
    private MesMdWorkstationMapper workstationMapper;

    @Resource
    private MesWmWarehouseLocationService locationService;

    @Override
    public Long createWarehouseArea(MesWmWarehouseAreaSaveReqVO createReqVO) {
        // 校验库区存在
        locationService.validateWarehouseLocationExists(createReqVO.getLocationId());
        // 校验编码唯一
        validateWarehouseAreaCodeUnique(null, createReqVO.getLocationId(), createReqVO.getCode());
        // 校验名称唯一
        validateWarehouseAreaNameUnique(null, createReqVO.getLocationId(), createReqVO.getName());

        MesWmWarehouseAreaDO area = BeanUtils.toBean(createReqVO, MesWmWarehouseAreaDO.class);
        areaMapper.insert(area);
        return area.getId();
    }

    @Override
    public void updateWarehouseArea(MesWmWarehouseAreaSaveReqVO updateReqVO) {
        // 校验存在
        validateWarehouseAreaExists(updateReqVO.getId());
        // 校验库区存在
        locationService.validateWarehouseLocationExists(updateReqVO.getLocationId());
        // 校验编码唯一
        validateWarehouseAreaCodeUnique(updateReqVO.getId(), updateReqVO.getLocationId(), updateReqVO.getCode());
        // 校验名称唯一
        validateWarehouseAreaNameUnique(updateReqVO.getId(), updateReqVO.getLocationId(), updateReqVO.getName());

        MesWmWarehouseAreaDO updateObj = BeanUtils.toBean(updateReqVO, MesWmWarehouseAreaDO.class);
        areaMapper.updateById(updateObj);
    }

    @Override
    public void deleteWarehouseArea(Long id) {
        // 校验存在
        validateWarehouseAreaExists(id);
        // 校验是否被工作站引用
        if (ObjUtil.defaultIfNull(workstationMapper.selectCountByAreaId(id), 0L) > 0) {
            throw exception(WM_WAREHOUSE_AREA_HAS_WORKSTATION);
        }
        // DONE @芋艿：本轮范围不接入库存占用校验，待 mes_wm_material_stock 迁移后补充

        areaMapper.deleteById(id);
    }

    @Override
    public MesWmWarehouseAreaDO validateWarehouseAreaExists(Long id) {
        MesWmWarehouseAreaDO area = areaMapper.selectById(id);
        if (area == null) {
            throw exception(WM_WAREHOUSE_AREA_NOT_EXISTS);
        }
        return area;
    }

    private void validateWarehouseAreaCodeUnique(Long id, Long locationId, String code) {
        MesWmWarehouseAreaDO area = areaMapper.selectByCode(locationId, code);
        if (area == null) {
            return;
        }
        if (ObjUtil.notEqual(area.getId(), id)) {
            throw exception(WM_WAREHOUSE_AREA_CODE_DUPLICATE);
        }
    }

    private void validateWarehouseAreaNameUnique(Long id, Long locationId, String name) {
        MesWmWarehouseAreaDO area = areaMapper.selectByName(locationId, name);
        if (area == null) {
            return;
        }
        if (ObjUtil.notEqual(area.getId(), id)) {
            throw exception(WM_WAREHOUSE_AREA_NAME_DUPLICATE);
        }
    }

    @Override
    public MesWmWarehouseAreaDO getWarehouseArea(Long id) {
        return areaMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmWarehouseAreaDO> getWarehouseAreaPage(MesWmWarehouseAreaPageReqVO pageReqVO) {
        return areaMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmWarehouseAreaDO> getWarehouseAreaSimpleList(Long locationId) {
        return areaMapper.selectSimpleList(locationId);
    }

    @Override
    public List<MesWmWarehouseAreaDO> getWarehouseAreaList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return areaMapper.selectListByIds(ids);
    }

}
