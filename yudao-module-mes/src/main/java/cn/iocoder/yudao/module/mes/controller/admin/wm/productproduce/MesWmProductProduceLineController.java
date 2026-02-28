package cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.line.MesWmProductProduceLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.line.MesWmProductProduceLineRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.line.MesWmProductProduceLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceLineDO;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.wm.productproduce.MesWmProductProduceLineService;
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

@Tag(name = "管理后台 - MES 生产入库单行")
@RestController
@RequestMapping("/mes/wm/product-produce-line")
@Validated
public class MesWmProductProduceLineController {

    @Resource
    private MesWmProductProduceLineService produceLineService;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesMdUnitMeasureService unitMeasureService;

    @PostMapping("/create")
    @Operation(summary = "创建生产入库单行")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:create')")
    public CommonResult<Long> createProductProduceLine(@Valid @RequestBody MesWmProductProduceLineSaveReqVO createReqVO) {
        return success(produceLineService.createProductProduceLine(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改生产入库单行")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:update')")
    public CommonResult<Boolean> updateProductProduceLine(@Valid @RequestBody MesWmProductProduceLineSaveReqVO updateReqVO) {
        produceLineService.updateProductProduceLine(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除生产入库单行")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:delete')")
    public CommonResult<Boolean> deleteProductProduceLine(@RequestParam("id") Long id) {
        produceLineService.deleteProductProduceLine(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得生产入库单行")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:query')")
    public CommonResult<MesWmProductProduceLineRespVO> getProductProduceLine(@RequestParam("id") Long id) {
        MesWmProductProduceLineDO line = produceLineService.getProductProduceLine(id);
        if (line == null) {
            return success(null);
        }
        return success(buildRespVOList(Collections.singletonList(line)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得生产入库单行分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:query')")
    public CommonResult<PageResult<MesWmProductProduceLineRespVO>> getProductProduceLinePage(
            @Valid MesWmProductProduceLinePageReqVO pageReqVO) {
        PageResult<MesWmProductProduceLineDO> pageResult = produceLineService.getProductProduceLinePage(pageReqVO);
        return success(new PageResult<>(buildRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/list-by-produce")
    @Operation(summary = "获得生产入库单行列表（按入库单编号）")
    @Parameter(name = "produceId", description = "入库单编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:query')")
    public CommonResult<List<MesWmProductProduceLineRespVO>> getProductProduceLineListByProduceId(
            @RequestParam("produceId") Long produceId) {
        List<MesWmProductProduceLineDO> list = produceLineService.getProductProduceLineListByProduceId(produceId);
        return success(buildRespVOList(list));
    }

    // ==================== 拼接 VO ====================

    private List<MesWmProductProduceLineRespVO> buildRespVOList(List<MesWmProductProduceLineDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 获得关联数据
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(
                convertSet(list, MesWmProductProduceLineDO::getItemId));
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(itemMap.values(), MesMdItemDO::getUnitMeasureId));
        // 2. 构建结果
        return BeanUtils.toBean(list, MesWmProductProduceLineRespVO.class, vo -> {
            MapUtils.findAndThen(itemMap, vo.getItemId(), item -> {
                vo.setItemCode(item.getCode()).setItemName(item.getName()).setSpecification(item.getSpecification());
                MapUtils.findAndThen(unitMeasureMap, item.getUnitMeasureId(),
                        unitMeasure -> vo.setUnitMeasureName(unitMeasure.getName()));
            });
        });
    }

}
