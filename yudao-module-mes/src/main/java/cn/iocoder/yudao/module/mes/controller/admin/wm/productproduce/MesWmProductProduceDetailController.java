package cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.detail.MesWmProductProduceDetailRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.detail.MesWmProductProduceDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.wm.productproduce.MesWmProductProduceDetailService;
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

@Tag(name = "管理后台 - MES 生产入库明细")
@RestController
@RequestMapping("/mes/wm/product-produce-detail")
@Validated
public class MesWmProductProduceDetailController {

    @Resource
    private MesWmProductProduceDetailService produceDetailService;

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
    @Operation(summary = "创建生产入库明细")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:create')")
    public CommonResult<Long> createProductProduceDetail(@Valid @RequestBody MesWmProductProduceDetailSaveReqVO createReqVO) {
        return success(produceDetailService.createProductProduceDetail(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改生产入库明细")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:update')")
    public CommonResult<Boolean> updateProductProduceDetail(@Valid @RequestBody MesWmProductProduceDetailSaveReqVO updateReqVO) {
        produceDetailService.updateProductProduceDetail(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除生产入库明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:delete')")
    public CommonResult<Boolean> deleteProductProduceDetail(@RequestParam("id") Long id) {
        produceDetailService.deleteProductProduceDetail(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得生产入库明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:query')")
    public CommonResult<MesWmProductProduceDetailRespVO> getProductProduceDetail(@RequestParam("id") Long id) {
        MesWmProductProduceDetailDO detail = produceDetailService.getProductProduceDetail(id);
        if (detail == null) {
            return success(null);
        }
        return success(buildRespVOList(Collections.singletonList(detail)).get(0));
    }

    @GetMapping("/list-by-line")
    @Operation(summary = "获得生产入库明细列表（按行编号）")
    @Parameter(name = "lineId", description = "行编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:query')")
    public CommonResult<List<MesWmProductProduceDetailRespVO>> getProductProduceDetailListByLineId(
            @RequestParam("lineId") Long lineId) {
        List<MesWmProductProduceDetailDO> list = produceDetailService.getProductProduceDetailListByLineId(lineId);
        return success(buildRespVOList(list));
    }

    // ==================== 拼接 VO ====================

    private List<MesWmProductProduceDetailRespVO> buildRespVOList(List<MesWmProductProduceDetailDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 获得关联数据
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(
                convertSet(list, MesWmProductProduceDetailDO::getItemId));
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(itemMap.values(), MesMdItemDO::getUnitMeasureId));
        Map<Long, MesWmWarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                convertSet(list, MesWmProductProduceDetailDO::getWarehouseId));
        Map<Long, MesWmWarehouseLocationDO> locationMap = locationService.getWarehouseLocationMap(
                convertSet(list, MesWmProductProduceDetailDO::getLocationId));
        Map<Long, MesWmWarehouseAreaDO> areaMap = areaService.getWarehouseAreaMap(
                convertSet(list, MesWmProductProduceDetailDO::getAreaId));
        // 2. 构建结果
        return BeanUtils.toBean(list, MesWmProductProduceDetailRespVO.class, vo -> {
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
