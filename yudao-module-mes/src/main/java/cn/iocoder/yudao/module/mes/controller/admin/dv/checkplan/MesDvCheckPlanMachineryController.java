package cn.iocoder.yudao.module.mes.controller.admin.dv.checkplan;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.dv.checkplan.vo.machinery.MesDvCheckPlanMachineryRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.dv.checkplan.vo.machinery.MesDvCheckPlanMachinerySaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.checkplan.MesDvCheckPlanMachineryDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.machinery.MesDvMachineryDO;
import cn.iocoder.yudao.module.mes.service.dv.checkplan.MesDvCheckPlanMachineryService;
import cn.iocoder.yudao.module.mes.service.dv.machinery.MesDvMachineryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 点检保养方案设备")
@RestController
@RequestMapping("/mes/dv/check-plan-machinery")
@Validated
public class MesDvCheckPlanMachineryController {

    @Resource
    private MesDvCheckPlanMachineryService checkPlanMachineryService;
    @Resource
    private MesDvMachineryService machineryService;

    @PostMapping("/create")
    @Operation(summary = "创建方案设备关联")
    @PreAuthorize("@ss.hasPermission('mes:dv-check-plan:update')")
    public CommonResult<Long> createCheckPlanMachinery(@Valid @RequestBody MesDvCheckPlanMachinerySaveReqVO createReqVO) {
        return success(checkPlanMachineryService.createCheckPlanMachinery(createReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除方案设备关联")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:dv-check-plan:update')")
    public CommonResult<Boolean> deleteCheckPlanMachinery(@RequestParam("id") Long id) {
        checkPlanMachineryService.deleteCheckPlanMachinery(id);
        return success(true);
    }

    @GetMapping("/list-by-plan")
    @Operation(summary = "获得指定方案的设备列表")
    @Parameter(name = "planId", description = "方案编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:dv-check-plan:query')")
    public CommonResult<List<MesDvCheckPlanMachineryRespVO>> getCheckPlanMachineryListByPlan(
            @RequestParam("planId") Long planId) {
        List<MesDvCheckPlanMachineryDO> list = checkPlanMachineryService.getCheckPlanMachineryListByPlanId(planId);
        List<MesDvCheckPlanMachineryRespVO> respList = BeanUtils.toBean(list, MesDvCheckPlanMachineryRespVO.class);
        // TODO @AI：为空，直接 return；
        // 拼装设备编码/名称/品牌/规格
        // TODO @AI：拆出来，放在 buildVO 那；
        if (CollUtil.isNotEmpty(respList)) {
            // TODO @AI：convertMap；
            List<Long> machineryIds = CollectionUtils.convertList(respList, MesDvCheckPlanMachineryRespVO::getMachineryId);
            // TODO @AI：这里暂时没实现；
            Map<Long, MesDvMachineryDO> machineryMap = machineryService.getMachineryMap(machineryIds);
            respList.forEach(resp -> {
                MesDvMachineryDO machinery = machineryMap.get(resp.getMachineryId());
                if (machinery != null) {
                    // TODO @AI：链式调用；
                    resp.setMachineryCode(machinery.getCode());
                    resp.setMachineryName(machinery.getName());
                    resp.setMachineryBrand(machinery.getBrand());
                    resp.setMachinerySpec(machinery.getSpec());
                }
            });
        }
        return success(respList);
    }

}
