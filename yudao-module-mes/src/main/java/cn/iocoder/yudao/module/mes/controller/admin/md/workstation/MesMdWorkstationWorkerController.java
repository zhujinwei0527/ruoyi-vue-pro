package cn.iocoder.yudao.module.mes.controller.admin.md.workstation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.worker.MesMdWorkstationWorkerRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.worker.MesMdWorkstationWorkerSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationWorkerDO;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationWorkerService;
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

@Tag(name = "管理后台 - MES 工位人员")
@RestController
@RequestMapping("/mes/md-workstation-worker")
@Validated
public class MesMdWorkstationWorkerController {

    @Resource
    private MesMdWorkstationWorkerService workstationWorkerService;

    @PostMapping("/create")
    @Operation(summary = "创建工位人员")
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:update')")
    public CommonResult<Long> createWorkstationWorker(@Valid @RequestBody MesMdWorkstationWorkerSaveReqVO createReqVO) {
        return success(workstationWorkerService.createWorkstationWorker(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新工位人员")
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:update')")
    public CommonResult<Boolean> updateWorkstationWorker(@Valid @RequestBody MesMdWorkstationWorkerSaveReqVO updateReqVO) {
        workstationWorkerService.updateWorkstationWorker(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工位人员")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:update')")
    public CommonResult<Boolean> deleteWorkstationWorker(@RequestParam("id") Long id) {
        workstationWorkerService.deleteWorkstationWorker(id);
        return success(true);
    }

    @GetMapping("/list-by-workstation")
    @Operation(summary = "获得工位人员列表")
    @Parameter(name = "workstationId", description = "工位编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:query')")
    public CommonResult<List<MesMdWorkstationWorkerRespVO>> getWorkstationWorkerList(
            @RequestParam("workstationId") Long workstationId) {
        List<MesMdWorkstationWorkerDO> list = workstationWorkerService.getWorkstationWorkerListByWorkstationId(workstationId);
        // TODO @芋艿：拼装岗位名称，等前端从 system_post 获取岗位下拉列表
        // TODO @AI：后端拼接下返回；postApi？
        return success(BeanUtils.toBean(list, MesMdWorkstationWorkerRespVO.class));
    }

    // TODO @AI：是不是要搞个方法，类似 private List<MesMdProductBomRespVO> buildProductBomRespVOList(List<MesMdProductBomDO> list) {

}
