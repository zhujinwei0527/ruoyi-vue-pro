package cn.iocoder.yudao.module.mes.controller.admin.wm.batch;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.batch.vo.MesWmBatchRespVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.batch.MesWmBatchDO;
import cn.iocoder.yudao.module.mes.service.wm.batch.MesWmBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 批次管理")
@RestController
@RequestMapping("/mes/wm/batch")
@Validated
public class MesWmBatchController {

    @Resource
    private MesWmBatchService batchService;

    @GetMapping("/forward-list")
    @Operation(summary = "批次向前追溯")
    @Parameter(name = "code", description = "批次编码", required = true, example = "BATCH20250314001")
    @PreAuthorize("@ss.hasPermission('mes:wm-batch:query')")
    public CommonResult<List<MesWmBatchRespVO>> getForwardList(@RequestParam("code") @Valid String code) {
        List<MesWmBatchDO> list = batchService.getForwardBatchList(code);
        return success(BeanUtils.toBean(list, MesWmBatchRespVO.class));
    }

    @GetMapping("/backward-list")
    @Operation(summary = "批次向后追溯")
    @Parameter(name = "code", description = "批次编码", required = true, example = "BATCH20250314001")
    @PreAuthorize("@ss.hasPermission('mes:wm-batch:query')")
    public CommonResult<List<MesWmBatchRespVO>> getBackwardList(@RequestParam("code") @Valid String code) {
        List<MesWmBatchDO> list = batchService.getBackwardBatchList(code);
        return success(BeanUtils.toBean(list, MesWmBatchRespVO.class));
    }

}
