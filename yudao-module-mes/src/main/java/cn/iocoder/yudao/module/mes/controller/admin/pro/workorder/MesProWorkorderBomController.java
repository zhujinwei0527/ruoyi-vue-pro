package cn.iocoder.yudao.module.mes.controller.admin.pro.workorder;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.bom.MesProWorkorderBomPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.bom.MesProWorkorderBomRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.bom.MesProWorkorderBomSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkorderBomDO;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkorderBomService;
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

@Tag(name = "管理后台 - MES 生产工单 BOM")
@RestController
@RequestMapping("/mes/pro/workorder-bom")
@Validated
public class MesProWorkorderBomController {

    @Resource
    private MesProWorkorderBomService workorderBomService;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesMdUnitMeasureService unitMeasureService;

    @PostMapping("/create")
    @Operation(summary = "创建工单 BOM")
    @PreAuthorize("@ss.hasPermission('mes:pro-workorder:update')")
    public CommonResult<Long> createWorkorderBom(@Valid @RequestBody MesProWorkorderBomSaveReqVO createReqVO) {
        return success(workorderBomService.createWorkorderBom(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新工单 BOM")
    @PreAuthorize("@ss.hasPermission('mes:pro-workorder:update')")
    public CommonResult<Boolean> updateWorkorderBom(@Valid @RequestBody MesProWorkorderBomSaveReqVO updateReqVO) {
        workorderBomService.updateWorkorderBom(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工单 BOM")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:pro-workorder:update')")
    public CommonResult<Boolean> deleteWorkorderBom(@RequestParam("id") Long id) {
        workorderBomService.deleteWorkorderBom(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得工单 BOM")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:pro-workorder:query')")
    public CommonResult<MesProWorkorderBomRespVO> getWorkorderBom(@RequestParam("id") Long id) {
        MesProWorkorderBomDO workorderBom = workorderBomService.getWorkorderBom(id);
        if (workorderBom == null) {
            return success(null);
        }
        return success(buildWorkorderBomRespVOList(List.of(workorderBom)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得工单 BOM 分页")
    @PreAuthorize("@ss.hasPermission('mes:pro-workorder:query')")
    public CommonResult<PageResult<MesProWorkorderBomRespVO>> getWorkorderBomPage(@Valid MesProWorkorderBomPageReqVO pageReqVO) {
        PageResult<MesProWorkorderBomDO> pageResult = workorderBomService.getWorkorderBomPage(pageReqVO);
        return success(new PageResult<>(buildWorkorderBomRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    // ==================== 拼接 VO ====================

    private List<MesProWorkorderBomRespVO> buildWorkorderBomRespVOList(List<MesProWorkorderBomDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 批量获取关联数据
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(
                convertSet(list, MesProWorkorderBomDO::getItemId));
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(list, MesProWorkorderBomDO::getUnitMeasureId));
        // 2. 拼接 VO
        return BeanUtils.toBean(list, MesProWorkorderBomRespVO.class, vo -> {
            MapUtils.findAndThen(itemMap, vo.getItemId(), item -> {
                // TODO @AI：少了 spec 字段？另外，改成链式调用；
                vo.setItemName(item.getName());
                vo.setItemCode(item.getCode());
                vo.setItemSpec(item.getSpec());
            });
            MapUtils.findAndThen(unitMeasureMap, vo.getUnitMeasureId(),
                    unitMeasure -> vo.setUnitMeasureName(unitMeasure.getName()));
        });
    }

}
