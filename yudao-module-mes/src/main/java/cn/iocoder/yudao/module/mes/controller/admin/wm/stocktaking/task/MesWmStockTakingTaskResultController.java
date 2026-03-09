package cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.result.MesWmStockTakingTaskResultPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.result.MesWmStockTakingTaskResultRespVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskResultDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.wm.stocktaking.task.MesWmStockTakingTaskResultService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - MES 盘点结果")
@RestController
@RequestMapping("/mes/wm/stocktaking-task-result")
@Validated
public class MesWmStockTakingTaskResultController {

    @Resource
    private MesWmStockTakingTaskResultService stockTakingTaskResultService;
    @Resource
    private MesMdItemService itemService;
    @Resource
    private MesMdUnitMeasureService unitMeasureService;
    @Resource
    private MesWmWarehouseService warehouseService;
    @Resource
    private MesWmWarehouseLocationService locationService;
    @Resource
    private MesWmWarehouseAreaService areaService;

    @GetMapping("/get")
    @Operation(summary = "获得盘点结果")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-stock-taking-task:query')")
    public CommonResult<MesWmStockTakingTaskResultRespVO> getStockTakingTaskResult(@RequestParam("id") Long id) {
        MesWmStockTakingTaskResultDO result = stockTakingTaskResultService.getStockTakingTaskResult(id);
        if (result == null) {
            return success(null);
        }
        return success(buildRespVOList(Collections.singletonList(result)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得盘点结果分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-stock-taking-task:query')")
    public CommonResult<PageResult<MesWmStockTakingTaskResultRespVO>> getStockTakingTaskResultPage(@Valid MesWmStockTakingTaskResultPageReqVO pageReqVO) {
        PageResult<MesWmStockTakingTaskResultDO> pageResult = stockTakingTaskResultService.getStockTakingTaskResultPage(pageReqVO);
        return success(new PageResult<>(buildRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/list")
    @Operation(summary = "获得盘点结果列表")
    @Parameter(name = "taskId", description = "任务编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-stock-taking-task:query')")
    public CommonResult<List<MesWmStockTakingTaskResultRespVO>> getStockTakingTaskResultList(@RequestParam("taskId") Long taskId) {
        return success(buildRespVOList(stockTakingTaskResultService.getStockTakingTaskResultList(taskId)));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出盘点结果 Excel")
    @PreAuthorize("@ss.hasPermission('mes:wm-stock-taking-task:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportStockTakingTaskResultExcel(@Valid MesWmStockTakingTaskResultPageReqVO pageReqVO,
                                                 HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<MesWmStockTakingTaskResultDO> pageResult = stockTakingTaskResultService.getStockTakingTaskResultPage(pageReqVO);
        ExcelUtils.write(response, "盘点结果.xls", "数据", MesWmStockTakingTaskResultRespVO.class,
                buildRespVOList(pageResult.getList()));
    }

    private List<MesWmStockTakingTaskResultRespVO> buildRespVOList(List<MesWmStockTakingTaskResultDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(convertSet(list, MesWmStockTakingTaskResultDO::getItemId));
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(itemMap.values(), MesMdItemDO::getUnitMeasureId));
        Map<Long, MesWmWarehouseDO> warehouseMap = warehouseService.getWarehouseMap(convertSet(list, MesWmStockTakingTaskResultDO::getWarehouseId));
        Map<Long, MesWmWarehouseLocationDO> locationMap = locationService.getWarehouseLocationMap(convertSet(list, MesWmStockTakingTaskResultDO::getLocationId));
        Map<Long, MesWmWarehouseAreaDO> areaMap = areaService.getWarehouseAreaMap(convertSet(list, MesWmStockTakingTaskResultDO::getAreaId));
        return BeanUtils.toBean(list, MesWmStockTakingTaskResultRespVO.class, vo -> {
            MapUtils.findAndThen(itemMap, vo.getItemId(), item -> {
                vo.setItemCode(item.getCode());
                vo.setItemName(item.getName());
                vo.setSpecification(item.getSpecification());
                MapUtils.findAndThen(unitMeasureMap, item.getUnitMeasureId(), unit -> vo.setUnitMeasureName(unit.getName()));
            });
            MapUtils.findAndThen(warehouseMap, vo.getWarehouseId(), warehouse -> vo.setWarehouseName(warehouse.getName()));
            MapUtils.findAndThen(locationMap, vo.getLocationId(), location -> vo.setLocationName(location.getName()));
            MapUtils.findAndThen(areaMap, vo.getAreaId(), area -> vo.setAreaName(area.getName()));
        });
    }

}
