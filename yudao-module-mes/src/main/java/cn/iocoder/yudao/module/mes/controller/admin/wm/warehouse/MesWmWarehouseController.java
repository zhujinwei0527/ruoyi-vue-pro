package cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.MesWmWarehousePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.MesWmWarehouseRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.warehouse.vo.MesWmWarehouseSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
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
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 仓库")
@RestController
@RequestMapping("/mes/wm/warehouse")
@Validated
public class MesWmWarehouseController {

    @Resource
    private MesWmWarehouseService warehouseService;

    @PostMapping("/create")
    @Operation(summary = "创建仓库")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse:create')")
    public CommonResult<Long> createWarehouse(@Valid @RequestBody MesWmWarehouseSaveReqVO createReqVO) {
        return success(warehouseService.createWarehouse(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新仓库")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse:update')")
    public CommonResult<Boolean> updateWarehouse(@Valid @RequestBody MesWmWarehouseSaveReqVO updateReqVO) {
        warehouseService.updateWarehouse(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除仓库")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse:delete')")
    public CommonResult<Boolean> deleteWarehouse(@RequestParam("id") Long id) {
        warehouseService.deleteWarehouse(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得仓库")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse:query')")
    public CommonResult<MesWmWarehouseRespVO> getWarehouse(@RequestParam("id") Long id) {
        MesWmWarehouseDO warehouse = warehouseService.getWarehouse(id);
        return success(BeanUtils.toBean(warehouse, MesWmWarehouseRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得仓库分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse:query')")
    public CommonResult<PageResult<MesWmWarehouseRespVO>> getWarehousePage(@Valid MesWmWarehousePageReqVO pageReqVO) {
        PageResult<MesWmWarehouseDO> pageResult = warehouseService.getWarehousePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesWmWarehouseRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得仓库精简列表", description = "只包含启用状态，主要用于前端下拉")
    public CommonResult<List<MesWmWarehouseRespVO>> getWarehouseSimpleList() {
        List<MesWmWarehouseDO> list = warehouseService.getWarehouseListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return success(BeanUtils.toBean(list, MesWmWarehouseRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出仓库 Excel")
    @PreAuthorize("@ss.hasPermission('mes:wm-warehouse:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWarehouseExcel(@Valid MesWmWarehousePageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MesWmWarehouseDO> list = warehouseService.getWarehousePage(pageReqVO).getList();
        ExcelUtils.write(response, "仓库.xls", "数据", MesWmWarehouseRespVO.class,
                BeanUtils.toBean(list, MesWmWarehouseRespVO.class));
    }

}
