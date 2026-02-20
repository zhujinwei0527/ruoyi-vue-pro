package cn.iocoder.yudao.module.mes.controller.admin.qc.iqc;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.defect.MesQcIqcDefectPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.defect.MesQcIqcDefectRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.defect.MesQcIqcDefectSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDefectDO;
import cn.iocoder.yudao.module.mes.service.qc.lqc.MesQcIqcService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 来料检验缺陷记录")
@RestController
@RequestMapping("/mes/qc/iqc/defect")
@Validated
public class MesQcIqcDefectController {

    @Resource
    private MesQcIqcService iqcService;

    @PostMapping("/create")
    @Operation(summary = "创建来料检验缺陷记录")
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:create')")
    public CommonResult<Long> createIqcDefect(@Valid @RequestBody MesQcIqcDefectSaveReqVO createReqVO) {
        return success(iqcService.createIqcDefect(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新来料检验缺陷记录")
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:update')")
    public CommonResult<Boolean> updateIqcDefect(@Valid @RequestBody MesQcIqcDefectSaveReqVO updateReqVO) {
        iqcService.updateIqcDefect(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除来料检验缺陷记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:update')")
    public CommonResult<Boolean> deleteIqcDefect(@RequestParam("id") Long id) {
        iqcService.deleteIqcDefect(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得来料检验缺陷记录分页")
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:query')")
    public CommonResult<PageResult<MesQcIqcDefectRespVO>> getIqcDefectPage(@Valid MesQcIqcDefectPageReqVO pageReqVO) {
        PageResult<MesQcIqcDefectDO> pageResult = iqcService.getIqcDefectPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesQcIqcDefectRespVO.class));
    }

}
