package cn.iocoder.yudao.module.mes.controller.admin.wm.miscreceipt;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.miscreceipt.vo.line.MesWmMiscReceiptLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.miscreceipt.vo.line.MesWmMiscReceiptLineRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.miscreceipt.vo.line.MesWmMiscReceiptLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.miscreceipt.MesWmMiscReceiptLineDO;
import cn.iocoder.yudao.module.mes.service.wm.miscreceipt.MesWmMiscReceiptLineService;
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

/**
 * MES 杂项入库单行 Controller
 */
@Tag(name = "管理后台 - MES 杂项入库单行")
@RestController
@RequestMapping("/mes/wm/misc-receipt-line")
@Validated
public class MesWmMiscReceiptLineController {

    @Resource
    private MesWmMiscReceiptLineService miscReceiptLineService;

    @PostMapping("/create")
    @Operation(summary = "创建杂项入库单行")
    @PreAuthorize("@ss.hasPermission('mes:wm:misc-receipt:create')")
    public CommonResult<Long> createMiscReceiptLine(@Valid @RequestBody MesWmMiscReceiptLineSaveReqVO createReqVO) {
        return success(miscReceiptLineService.createMiscReceiptLine(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改杂项入库单行")
    @PreAuthorize("@ss.hasPermission('mes:wm:misc-receipt:update')")
    public CommonResult<Boolean> updateMiscReceiptLine(@Valid @RequestBody MesWmMiscReceiptLineSaveReqVO updateReqVO) {
        miscReceiptLineService.updateMiscReceiptLine(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除杂项入库单行")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm:misc-receipt:delete')")
    public CommonResult<Boolean> deleteMiscReceiptLine(@RequestParam("id") Long id) {
        miscReceiptLineService.deleteMiscReceiptLine(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得杂项入库单行")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm:misc-receipt:query')")
    public CommonResult<MesWmMiscReceiptLineRespVO> getMiscReceiptLine(@RequestParam("id") Long id) {
        MesWmMiscReceiptLineDO line = miscReceiptLineService.getMiscReceiptLine(id);
        return success(BeanUtils.toBean(line, MesWmMiscReceiptLineRespVO.class));
    }

    @GetMapping("/list-by-receipt-id")
    @Operation(summary = "获得杂项入库单行列表")
    @Parameter(name = "receiptId", description = "入库单编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm:misc-receipt:query')")
    public CommonResult<List<MesWmMiscReceiptLineRespVO>> getMiscReceiptLineListByReceiptId(@RequestParam("receiptId") Long receiptId) {
        List<MesWmMiscReceiptLineDO> list = miscReceiptLineService.getMiscReceiptLineListByReceiptId(receiptId);
        return success(BeanUtils.toBean(list, MesWmMiscReceiptLineRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得杂项入库单行分页")
    @PreAuthorize("@ss.hasPermission('mes:wm:misc-receipt:query')")
    public CommonResult<PageResult<MesWmMiscReceiptLineRespVO>> getMiscReceiptLinePage(@Valid MesWmMiscReceiptLinePageReqVO pageReqVO) {
        PageResult<MesWmMiscReceiptLineDO> pageResult = miscReceiptLineService.getMiscReceiptLinePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesWmMiscReceiptLineRespVO.class));
    }

}
