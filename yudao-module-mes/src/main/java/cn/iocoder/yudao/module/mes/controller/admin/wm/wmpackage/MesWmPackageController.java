package cn.iocoder.yudao.module.mes.controller.admin.wm.wmpackage;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.wmpackage.vo.MesWmPackagePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.wmpackage.vo.MesWmPackageRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.wmpackage.vo.MesWmPackageSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.client.MesMdClientDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.wmpackage.MesWmPackageDO;
import cn.iocoder.yudao.module.mes.service.md.client.MesMdClientService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.wm.wmpackage.MesWmPackageService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
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
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - MES 装箱单")
@RestController
@RequestMapping("/mes/wm/package")
@Validated
public class MesWmPackageController {

    @Resource
    private MesWmPackageService packageService;
    @Resource
    private MesMdClientService clientService;
    @Resource
    private MesMdUnitMeasureService unitMeasureService;
    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/create")
    @Operation(summary = "创建装箱单")
    @PreAuthorize("@ss.hasPermission('mes:wm-package:create')")
    public CommonResult<Long> createPackage(@Valid @RequestBody MesWmPackageSaveReqVO createReqVO) {
        return success(packageService.createPackage(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改装箱单")
    @PreAuthorize("@ss.hasPermission('mes:wm-package:update')")
    public CommonResult<Boolean> updatePackage(@Valid @RequestBody MesWmPackageSaveReqVO updateReqVO) {
        packageService.updatePackage(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除装箱单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-package:delete')")
    public CommonResult<Boolean> deletePackage(@RequestParam("id") Long id) {
        packageService.deletePackage(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得装箱单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-package:query')")
    public CommonResult<MesWmPackageRespVO> getPackage(@RequestParam("id") Long id) {
        MesWmPackageDO packageDO = packageService.getPackage(id);
        if (packageDO == null) {
            return success(null);
        }
        return success(buildRespVOList(Collections.singletonList(packageDO)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得装箱单分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-package:query')")
    public CommonResult<PageResult<MesWmPackageRespVO>> getPackagePage(
            @Valid MesWmPackagePageReqVO pageReqVO) {
        PageResult<MesWmPackageDO> pageResult = packageService.getPackagePage(pageReqVO);
        return success(new PageResult<>(buildRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出装箱单 Excel")
    @PreAuthorize("@ss.hasPermission('mes:wm-package:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPackageExcel(@Valid MesWmPackagePageReqVO pageReqVO,
            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<MesWmPackageDO> pageResult = packageService.getPackagePage(pageReqVO);
        ExcelUtils.write(response, "装箱单.xls", "数据", MesWmPackageRespVO.class,
                buildRespVOList(pageResult.getList()));
    }

    @PutMapping("/finish")
    @Operation(summary = "完成装箱单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-package:update')")
    public CommonResult<Boolean> finishPackage(@RequestParam("id") Long id) {
        packageService.finishPackage(id);
        return success(true);
    }

    @GetMapping("/tree")
    @Operation(summary = "获得装箱单树形结构")
    @PreAuthorize("@ss.hasPermission('mes:wm-package:query')")
    public CommonResult<List<MesWmPackageRespVO>> getPackageTree() {
        List<MesWmPackageDO> list = packageService.getPackageTree();
        List<MesWmPackageRespVO> voList = buildRespVOList(list);
        // 构建树形结构
        return success(buildTree(voList));
    }

    // ========== 私有方法 ==========

    private List<MesWmPackageRespVO> buildRespVOList(List<MesWmPackageDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 批量查询客户
        Map<Long, MesMdClientDO> clientMap = clientService.getClientMap(
                convertSet(list, MesWmPackageDO::getClientId));
        // 批量查询计量单位（尺寸 + 重量）
        Set<Long> unitIds = new HashSet<>();
        list.forEach(p -> {
            if (p.getSizeUnitId() != null) {
                unitIds.add(p.getSizeUnitId());
            }
            if (p.getWeightUnitId() != null) {
                unitIds.add(p.getWeightUnitId());
            }
        });
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(unitIds);
        // 批量查询检查员
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(
                convertSet(list, MesWmPackageDO::getInspectorUserId));

        return BeanUtils.toBean(list, MesWmPackageRespVO.class, vo -> {
            MapUtils.findAndThen(clientMap, vo.getClientId(),
                    client -> vo.setClientCode(client.getCode()).setClientName(client.getName())
                            .setClientNickname(client.getNickname()));
            MapUtils.findAndThen(unitMeasureMap, vo.getSizeUnitId(),
                    unit -> vo.setSizeUnitName(unit.getName()));
            MapUtils.findAndThen(unitMeasureMap, vo.getWeightUnitId(),
                    unit -> vo.setWeightUnitName(unit.getName()));
            MapUtils.findAndThen(userMap, vo.getInspectorUserId(),
                    user -> vo.setInspectorName(user.getNickname()));
        });
    }

    private List<MesWmPackageRespVO> buildTree(List<MesWmPackageRespVO> list) {
        // 按 parentId 分组
        Map<Long, List<MesWmPackageRespVO>> parentMap = list.stream()
                .collect(Collectors.groupingBy(vo -> vo.getParentId() != null ? vo.getParentId() : 0L));
        // 设置 children
        list.forEach(vo -> vo.setChildren(parentMap.get(vo.getId())));
        // 返回顶级节点（parentId == 0 或 parentId == null）
        return list.stream()
                .filter(vo -> vo.getParentId() == null || vo.getParentId() == 0L)
                .collect(Collectors.toList());
    }

}
