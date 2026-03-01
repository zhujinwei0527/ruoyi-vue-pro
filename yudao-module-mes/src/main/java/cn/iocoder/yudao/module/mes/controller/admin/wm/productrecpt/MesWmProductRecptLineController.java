package cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.line.MesWmProductRecptLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.line.MesWmProductRecptLineRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.line.MesWmProductRecptLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt.MesWmProductRecptLineDO;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.wm.productrecpt.MesWmProductRecptLineService;
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

@Tag(name = "管理后台 - MES 产品收货单行")
@RestController
@RequestMapping("/mes/wm/product-recpt-line")
@Validated
public class MesWmProductRecptLineController {

    @Resource
    private MesWmProductRecptLineService productRecptLineService;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesMdUnitMeasureService unitMeasureService;

    @PostMapping("/create")
    @Operation(summary = "创建产品收货单行")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:create')")
    public CommonResult<Long> createProductRecptLine(@Valid @RequestBody MesWmProductRecptLineSaveReqVO createReqVO) {
        return success(productRecptLineService.createProductRecptLine(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改产品收货单行")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:update')")
    public CommonResult<Boolean> updateProductRecptLine(@Valid @RequestBody MesWmProductRecptLineSaveReqVO updateReqVO) {
        productRecptLineService.updateProductRecptLine(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除产品收货单行")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:delete')")
    public CommonResult<Boolean> deleteProductRecptLine(@RequestParam("id") Long id) {
        productRecptLineService.deleteProductRecptLine(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得产品收货单行")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:query')")
    public CommonResult<MesWmProductRecptLineRespVO> getProductRecptLine(@RequestParam("id") Long id) {
        MesWmProductRecptLineDO line = productRecptLineService.getProductRecptLine(id);
        if (line == null) {
            return success(null);
        }
        return success(buildRespVOList(Collections.singletonList(line)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得产品收货单行分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:query')")
    public CommonResult<PageResult<MesWmProductRecptLineRespVO>> getProductRecptLinePage(
            @Valid MesWmProductRecptLinePageReqVO pageReqVO) {
        PageResult<MesWmProductRecptLineDO> pageResult = productRecptLineService.getProductRecptLinePage(pageReqVO);
        return success(new PageResult<>(buildRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    // ==================== 拼接 VO ====================

    private List<MesWmProductRecptLineRespVO> buildRespVOList(List<MesWmProductRecptLineDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 获得关联数据
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(
                convertSet(list, MesWmProductRecptLineDO::getItemId));
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(itemMap.values(), MesMdItemDO::getUnitMeasureId));
        // 2. 构建结果
        return BeanUtils.toBean(list, MesWmProductRecptLineRespVO.class, vo -> {
            MapUtils.findAndThen(itemMap, vo.getItemId(), item -> {
                vo.setItemCode(item.getCode()).setItemName(item.getName()).setSpecification(item.getSpecification());
                MapUtils.findAndThen(unitMeasureMap, item.getUnitMeasureId(),
                        unitMeasure -> vo.setUnitMeasureName(unitMeasure.getName()));
            });
        });
    }

}
