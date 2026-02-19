package cn.iocoder.yudao.module.mes.service.md.workstation;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkshopDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkstationMachineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkstationMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkstationToolMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkstationWorkerMapper;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseAreaService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseLocationService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 工作站 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesMdWorkstationServiceImpl implements MesMdWorkstationService {

    @Resource
    private MesMdWorkstationMapper workstationMapper;

    @Resource
    private MesMdWorkstationMachineMapper workstationMachineMapper;

    @Resource
    private MesMdWorkstationToolMapper workstationToolMapper;

    @Resource
    private MesMdWorkstationWorkerMapper workstationWorkerMapper;

    @Resource
    private MesMdWorkshopService workshopService;

    @Resource
    private MesWmWarehouseService warehouseService;

    @Resource
    private MesWmWarehouseLocationService locationService;

    @Resource
    private MesWmWarehouseAreaService areaService;

    @Override
    public Long createWorkstation(MesMdWorkstationSaveReqVO createReqVO) {
        // 校验编码唯一
        validateWorkstationCodeUnique(null, createReqVO.getCode());
        // 校验名称唯一
        validateWorkstationNameUnique(null, createReqVO.getName());
        // 校验车间存在
        validateWorkshopExists(createReqVO.getWorkshopId());
        // 校验仓库层级
        validateWarehouseHierarchy(createReqVO);

        // 插入
        MesMdWorkstationDO workstation = BeanUtils.toBean(createReqVO, MesMdWorkstationDO.class);
        workstationMapper.insert(workstation);
        return workstation.getId();
    }

    @Override
    public void updateWorkstation(MesMdWorkstationSaveReqVO updateReqVO) {
        // 校验存在
        validateWorkstationExists(updateReqVO.getId());
        // 校验编码唯一
        validateWorkstationCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 校验名称唯一
        validateWorkstationNameUnique(updateReqVO.getId(), updateReqVO.getName());
        // 校验车间存在
        validateWorkshopExists(updateReqVO.getWorkshopId());
        // 校验仓库层级
        validateWarehouseHierarchy(updateReqVO);

        // 更新
        MesMdWorkstationDO updateObj = BeanUtils.toBean(updateReqVO, MesMdWorkstationDO.class);
        workstationMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkstation(Long id) {
        // 校验存在
        validateWorkstationExists(id);

        // 级联删除子资源
        workstationMachineMapper.deleteByWorkstationId(id);
        workstationToolMapper.deleteByWorkstationId(id);
        workstationWorkerMapper.deleteByWorkstationId(id);
        // 删除工作站
        workstationMapper.deleteById(id);
    }

    private void validateWorkstationExists(Long id) {
        if (workstationMapper.selectById(id) == null) {
            throw exception(MD_WORKSTATION_NOT_EXISTS);
        }
    }

    private void validateWorkshopExists(Long workshopId) {
        MesMdWorkshopDO workshop = workshopService.getWorkshop(workshopId);
        if (workshop == null) {
            throw exception(MD_WORKSHOP_NOT_EXISTS);
        }
    }

    /**
     * 校验仓库层级关系
     *
     * 仓库层级结构：仓库 -> 库位 -> 库区
     * 校验规则：
     * 1. 如果填写了库位，必须填写仓库，且库位必须属于该仓库
     * 2. 如果填写了库区，必须填写库位，且库区必须属于该库位
     *
     * @param reqVO 工作站保存请求对象
     */
    private void validateWarehouseHierarchy(MesMdWorkstationSaveReqVO reqVO) {
        // 1.1 获取仓库、库位、库区 ID
        Long warehouseId = reqVO.getWarehouseId();
        Long locationId = reqVO.getLocationId();
        Long areaId = reqVO.getAreaId();
        // 1.2  如果都为空，则无需校验
        if (warehouseId == null && locationId == null && areaId == null) {
            return;
        }

        // 2. 校验仓库是否存在
        if (warehouseId != null) {
            warehouseService.validateWarehouseExists(warehouseId);
        }

        // 3. 校验库位：如果填写了库位，必须填写仓库，且库位必须属于该仓库
        MesWmWarehouseLocationDO location;
        if (locationId != null) {
            // 3.1 校验库位是否存在
            location = locationService.validateWarehouseLocationExists(locationId);
            // 3.2 校验必须填写仓库
            if (warehouseId == null) {
                throw exception(WM_WAREHOUSE_REQUIRED);
            }
            // 3.3 校验库位是否属于该仓库
            if (ObjUtil.notEqual(location.getWarehouseId(), warehouseId)) {
                throw exception(WM_WAREHOUSE_LOCATION_RELATION_INVALID);
            }
        }

        // 4. 校验库区：如果填写了库区，必须填写库位，且库区必须属于该库位
        if (areaId == null) {
            return;
        }
        // 4.1 校验库区是否存在
        MesWmWarehouseAreaDO area = areaService.validateWarehouseAreaExists(areaId);
        // 4.2 校验必须填写库位
        if (locationId == null) {
            throw exception(WM_WAREHOUSE_LOCATION_REQUIRED);
        }
        // 4.3 校验库区是否属于该库位
        if (ObjUtil.notEqual(area.getLocationId(), locationId)) {
            throw exception(WM_WAREHOUSE_AREA_RELATION_INVALID);
        }
    }

    private void validateWorkstationCodeUnique(Long id, String code) {
        MesMdWorkstationDO workstation = workstationMapper.selectByCode(code);
        if (workstation == null) {
            return;
        }
        if (ObjUtil.notEqual(workstation.getId(), id)) {
            throw exception(MD_WORKSTATION_CODE_DUPLICATE);
        }
    }

    private void validateWorkstationNameUnique(Long id, String name) {
        MesMdWorkstationDO workstation = workstationMapper.selectByName(name);
        if (workstation == null) {
            return;
        }
        if (ObjUtil.notEqual(workstation.getId(), id)) {
            throw exception(MD_WORKSTATION_NAME_DUPLICATE);
        }
    }

    @Override
    public MesMdWorkstationDO getWorkstation(Long id) {
        return workstationMapper.selectById(id);
    }

    @Override
    public PageResult<MesMdWorkstationDO> getWorkstationPage(MesMdWorkstationPageReqVO pageReqVO) {
        return workstationMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesMdWorkstationDO> getWorkstationListByStatus(Integer status) {
        return workstationMapper.selectListByStatus(status);
    }

}
