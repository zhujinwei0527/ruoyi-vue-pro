package cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.line.MesWmMaterialRequestLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.line.MesWmMaterialRequestLineRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.line.MesWmMaterialRequestLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialrequest.MesWmMaterialRequestLineDO;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.wm.materialrequest.MesWmMaterialRequestLineService;
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

@Tag(name = "管理后台 - MES 领料申请单行")
@RestController
@RequestMapping("/mes/wm/material-request-line")
@Validated
public class MesWmMaterialRequestLineController {

    @Resource
    private MesWmMaterialRequestLineService materialRequestLineService;
    @Resource
    private MesMdItemService itemService;
    @Resource
    private MesMdUnitMeasureService unitMeasureService;

    @PostMapping("/create")
    @Operation(summary = "创建领料申请单行")
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:create')")
    public CommonResult<Long> createMaterialRequestLine(@Valid @RequestBody MesWmMaterialRequestLineSaveReqVO createReqVO) {
        return success(materialRequestLineService.createMaterialRequestLine(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改领料申请单行")
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:update')")
    public CommonResult<Boolean> updateMaterialRequestLine(@Valid @RequestBody MesWmMaterialRequestLineSaveReqVO updateReqVO) {
        materialRequestLineService.updateMaterialRequestLine(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除领料申请单行")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:delete')")
    public CommonResult<Boolean> deleteMaterialRequestLine(@RequestParam("id") Long id) {
        materialRequestLineService.deleteMaterialRequestLine(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得领料申请单行")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:query')")
    public CommonResult<MesWmMaterialRequestLineRespVO> getMaterialRequestLine(@RequestParam("id") Long id) {
        MesWmMaterialRequestLineDO line = materialRequestLineService.getMaterialRequestLine(id);
        if (line == null) {
            return success(null);
        }
        return success(buildRespVOList(Collections.singletonList(line)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得领料申请单行分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-material-request:query')")
    public CommonResult<PageResult<MesWmMaterialRequestLineRespVO>> getMaterialRequestLinePage(
            @Valid MesWmMaterialRequestLinePageReqVO pageReqVO) {
        PageResult<MesWmMaterialRequestLineDO> pageResult = materialRequestLineService.getMaterialRequestLinePage(pageReqVO);
        return success(new PageResult<>(buildRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    // ==================== 拼接 VO ====================

    private List<MesWmMaterialRequestLineRespVO> buildRespVOList(List<MesWmMaterialRequestLineDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 获得关联数据
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(
                convertSet(list, MesWmMaterialRequestLineDO::getItemId));
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(list, MesWmMaterialRequestLineDO::getUnitMeasureId));
        // 2. 构建结果
        return BeanUtils.toBean(list, MesWmMaterialRequestLineRespVO.class, vo -> {
            MapUtils.findAndThen(itemMap, vo.getItemId(), item ->
                    vo.setItemCode(item.getCode()).setSpecification(item.getSpecification()));
            MapUtils.findAndThen(unitMeasureMap, vo.getUnitMeasureId(), unit ->
                    vo.setUnitMeasureName(unit.getName()));
        });
    }

}
