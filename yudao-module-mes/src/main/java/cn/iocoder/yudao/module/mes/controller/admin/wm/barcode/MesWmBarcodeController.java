package cn.iocoder.yudao.module.mes.controller.admin.wm.barcode;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.barcode.vo.MesWmBarcodePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.barcode.vo.MesWmBarcodeRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.barcode.vo.MesWmBarcodeSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.barcode.MesWmBarcodeDO;
import cn.iocoder.yudao.module.mes.service.wm.barcode.MesWmBarcodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 条码清单")
@RestController
@RequestMapping("/mes/wm/barcode")
@Validated
public class MesWmBarcodeController {

    @Resource
    private MesWmBarcodeService barcodeService;

    @PostMapping("/create")
    @Operation(summary = "创建条码")
    @PreAuthorize("@ss.hasPermission('mes:wm-barcode:create')")
    public CommonResult<Long> createBarcode(@Valid @RequestBody MesWmBarcodeSaveReqVO createReqVO) {
        return success(barcodeService.createBarcode(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新条码")
    @PreAuthorize("@ss.hasPermission('mes:wm-barcode:update')")
    public CommonResult<Boolean> updateBarcode(@Valid @RequestBody MesWmBarcodeSaveReqVO updateReqVO) {
        barcodeService.updateBarcode(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除条码")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-barcode:delete')")
    public CommonResult<Boolean> deleteBarcode(@RequestParam("id") Long id) {
        barcodeService.deleteBarcode(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得条码")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-barcode:query')")
    public CommonResult<MesWmBarcodeRespVO> getBarcode(@RequestParam("id") Long id) {
        MesWmBarcodeDO barcode = barcodeService.getBarcode(id);
        return success(BeanUtils.toBean(barcode, MesWmBarcodeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得条码分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-barcode:query')")
    public CommonResult<PageResult<MesWmBarcodeRespVO>> getBarcodePage(@Valid MesWmBarcodePageReqVO pageReqVO) {
        PageResult<MesWmBarcodeDO> pageResult = barcodeService.getBarcodePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesWmBarcodeRespVO.class));
    }

    @GetMapping("/get-by-business")
    @Operation(summary = "根据业务对象获取条码信息", description = "前端用于生成条码图片")
    @Parameter(name = "bizType", description = "业务类型", required = true, example = "102")
    @Parameter(name = "bizId", description = "业务编号", required = true, example = "1024")
    public CommonResult<MesWmBarcodeRespVO> getBarcodeByBusiness(
            @RequestParam("bizType") Integer bizType,
            @RequestParam("bizId") Long bizId) {
        MesWmBarcodeDO barcode = barcodeService.getBarcodeByBizTypeAndBizId(bizType, bizId);
        return success(BeanUtils.toBean(barcode, MesWmBarcodeRespVO.class));
    }

}
