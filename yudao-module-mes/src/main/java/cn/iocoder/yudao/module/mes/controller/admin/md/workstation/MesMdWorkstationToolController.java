package cn.iocoder.yudao.module.mes.controller.admin.md.workstation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.tool.MesMdWorkstationToolRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.tool.MesMdWorkstationToolSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationToolDO;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationToolService;
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

@Tag(name = "管理后台 - MES 工位工具")
@RestController
@RequestMapping("/mes/md-workstation-tool")
@Validated
public class MesMdWorkstationToolController {

    @Resource
    private MesMdWorkstationToolService workstationToolService;

    @PostMapping("/create")
    @Operation(summary = "创建工位工具")
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:update')")
    public CommonResult<Long> createWorkstationTool(@Valid @RequestBody MesMdWorkstationToolSaveReqVO createReqVO) {
        return success(workstationToolService.createWorkstationTool(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新工位工具")
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:update')")
    public CommonResult<Boolean> updateWorkstationTool(@Valid @RequestBody MesMdWorkstationToolSaveReqVO updateReqVO) {
        workstationToolService.updateWorkstationTool(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工位工具")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:update')")
    public CommonResult<Boolean> deleteWorkstationTool(@RequestParam("id") Long id) {
        workstationToolService.deleteWorkstationTool(id);
        return success(true);
    }

    @GetMapping("/list-by-workstation")
    @Operation(summary = "获得工位工具列表")
    @Parameter(name = "workstationId", description = "工位编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:query')")
    public CommonResult<List<MesMdWorkstationToolRespVO>> getWorkstationToolList(
            @RequestParam("workstationId") Long workstationId) {
        List<MesMdWorkstationToolDO> list = workstationToolService.getWorkstationToolListByWorkstationId(workstationId);
        // TODO @芋艿：拼装工具类型名称，等 TM 工具模块完成后对接
        return success(BeanUtils.toBean(list, MesMdWorkstationToolRespVO.class));
    }

}
