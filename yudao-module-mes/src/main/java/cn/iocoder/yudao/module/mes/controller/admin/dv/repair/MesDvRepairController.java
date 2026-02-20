package cn.iocoder.yudao.module.mes.controller.admin.dv.repair;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.dv.repair.vo.MesDvRepairPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.dv.repair.vo.MesDvRepairRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.dv.repair.vo.MesDvRepairSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.machinery.MesDvMachineryDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.repair.MesDvRepairDO;
import cn.iocoder.yudao.module.mes.service.dv.machinery.MesDvMachineryService;
import cn.iocoder.yudao.module.mes.service.dv.repair.MesDvRepairService;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - MES 维修工单")
@RestController
@RequestMapping("/mes/dv/repair")
@Validated
public class MesDvRepairController {

    @Resource
    private MesDvRepairService repairService;

    @Resource
    private MesDvMachineryService machineryService;

    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/create")
    @Operation(summary = "创建维修工单")
    @PreAuthorize("@ss.hasPermission('mes:dv-repair:create')")
    public CommonResult<Long> createRepair(@Valid @RequestBody MesDvRepairSaveReqVO createReqVO) {
        return success(repairService.createRepair(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新维修工单")
    @PreAuthorize("@ss.hasPermission('mes:dv-repair:update')")
    public CommonResult<Boolean> updateRepair(@Valid @RequestBody MesDvRepairSaveReqVO updateReqVO) {
        repairService.updateRepair(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除维修工单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:dv-repair:delete')")
    public CommonResult<Boolean> deleteRepair(@RequestParam("id") Long id) {
        repairService.deleteRepair(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得维修工单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:dv-repair:query')")
    public CommonResult<MesDvRepairRespVO> getRepair(@RequestParam("id") Long id) {
        MesDvRepairDO repair = repairService.getRepair(id);
        if (repair == null) {
            return success(null);
        }
        return success(buildRepairRespVOList(Collections.singletonList(repair)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得维修工单分页")
    @PreAuthorize("@ss.hasPermission('mes:dv-repair:query')")
    public CommonResult<PageResult<MesDvRepairRespVO>> getRepairPage(@Valid MesDvRepairPageReqVO pageReqVO) {
        PageResult<MesDvRepairDO> pageResult = repairService.getRepairPage(pageReqVO);
        return success(new PageResult<>(buildRepairRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出维修工单 Excel")
    @PreAuthorize("@ss.hasPermission('mes:dv-repair:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportRepairExcel(@Valid MesDvRepairPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MesDvRepairDO> list = repairService.getRepairPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "维修工单.xls", "数据", MesDvRepairRespVO.class,
                buildRepairRespVOList(list));
    }

    // ==================== 拼接 VO ====================

    private List<MesDvRepairRespVO> buildRepairRespVOList(List<MesDvRepairDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1.1 批量获取关联数据
        Map<Long, MesDvMachineryDO> machineryMap = machineryService.getMachineryMap(
                convertSet(list, MesDvRepairDO::getMachineryId));
        // 1.2 收集所有用户 ID（维修人 + 验收人）
        // TODO @AI：CollUtils 里面，有多字段的处理，你研究下；
        Set<Long> userIds = convertSet(list, MesDvRepairDO::getAcceptedUserId);
        userIds.addAll(convertSet(list, MesDvRepairDO::getConfirmUserId));
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(userIds);
        // 2. 拼接 VO
        return BeanUtils.toBean(list, MesDvRepairRespVO.class, vo -> {
            MapUtils.findAndThen(machineryMap, vo.getMachineryId(), machinery -> vo
                    .setMachineryCode(machinery.getCode()).setMachineryName(machinery.getName())
                    .setMachineryBrand(machinery.getBrand()).setMachinerySpec(machinery.getSpec()));
            MapUtils.findAndThen(userMap, vo.getAcceptedUserId(),
                    user -> vo.setAcceptedUserNickname(user.getNickname()));
            MapUtils.findAndThen(userMap, vo.getConfirmUserId(),
                    user -> vo.setConfirmUserNickname(user.getNickname()));
        });
    }

}
