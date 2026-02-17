package cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.location.MesWmWarehouseLocationPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.location.MesWmWarehouseLocationRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.location.MesWmWarehouseLocationSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - MES 库区")
@RestController
@RequestMapping("/mes/wm/warehouse-location")
@Validated
public class MesWmWarehouseLocationController {

    @Resource
    private MesWmWarehouseLocationService locationService;

    @Resource
    private MesWmWarehouseService warehouseService;

    @PostMapping("/create")
    @Operation(summary = "创建库区")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse:create')")
    public CommonResult<Long> createWarehouseLocation(@Valid @RequestBody MesWmWarehouseLocationSaveReqVO createReqVO) {
        return success(locationService.createWarehouseLocation(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新库区")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse:update')")
    public CommonResult<Boolean> updateWarehouseLocation(@Valid @RequestBody MesWmWarehouseLocationSaveReqVO updateReqVO) {
        locationService.updateWarehouseLocation(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除库区")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse:delete')")
    public CommonResult<Boolean> deleteWarehouseLocation(@RequestParam("id") Long id) {
        locationService.deleteWarehouseLocation(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得库区")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse:query')")
    public CommonResult<MesWmWarehouseLocationRespVO> getWarehouseLocation(@RequestParam("id") Long id) {
        MesWmWarehouseLocationDO location = locationService.getWarehouseLocation(id);
        if (location == null) {
            return success(null);
        }
        return success(buildWarehouseLocationRespVOList(Collections.singletonList(location)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得库区分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse:query')")
    public CommonResult<PageResult<MesWmWarehouseLocationRespVO>> getWarehouseLocationPage(@Valid MesWmWarehouseLocationPageReqVO pageReqVO) {
        PageResult<MesWmWarehouseLocationDO> pageResult = locationService.getWarehouseLocationPage(pageReqVO);
        return success(new PageResult<>(buildWarehouseLocationRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得库区精简列表", description = "只包含启用状态，可按仓库过滤")
    public CommonResult<List<MesWmWarehouseLocationRespVO>> getWarehouseLocationSimpleList(
            @Parameter(name = "warehouseId", description = "仓库编号") @RequestParam(value = "warehouseId", required = false) Long warehouseId) {
        List<MesWmWarehouseLocationDO> list = locationService.getWarehouseLocationList(warehouseId, CommonStatusEnum.ENABLE.getStatus());
        return success(buildWarehouseLocationRespVOList(list));
    }

    // TODO @AI：不需要导出功能；前端也可以去掉；

    @GetMapping("/export-excel")
    @Operation(summary = "导出库区 Excel")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWarehouseLocationExcel(@Valid MesWmWarehouseLocationPageReqVO pageReqVO,
                                             HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MesWmWarehouseLocationDO> list = locationService.getWarehouseLocationPage(pageReqVO).getList();
        ExcelUtils.write(response, "库区.xls", "数据", MesWmWarehouseLocationRespVO.class,
                buildWarehouseLocationRespVOList(list));
    }

    // ==================== 拼接 VO ====================

    /**
     * 批量构建库区响应 VO 列表（填充仓库名称）
     *
     * @param list 库区列表
     * @return 响应 VO 列表
     */
    private List<MesWmWarehouseLocationRespVO> buildWarehouseLocationRespVOList(List<MesWmWarehouseLocationDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 获得仓库信息
        Map<Long, MesWmWarehouseDO> warehouseMap = warehouseService.getWarehouseMap(convertSet(list, MesWmWarehouseLocationDO::getWarehouseId));
        // 2. 构建结果
        return BeanUtils.toBean(list, MesWmWarehouseLocationRespVO.class, vo ->
                MapUtils.findAndThen(warehouseMap, vo.getWarehouseId(), warehouse -> vo.setWarehouseName(warehouse.getName())));
    }

}
