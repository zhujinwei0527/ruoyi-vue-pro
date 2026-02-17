package cn.iocoder.yudao.module.mes.controller.admin.pro.workorder;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.MesProWorkorderPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.MesProWorkorderRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.MesProWorkorderSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.client.MesMdClientDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.vendor.MesMdVendorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkorderDO;
import cn.iocoder.yudao.module.mes.service.md.client.MesMdClientService;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.md.vendor.MesMdVendorService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkorderService;
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

@Tag(name = "管理后台 - MES 生产工单")
@RestController
@RequestMapping("/mes/pro/workorder")
@Validated
public class MesProWorkorderController {

    @Resource
    private MesProWorkorderService workorderService;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesMdClientService clientService;

    @Resource
    private MesMdVendorService vendorService;

    @Resource
    private MesMdUnitMeasureService unitMeasureService;

    @PostMapping("/create")
    @Operation(summary = "创建生产工单")
    @PreAuthorize("@ss.hasPermission('mes:pro-workorder:create')")
    public CommonResult<Long> createWorkorder(@Valid @RequestBody MesProWorkorderSaveReqVO createReqVO) {
        return success(workorderService.createWorkorder(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新生产工单")
    @PreAuthorize("@ss.hasPermission('mes:pro-workorder:update')")
    public CommonResult<Boolean> updateWorkorder(@Valid @RequestBody MesProWorkorderSaveReqVO updateReqVO) {
        workorderService.updateWorkorder(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除生产工单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:pro-workorder:delete')")
    public CommonResult<Boolean> deleteWorkorder(@RequestParam("id") Long id) {
        workorderService.deleteWorkorder(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得生产工单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:pro-workorder:query')")
    public CommonResult<MesProWorkorderRespVO> getWorkorder(@RequestParam("id") Long id) {
        MesProWorkorderDO workorder = workorderService.getWorkorder(id);
        if (workorder == null) {
            return success(null);
        }
        return success(buildWorkorderRespVOList(List.of(workorder)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得生产工单分页")
    @PreAuthorize("@ss.hasPermission('mes:pro-workorder:query')")
    public CommonResult<PageResult<MesProWorkorderRespVO>> getWorkorderPage(@Valid MesProWorkorderPageReqVO pageReqVO) {
        PageResult<MesProWorkorderDO> pageResult = workorderService.getWorkorderPage(pageReqVO);
        return success(new PageResult<>(buildWorkorderRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出生产工单 Excel")
    @PreAuthorize("@ss.hasPermission('mes:pro-workorder:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWorkorderExcel(@Valid MesProWorkorderPageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MesProWorkorderDO> list = workorderService.getWorkorderPage(pageReqVO).getList();
        List<MesProWorkorderRespVO> voList = buildWorkorderRespVOList(list);
        ExcelUtils.write(response, "生产工单.xls", "数据", MesProWorkorderRespVO.class, voList);
    }

    @PutMapping("/finish")
    @Operation(summary = "完成工单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:pro-workorder:update')")
    public CommonResult<Boolean> finishWorkorder(@RequestParam("id") Long id) {
        workorderService.finishWorkorder(id);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消工单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:pro-workorder:update')")
    public CommonResult<Boolean> cancelWorkorder(@RequestParam("id") Long id) {
        workorderService.cancelWorkorder(id);
        return success(true);
    }

    // ==================== 拼接 VO ====================

    @SuppressWarnings("CodeBlock2Expr")
    private List<MesProWorkorderRespVO> buildWorkorderRespVOList(List<MesProWorkorderDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 批量获取关联数据
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(
                convertSet(list, MesProWorkorderDO::getProductId));
        Map<Long, MesMdClientDO> clientMap = clientService.getClientMap(
                convertSet(list, MesProWorkorderDO::getClientId));
        Map<Long, MesMdVendorDO> vendorMap = vendorService.getVendorMap(
                convertSet(list, MesProWorkorderDO::getVendorId));
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(list, MesProWorkorderDO::getUnitMeasureId));
        // 2. 拼接 VO
        return BeanUtils.toBean(list, MesProWorkorderRespVO.class, vo -> {
            MapUtils.findAndThen(itemMap, vo.getProductId(), item -> {
                // TODO @AI：少了 spec 字段？另外，改成链式调用；
                vo.setProductName(item.getName());
                vo.setProductCode(item.getCode());
                vo.setProductSpec(item.getSpec());
            });
            MapUtils.findAndThen(clientMap, vo.getClientId(),
                    client -> vo.setClientName(client.getName()));
            MapUtils.findAndThen(vendorMap, vo.getVendorId(),
                    vendor -> vo.setVendorName(vendor.getName()));
            MapUtils.findAndThen(unitMeasureMap, vo.getUnitMeasureId(),
                    unitMeasure -> vo.setUnitMeasureName(unitMeasure.getName()));
        });
    }

}
