package cn.iocoder.yudao.module.mes.controller.admin.wm.transfer;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.line.MesWmTransferLineRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.line.MesWmTransferLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.transfer.MesWmTransferLineDO;
import cn.iocoder.yudao.module.mes.service.wm.transfer.MesWmTransferLineService;
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

@Tag(name = "管理后台 - MES 转移单行")
@RestController
@RequestMapping("/mes/wm/transfer-line")
@Validated
public class MesWmTransferLineController {

    @Resource
    private MesWmTransferLineService transferLineService;

    @PostMapping("/create")
    @Operation(summary = "创建转移单行")
    @PreAuthorize("@ss.hasPermission('mes:wm-transfer:create')")
    public CommonResult<Long> createTransferLine(@Valid @RequestBody MesWmTransferLineSaveReqVO createReqVO) {
        return success(transferLineService.createTransferLine(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改转移单行")
    @PreAuthorize("@ss.hasPermission('mes:wm-transfer:update')")
    public CommonResult<Boolean> updateTransferLine(@Valid @RequestBody MesWmTransferLineSaveReqVO updateReqVO) {
        transferLineService.updateTransferLine(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除转移单行")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-transfer:delete')")
    public CommonResult<Boolean> deleteTransferLine(@RequestParam("id") Long id) {
        transferLineService.deleteTransferLine(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得转移单行")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-transfer:query')")
    public CommonResult<MesWmTransferLineRespVO> getTransferLine(@RequestParam("id") Long id) {
        MesWmTransferLineDO line = transferLineService.getTransferLine(id);
        return success(BeanUtils.toBean(line, MesWmTransferLineRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得转移单行列表")
    @Parameter(name = "transferId", description = "转移单编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-transfer:query')")
    public CommonResult<List<MesWmTransferLineRespVO>> getTransferLineList(@RequestParam("transferId") Long transferId) {
        List<MesWmTransferLineDO> list = transferLineService.getTransferLineListByTransferId(transferId);
        return success(BeanUtils.toBean(list, MesWmTransferLineRespVO.class));
    }

}
