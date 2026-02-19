package cn.iocoder.yudao.module.mes.controller.admin.pro.route;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.route.vo.productbom.MesProRouteProductBomRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.route.vo.productbom.MesProRouteProductBomSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProductBomDO;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.pro.route.MesProRouteProductBomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

@Tag(name = "管理后台 - MES 工艺路线产品 BOM")
@RestController
@RequestMapping("/mes/pro/route-product-bom")
@Validated
public class MesProRouteProductBomController {

    @Resource
    private MesProRouteProductBomService routeProductBomService;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesMdUnitMeasureService unitMeasureService;

    @PostMapping("/create")
    @Operation(summary = "创建工艺路线产品 BOM")
    @PreAuthorize("@ss.hasPermission('mes:pro-route:update')")
    public CommonResult<Long> createRouteProductBom(@Valid @RequestBody MesProRouteProductBomSaveReqVO createReqVO) {
        return success(routeProductBomService.createRouteProductBom(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新工艺路线产品 BOM")
    @PreAuthorize("@ss.hasPermission('mes:pro-route:update')")
    public CommonResult<Boolean> updateRouteProductBom(@Valid @RequestBody MesProRouteProductBomSaveReqVO updateReqVO) {
        routeProductBomService.updateRouteProductBom(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工艺路线产品 BOM")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:pro-route:update')")
    public CommonResult<Boolean> deleteRouteProductBom(@RequestParam("id") Long id) {
        routeProductBomService.deleteRouteProductBom(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得工艺路线产品 BOM")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:pro-route:query')")
    public CommonResult<MesProRouteProductBomRespVO> getRouteProductBom(@RequestParam("id") Long id) {
        MesProRouteProductBomDO routeProductBom = routeProductBomService.getRouteProductBom(id);
        return success(buildRouteProductBomRespVO(routeProductBom));
    }

    @GetMapping("/list")
    @Operation(summary = "查询工艺路线产品 BOM 列表")
    @Parameters({
            @Parameter(name = "routeId", description = "工艺路线编号", required = true, example = "1"),
            @Parameter(name = "processId", description = "工序编号", example = "1"),
            @Parameter(name = "productId", description = "产品物料编号", example = "1")
    })
    @PreAuthorize("@ss.hasPermission('mes:pro-route:query')")
    public CommonResult<List<MesProRouteProductBomRespVO>> getRouteProductBomList(
            @RequestParam("routeId") Long routeId,
            @RequestParam(value = "processId", required = false) Long processId,
            @RequestParam(value = "productId", required = false) Long productId) {
        List<MesProRouteProductBomDO> list = routeProductBomService.getRouteProductBomList(routeId, processId, productId);
        return success(buildRouteProductBomRespVOList(list));
    }

    // ==================== 拼接 VO ====================

    // TODO @AI：参考 /Users/yunai/Java/yudao-all-in-one/ruoyi-vue-pro/yudao-module-mes/src/main/java/cn/iocoder/yudao/module/mes/controller/admin/pro/route/MesProRouteProcessController.java 的 todo；

    private List<MesProRouteProductBomRespVO> buildRouteProductBomRespVOList(List<MesProRouteProductBomDO> list) {
        if (list.isEmpty()) {
            return List.of();
        }
        // 批量查询物料信息
        List<Long> itemIds = convertList(list, MesProRouteProductBomDO::getItemId);
        List<MesMdItemDO> itemList = itemService.getItemList(itemIds);
        Map<Long, MesMdItemDO> itemMap = convertMap(itemList, MesMdItemDO::getId);
        // 批量查询单位信息
        List<Long> unitMeasureIds = convertList(itemList, MesMdItemDO::getUnitMeasureId);
        Map<Long, MesMdUnitMeasureDO> unitMap = convertMap(
                unitMeasureService.getUnitMeasureList(unitMeasureIds), MesMdUnitMeasureDO::getId);
        // 拼装
        List<MesProRouteProductBomRespVO> result = BeanUtils.toBean(list, MesProRouteProductBomRespVO.class);
        for (MesProRouteProductBomRespVO vo : result) {
            MesMdItemDO item = itemMap.get(vo.getItemId());
            if (item != null) {
                vo.setItemCode(item.getCode());
                vo.setItemName(item.getName());
                vo.setSpecification(item.getSpecification());
                MesMdUnitMeasureDO unit = unitMap.get(item.getUnitMeasureId());
                if (unit != null) {
                    vo.setUnitName(unit.getName());
                }
            }
        }
        return result;
    }

    private MesProRouteProductBomRespVO buildRouteProductBomRespVO(MesProRouteProductBomDO routeProductBom) {
        if (routeProductBom == null) {
            return null;
        }
        return buildRouteProductBomRespVOList(List.of(routeProductBom)).get(0);
    }

}
