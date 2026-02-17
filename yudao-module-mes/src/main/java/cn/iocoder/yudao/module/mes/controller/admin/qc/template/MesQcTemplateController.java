package cn.iocoder.yudao.module.mes.controller.admin.qc.template;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.MesQcTemplatePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.MesQcTemplateRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.MesQcTemplateSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.MesQcTemplateDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.MesQcTemplateIndicatorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.MesQcTemplateItemDO;
import cn.iocoder.yudao.module.mes.service.qc.MesQcTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 质检方案")
@RestController
@RequestMapping("/mes/qc/template")
@Validated
public class MesQcTemplateController {

    @Resource
    private MesQcTemplateService templateService;

    // ========== 质检方案主表 ==========

    @PostMapping("/create")
    @Operation(summary = "创建质检方案")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:create')")
    public CommonResult<Long> createTemplate(@Valid @RequestBody MesQcTemplateSaveReqVO createReqVO) {
        return success(templateService.createTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新质检方案")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:update')")
    public CommonResult<Boolean> updateTemplate(@Valid @RequestBody MesQcTemplateSaveReqVO updateReqVO) {
        templateService.updateTemplate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除质检方案")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:qc-template:delete')")
    public CommonResult<Boolean> deleteTemplate(@RequestParam("id") Long id) {
        templateService.deleteTemplate(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得质检方案")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:query')")
    public CommonResult<MesQcTemplateRespVO> getTemplate(@RequestParam("id") Long id) {
        MesQcTemplateDO template = templateService.getTemplate(id);
        return success(BeanUtils.toBean(template, MesQcTemplateRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得质检方案分页")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:query')")
    public CommonResult<PageResult<MesQcTemplateRespVO>> getTemplatePage(@Valid MesQcTemplatePageReqVO pageReqVO) {
        PageResult<MesQcTemplateDO> pageResult = templateService.getTemplatePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesQcTemplateRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得质检方案精简列表", description = "主要用于前端的下拉选项")
    public CommonResult<List<MesQcTemplateRespVO>> getTemplateSimpleList() {
        List<MesQcTemplateDO> list = templateService.getTemplateList();
        return success(BeanUtils.toBean(list, MesQcTemplateRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出质检方案 Excel")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTemplateExcel(@Valid MesQcTemplatePageReqVO pageReqVO,
                                    HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MesQcTemplateDO> list = templateService.getTemplatePage(pageReqVO).getList();
        ExcelUtils.write(response, "质检方案.xls", "数据", MesQcTemplateRespVO.class,
                BeanUtils.toBean(list, MesQcTemplateRespVO.class));
    }

    // ========== 质检方案-检测指标项 ==========

    // TODO @AI：是不是拆分成独立的 controller；类似别的模块

    @PostMapping("/indicator/create")
    @Operation(summary = "创建质检方案-检测指标项")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:create')")
    public CommonResult<Long> createTemplateIndicator(@Valid @RequestBody MesQcTemplateIndicatorSaveReqVO createReqVO) {
        return success(templateService.createTemplateIndicator(createReqVO));
    }

    @PutMapping("/indicator/update")
    @Operation(summary = "更新质检方案-检测指标项")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:update')")
    public CommonResult<Boolean> updateTemplateIndicator(@Valid @RequestBody MesQcTemplateIndicatorSaveReqVO updateReqVO) {
        templateService.updateTemplateIndicator(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/indicator/delete")
    @Operation(summary = "删除质检方案-检测指标项")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:qc-template:update')")
    public CommonResult<Boolean> deleteTemplateIndicator(@RequestParam("id") Long id) {
        templateService.deleteTemplateIndicator(id);
        return success(true);
    }

    @GetMapping("/indicator/get")
    @Operation(summary = "获得质检方案-检测指标项")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:query')")
    public CommonResult<MesQcTemplateIndicatorRespVO> getTemplateIndicator(@RequestParam("id") Long id) {
        MesQcTemplateIndicatorDO indicator = templateService.getTemplateIndicator(id);
        return success(BeanUtils.toBean(indicator, MesQcTemplateIndicatorRespVO.class));
    }

    @GetMapping("/indicator/page")
    @Operation(summary = "获得质检方案-检测指标项分页")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:query')")
    public CommonResult<PageResult<MesQcTemplateIndicatorRespVO>> getTemplateIndicatorPage(
            @Valid MesQcTemplateIndicatorPageReqVO pageReqVO) {
        PageResult<MesQcTemplateIndicatorDO> pageResult = templateService.getTemplateIndicatorPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesQcTemplateIndicatorRespVO.class));
    }

    // ========== 质检方案-产品关联 ==========

    // TODO @AI：是不是拆分成独立的 controller；类似别的模块

    @PostMapping("/item/create")
    @Operation(summary = "创建质检方案-产品关联")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:create')")
    public CommonResult<Long> createTemplateItem(@Valid @RequestBody MesQcTemplateItemSaveReqVO createReqVO) {
        return success(templateService.createTemplateItem(createReqVO));
    }

    @PutMapping("/item/update")
    @Operation(summary = "更新质检方案-产品关联")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:update')")
    public CommonResult<Boolean> updateTemplateItem(@Valid @RequestBody MesQcTemplateItemSaveReqVO updateReqVO) {
        templateService.updateTemplateItem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/item/delete")
    @Operation(summary = "删除质检方案-产品关联")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:qc-template:update')")
    public CommonResult<Boolean> deleteTemplateItem(@RequestParam("id") Long id) {
        templateService.deleteTemplateItem(id);
        return success(true);
    }

    @GetMapping("/item/get")
    @Operation(summary = "获得质检方案-产品关联")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:query')")
    public CommonResult<MesQcTemplateItemRespVO> getTemplateItem(@RequestParam("id") Long id) {
        MesQcTemplateItemDO item = templateService.getTemplateItem(id);
        return success(BeanUtils.toBean(item, MesQcTemplateItemRespVO.class));
    }

    @GetMapping("/item/page")
    @Operation(summary = "获得质检方案-产品关联分页")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:query')")
    public CommonResult<PageResult<MesQcTemplateItemRespVO>> getTemplateItemPage(
            @Valid MesQcTemplateItemPageReqVO pageReqVO) {
        PageResult<MesQcTemplateItemDO> pageResult = templateService.getTemplateItemPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesQcTemplateItemRespVO.class));
    }

}
