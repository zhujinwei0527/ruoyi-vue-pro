package cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.MesWmProductRecptPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.MesWmProductRecptRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.MesWmProductRecptSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt.MesWmProductRecptDO;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import cn.iocoder.yudao.module.mes.service.wm.productrecpt.MesWmProductRecptService;
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

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - MES 产品收货单")
@RestController
@RequestMapping("/mes/wm/product-recpt")
@Validated
public class MesWmProductRecptController {

    @Resource
    private MesWmProductRecptService productRecptService;

    @Resource
    private MesProWorkOrderService workOrderService;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesMdUnitMeasureService unitMeasureService;

    @PostMapping("/create")
    @Operation(summary = "创建产品收货单")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:create')")
    public CommonResult<Long> createProductRecpt(@Valid @RequestBody MesWmProductRecptSaveReqVO createReqVO) {
        return success(productRecptService.createProductRecpt(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改产品收货单")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:update')")
    public CommonResult<Boolean> updateProductRecpt(@Valid @RequestBody MesWmProductRecptSaveReqVO updateReqVO) {
        productRecptService.updateProductRecpt(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除产品收货单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:delete')")
    public CommonResult<Boolean> deleteProductRecpt(@RequestParam("id") Long id) {
        productRecptService.deleteProductRecpt(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得产品收货单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:query')")
    public CommonResult<MesWmProductRecptRespVO> getProductRecpt(@RequestParam("id") Long id) {
        MesWmProductRecptDO recpt = productRecptService.getProductRecpt(id);
        if (recpt == null) {
            return success(null);
        }
        return success(buildRespVOList(Collections.singletonList(recpt)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得产品收货单分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:query')")
    public CommonResult<PageResult<MesWmProductRecptRespVO>> getProductRecptPage(
            @Valid MesWmProductRecptPageReqVO pageReqVO) {
        PageResult<MesWmProductRecptDO> pageResult = productRecptService.getProductRecptPage(pageReqVO);
        return success(new PageResult<>(buildRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出产品收货单 Excel")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportProductRecptExcel(@Valid MesWmProductRecptPageReqVO pageReqVO,
                                        HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<MesWmProductRecptDO> pageResult = productRecptService.getProductRecptPage(pageReqVO);
        ExcelUtils.write(response, "产品收货单.xls", "数据", MesWmProductRecptRespVO.class,
                buildRespVOList(pageResult.getList()));
    }

    @PutMapping("/submit")
    @Operation(summary = "提交产品收货单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:update')")
    public CommonResult<Boolean> submitProductRecpt(@RequestParam("id") Long id) {
        productRecptService.submitProductRecpt(id);
        return success(true);
    }

    @PutMapping("/stock")
    @Operation(summary = "执行上架")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:update')")
    public CommonResult<Boolean> stockProductRecpt(@RequestParam("id") Long id) {
        productRecptService.stockProductRecpt(id);
        return success(true);
    }

    @PutMapping("/execute")
    @Operation(summary = "执行入库")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:execute')")
    public CommonResult<Boolean> executeProductRecpt(@RequestParam("id") Long id) {
        productRecptService.executeProductRecpt(id);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消产品收货单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:update')")
    public CommonResult<Boolean> cancelProductRecpt(@RequestParam("id") Long id) {
        productRecptService.cancelProductRecpt(id);
        return success(true);
    }

    @GetMapping("/check-quantity")
    @Operation(summary = "校验产品收货单明细数量")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-product-recpt:query')")
    public CommonResult<Boolean> checkProductRecptQuantity(@RequestParam("id") Long id) {
        return success(productRecptService.checkProductRecptQuantity(id));
    }

    // ==================== 拼接 VO ====================

    private List<MesWmProductRecptRespVO> buildRespVOList(List<MesWmProductRecptDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 获得关联数据
        Map<Long, MesProWorkOrderDO> workOrderMap = workOrderService.getWorkOrderMap(
                convertSet(list, MesWmProductRecptDO::getWorkOrderId));
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(
                convertSet(list, MesWmProductRecptDO::getItemId));
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(itemMap.values(), MesMdItemDO::getUnitMeasureId));
        // 2. 构建结果
        return BeanUtils.toBean(list, MesWmProductRecptRespVO.class, vo -> {
            MapUtils.findAndThen(workOrderMap, vo.getWorkOrderId(),
                    workOrder -> vo.setWorkOrderCode(workOrder.getCode()));
            MapUtils.findAndThen(itemMap, vo.getItemId(), item -> {
                vo.setItemCode(item.getCode()).setItemName(item.getName()).setSpecification(item.getSpecification());
                MapUtils.findAndThen(unitMeasureMap, item.getUnitMeasureId(),
                        unitMeasure -> vo.setUnitMeasureName(unitMeasure.getName()));
            });
        });
    }

}
