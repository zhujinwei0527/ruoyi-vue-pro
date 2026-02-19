package cn.iocoder.yudao.module.mes.controller.admin.pro.route;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.route.vo.product.MesProRouteProductRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.route.vo.product.MesProRouteProductSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProductDO;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.pro.route.MesProRouteProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "管理后台 - MES 工艺路线产品")
@RestController
@RequestMapping("/mes/pro/route-product")
@Validated
public class MesProRouteProductController {

    @Resource
    private MesProRouteProductService routeProductService;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesMdUnitMeasureService unitMeasureService;

    @PostMapping("/create")
    @Operation(summary = "创建工艺路线产品")
    @PreAuthorize("@ss.hasPermission('mes:pro-route:update')")
    public CommonResult<Long> createRouteProduct(@Valid @RequestBody MesProRouteProductSaveReqVO createReqVO) {
        return success(routeProductService.createRouteProduct(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新工艺路线产品")
    @PreAuthorize("@ss.hasPermission('mes:pro-route:update')")
    public CommonResult<Boolean> updateRouteProduct(@Valid @RequestBody MesProRouteProductSaveReqVO updateReqVO) {
        routeProductService.updateRouteProduct(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工艺路线产品")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:pro-route:update')")
    public CommonResult<Boolean> deleteRouteProduct(@RequestParam("id") Long id) {
        routeProductService.deleteRouteProduct(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得工艺路线产品")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:pro-route:query')")
    public CommonResult<MesProRouteProductRespVO> getRouteProduct(@RequestParam("id") Long id) {
        MesProRouteProductDO routeProduct = routeProductService.getRouteProduct(id);
        return success(buildRouteProductRespVO(routeProduct));
    }

    @GetMapping("/list-by-route")
    @Operation(summary = "按工艺路线获得产品列表")
    @Parameter(name = "routeId", description = "工艺路线编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:pro-route:query')")
    public CommonResult<List<MesProRouteProductRespVO>> getRouteProductListByRoute(@RequestParam("routeId") Long routeId) {
        List<MesProRouteProductDO> list = routeProductService.getRouteProductListByRouteId(routeId);
        return success(buildRouteProductRespVOList(list));
    }

    // ==================== 拼接 VO ====================
    // TODO @AI：参考 /Users/yunai/Java/yudao-all-in-one/ruoyi-vue-pro/yudao-module-mes/src/main/java/cn/iocoder/yudao/module/mes/controller/admin/pro/route/MesProRouteProcessController.java 的 todo；

    private List<MesProRouteProductRespVO> buildRouteProductRespVOList(List<MesProRouteProductDO> list) {
        if (list.isEmpty()) {
            return List.of();
        }
        // 批量查询物料信息
        List<Long> itemIds = convertList(list, MesProRouteProductDO::getItemId);
        List<MesMdItemDO> itemList = itemService.getItemList(itemIds);
        Map<Long, MesMdItemDO> itemMap = convertMap(itemList, MesMdItemDO::getId);
        // 批量查询单位信息
        List<Long> unitMeasureIds = convertList(itemList, MesMdItemDO::getUnitMeasureId);
        Map<Long, MesMdUnitMeasureDO> unitMap = convertMap(
                unitMeasureService.getUnitMeasureList(unitMeasureIds), MesMdUnitMeasureDO::getId);
        // 拼装
        List<MesProRouteProductRespVO> result = BeanUtils.toBean(list, MesProRouteProductRespVO.class);
        for (MesProRouteProductRespVO vo : result) {
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

    private MesProRouteProductRespVO buildRouteProductRespVO(MesProRouteProductDO routeProduct) {
        if (routeProduct == null) {
            return null;
        }
        return buildRouteProductRespVOList(List.of(routeProduct)).get(0);
    }

}
