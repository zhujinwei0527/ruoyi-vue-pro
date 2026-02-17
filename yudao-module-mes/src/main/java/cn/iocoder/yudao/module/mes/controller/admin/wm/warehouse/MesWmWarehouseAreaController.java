package cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.area.MesWmWarehouseAreaPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.area.MesWmWarehouseAreaRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.area.MesWmWarehouseAreaSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseAreaService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseLocationService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - MES 库位")
@RestController
@RequestMapping("/mes/wm/warehouse-area")
@Validated
public class MesWmWarehouseAreaController {

    @Resource
    private MesWmWarehouseAreaService areaService;

    @Resource
    private MesWmWarehouseLocationService locationService;

    @Resource
    private MesWmWarehouseService warehouseService;

    @PostMapping("/create")
    @Operation(summary = "创建库位")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse-area:create')")
    public CommonResult<Long> createWarehouseArea(@Valid @RequestBody MesWmWarehouseAreaSaveReqVO createReqVO) {
        return success(areaService.createWarehouseArea(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新库位")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse-area:update')")
    public CommonResult<Boolean> updateWarehouseArea(@Valid @RequestBody MesWmWarehouseAreaSaveReqVO updateReqVO) {
        areaService.updateWarehouseArea(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除库位")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse-area:delete')")
    public CommonResult<Boolean> deleteWarehouseArea(@RequestParam("id") Long id) {
        areaService.deleteWarehouseArea(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得库位")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse-area:query')")
    public CommonResult<MesWmWarehouseAreaRespVO> getWarehouseArea(@RequestParam("id") Long id) {
        MesWmWarehouseAreaDO area = areaService.getWarehouseArea(id);
        return success(buildWarehouseAreaRespVO(area));
    }

    @GetMapping("/page")
    @Operation(summary = "获得库位分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse-area:query')")
    public CommonResult<PageResult<MesWmWarehouseAreaRespVO>> getWarehouseAreaPage(@Valid MesWmWarehouseAreaPageReqVO pageReqVO) {
        PageResult<MesWmWarehouseAreaDO> pageResult = areaService.getWarehouseAreaPage(pageReqVO);
        return success(new PageResult<>(buildWarehouseAreaRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得库位精简列表", description = "只包含启用状态，可按库区过滤")
    public CommonResult<List<MesWmWarehouseAreaRespVO>> getWarehouseAreaSimpleList(
            @Parameter(name = "locationId", description = "库区编号") @RequestParam(value = "locationId", required = false) Long locationId) {
        List<MesWmWarehouseAreaDO> list = areaService.getWarehouseAreaList(locationId, CommonStatusEnum.ENABLE.getStatus());
        return success(buildWarehouseAreaRespVOList(list));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出库位 Excel")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse-area:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWarehouseAreaExcel(@Valid MesWmWarehouseAreaPageReqVO pageReqVO,
                                         HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MesWmWarehouseAreaDO> list = areaService.getWarehouseAreaPage(pageReqVO).getList();
        ExcelUtils.write(response, "库位.xls", "数据", MesWmWarehouseAreaRespVO.class,
                buildWarehouseAreaRespVOList(list));
    }

    // TODO @AI：这里有个注释块，参考别的方法；

    // TODO @AI：下面这个，就不用愁方法了；直接 get 那处理下 get 0；

    private MesWmWarehouseAreaRespVO buildWarehouseAreaRespVO(MesWmWarehouseAreaDO area) {
        if (area == null) {
            return null;
        }
        List<MesWmWarehouseAreaRespVO> list = buildWarehouseAreaRespVOList(Collections.singletonList(area));
        return CollUtil.isEmpty(list) ? null : CollUtil.getFirst(list);
    }

    private List<MesWmWarehouseAreaRespVO> buildWarehouseAreaRespVOList(List<MesWmWarehouseAreaDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Set<Long> locationIds = convertSet(list, MesWmWarehouseAreaDO::getLocationId);
        Map<Long, MesWmWarehouseLocationDO> locationMap = locationService.getWarehouseLocationMap(locationIds);
        Set<Long> warehouseIds = convertSet(locationMap.values(), MesWmWarehouseLocationDO::getWarehouseId);
        Map<Long, MesWmWarehouseDO> warehouseMap = warehouseService.getWarehouseMap(warehouseIds);

        return BeanUtils.toBean(list, MesWmWarehouseAreaRespVO.class, vo -> {
            MapUtils.findAndThen(locationMap, vo.getLocationId(), location -> {
                vo.setLocationName(location.getName());
                vo.setWarehouseId(location.getWarehouseId());
                MapUtils.findAndThen(warehouseMap, location.getWarehouseId(), warehouse -> vo.setWarehouseName(warehouse.getName()));
            });
        });
    }

}
