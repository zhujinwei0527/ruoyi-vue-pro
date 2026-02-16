package cn.iocoder.yudao.module.mes.controller.admin.md.workstation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationMachineRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationMachineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationMachineDO;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationMachineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 工位设备")
@RestController
@RequestMapping("/mes/md-workstation-machine")
@Validated
public class MesMdWorkstationMachineController {

    @Resource
    private MesMdWorkstationMachineService workstationMachineService;

    @PostMapping("/create")
    @Operation(summary = "创建工位设备")
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:update')")
    public CommonResult<Long> createWorkstationMachine(@Valid @RequestBody MesMdWorkstationMachineSaveReqVO createReqVO) {
        return success(workstationMachineService.createWorkstationMachine(createReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工位设备")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:update')")
    public CommonResult<Boolean> deleteWorkstationMachine(@RequestParam("id") Long id) {
        workstationMachineService.deleteWorkstationMachine(id);
        return success(true);
    }

    @GetMapping("/list-by-workstation")
    @Operation(summary = "获得工位设备列表")
    @Parameter(name = "workstationId", description = "工位编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:query')")
    public CommonResult<List<MesMdWorkstationMachineRespVO>> getWorkstationMachineList(
            @RequestParam("workstationId") Long workstationId) {
        List<MesMdWorkstationMachineDO> list = workstationMachineService.getWorkstationMachineListByWorkstationId(workstationId);
        // TODO @芋艿：拼装设备编码/名称，等 DV 设备模块完成后对接
        return success(BeanUtils.toBean(list, MesMdWorkstationMachineRespVO.class));
    }

}
