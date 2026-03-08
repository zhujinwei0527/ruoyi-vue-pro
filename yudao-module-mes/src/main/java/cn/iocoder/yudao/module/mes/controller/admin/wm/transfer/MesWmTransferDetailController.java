package cn.iocoder.yudao.module.mes.controller.admin.wm.transfer;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.detail.MesWmTransferDetailRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.detail.MesWmTransferDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.transfer.MesWmTransferDetailDO;
import cn.iocoder.yudao.module.mes.service.wm.transfer.MesWmTransferDetailService;
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

@Tag(name = "管理后台 - MES 调拨明细")
@RestController
@RequestMapping("/mes/wm/transfer-detail")
@Validated
public class MesWmTransferDetailController {

    @Resource
    private MesWmTransferDetailService transferDetailService;

    @PostMapping("/create")
    @Operation(summary = "创建调拨明细")
    @PreAuthorize("@ss.hasPermission('mes:wm-transfer:create')")
    public CommonResult<Long> createTransferDetail(@Valid @RequestBody MesWmTransferDetailSaveReqVO createReqVO) {
        return success(transferDetailService.createTransferDetail(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改调拨明细")
    @PreAuthorize("@ss.hasPermission('mes:wm-transfer:update')")
    public CommonResult<Boolean> updateTransferDetail(@Valid @RequestBody MesWmTransferDetailSaveReqVO updateReqVO) {
        transferDetailService.updateTransferDetail(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除调拨明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-transfer:delete')")
    public CommonResult<Boolean> deleteTransferDetail(@RequestParam("id") Long id) {
        transferDetailService.deleteTransferDetail(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得调拨明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-transfer:query')")
    public CommonResult<MesWmTransferDetailRespVO> getTransferDetail(@RequestParam("id") Long id) {
        MesWmTransferDetailDO detail = transferDetailService.getTransferDetail(id);
        return success(BeanUtils.toBean(detail, MesWmTransferDetailRespVO.class));
    }

    @GetMapping("/list-by-line")
    @Operation(summary = "获得调拨明细列表（按行编号）")
    @Parameter(name = "lineId", description = "行编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-transfer:query')")
    public CommonResult<List<MesWmTransferDetailRespVO>> getTransferDetailListByLineId(
            @RequestParam("lineId") Long lineId) {
        List<MesWmTransferDetailDO> list = transferDetailService.getTransferDetailListByLineId(lineId);
        return success(BeanUtils.toBean(list, MesWmTransferDetailRespVO.class));
    }

}
