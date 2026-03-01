package cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.detail.MesWmProductRecptDetailRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.detail.MesWmProductRecptDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt.MesWmProductRecptDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.wm.productrecpt.MesWmProductRecptDetailService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseAreaService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseLocationService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - MES 产品收货单明细")
@RestController
@RequestMapping("/mes/wm/product-recpt-detail")
@Validated
public class MesWmProductRecptDetailController {

    @Resource
    private MesWmProductRecptDetailService productRecptDetailService;

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

    @PostMapping("/create")
    @Operation(summary = "创建产品收货单明细")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:create')")
    public CommonResult<Long> createProductRecptDetail(@Valid @RequestBody MesWmProductRecptDetailSaveReqVO createReqVO) {
        return success(productRecptDetailService.createProductRecptDetail(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改产品收货单明细")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:update')")
    public CommonResult<Boolean> updateProductRecptDetail(@Valid @RequestBody MesWmProductRecptDetailSaveReqVO updateReqVO) {
        productRecptDetailService.updateProductRecptDetail(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除产品收货单明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:delete')")
    public CommonResult<Boolean> deleteProductRecptDetail(@RequestParam("id") Long id) {
        productRecptDetailService.deleteProductRecptDetail(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得产品收货单明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:query')")
    public CommonResult<MesWmProductRecptDetailRespVO> getProductRecptDetail(@RequestParam("id") Long id) {
        MesWmProductRecptDetailDO detail = productRecptDetailService.getProductRecptDetail(id);
        if (detail == null) {
            return success(null);
        }
        return success(buildRespVOList(Collections.singletonList(detail)).get(0));
    }

    @GetMapping("/list-by-line")
    @Operation(summary = "获得产品收货单明细列表（按行编号）")
    @Parameter(name = "lineId", description = "行编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:query')")
    public CommonResult<List<MesWmProductRecptDetailRespVO>> getProductRecptDetailListByLineId(
            @RequestParam("lineId") Long lineId) {
        List<MesWmProductRecptDetailDO> list = productRecptDetailService.getProductRecptDetailListByLineId(lineId);
        return success(buildRespVOList(list));
    }

    // ==================== 拼接 VO ====================

    private List<MesWmProductRecptDetailRespVO> buildRespVOList(List<MesWmProductRecptDetailDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 获得关联数据
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(
                convertSet(list, MesWmProductRecptDetailDO::getItemId));
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(itemMap.values(), MesMdItemDO::getUnitMeasureId));
        Map<Long, MesWmWarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                convertSet(list, MesWmProductRecptDetailDO::getWarehouseId));
        Map<Long, MesWmWarehouseLocationDO> locationMap = locationService.getWarehouseLocationMap(
                convertSet(list, MesWmProductRecptDetailDO::getLocationId));
        Map<Long, MesWmWarehouseAreaDO> areaMap = areaService.getWarehouseAreaMap(
                convertSet(list, MesWmProductRecptDetailDO::getAreaId));
        // 2. 构建结果
        return BeanUtils.toBean(list, MesWmProductRecptDetailRespVO.class, vo -> {
            MapUtils.findAndThen(itemMap, vo.getItemId(), item -> {
                vo.setItemCode(item.getCode()).setItemName(item.getName()).setSpecification(item.getSpecification());
                MapUtils.findAndThen(unitMeasureMap, item.getUnitMeasureId(),
                        unitMeasure -> vo.setUnitMeasureName(unitMeasure.getName()));
            });
            MapUtils.findAndThen(warehouseMap, vo.getWarehouseId(),
                    warehouse -> vo.setWarehouseName(warehouse.getName()));
            MapUtils.findAndThen(locationMap, vo.getLocationId(),
                    location -> vo.setLocationName(location.getName()));
            MapUtils.findAndThen(areaMap, vo.getAreaId(),
                    area -> vo.setAreaName(area.getName()));
        });
    }

}
