package cn.iocoder.yudao.module.mes.controller.admin.qc.template;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.MesQcIndicatorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.MesQcTemplateIndicatorDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.unitmeasure.MesMdUnitMeasureMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.MesQcIndicatorMapper;
import cn.iocoder.yudao.module.mes.service.qc.MesQcTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 质检方案-检测指标项")
@RestController
@RequestMapping("/mes/qc/template/indicator")
@Validated
public class MesQcTemplateIndicatorController {

    @Resource
    private MesQcTemplateService templateService;
    @Resource
    private MesQcIndicatorMapper indicatorMapper;
    @Resource
    private MesMdUnitMeasureMapper unitMeasureMapper;

    @PostMapping("/create")
    @Operation(summary = "创建质检方案-检测指标项")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:create')")
    public CommonResult<Long> createTemplateIndicator(@Valid @RequestBody MesQcTemplateIndicatorSaveReqVO createReqVO) {
        return success(templateService.createTemplateIndicator(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新质检方案-检测指标项")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:update')")
    public CommonResult<Boolean> updateTemplateIndicator(@Valid @RequestBody MesQcTemplateIndicatorSaveReqVO updateReqVO) {
        templateService.updateTemplateIndicator(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除质检方案-检测指标项")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:qc-template:update')")
    public CommonResult<Boolean> deleteTemplateIndicator(@RequestParam("id") Long id) {
        templateService.deleteTemplateIndicator(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得质检方案-检测指标项")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:query')")
    public CommonResult<MesQcTemplateIndicatorRespVO> getTemplateIndicator(@RequestParam("id") Long id) {
        MesQcTemplateIndicatorDO do0 = templateService.getTemplateIndicator(id);
        return success(convertIndicatorRespVO(do0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得质检方案-检测指标项分页")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:query')")
    public CommonResult<PageResult<MesQcTemplateIndicatorRespVO>> getTemplateIndicatorPage(
            @Valid MesQcTemplateIndicatorPageReqVO pageReqVO) {
        PageResult<MesQcTemplateIndicatorDO> pageResult = templateService.getTemplateIndicatorPage(pageReqVO);
        List<MesQcTemplateIndicatorRespVO> voList = pageResult.getList().stream()
                .map(this::convertIndicatorRespVO)
                .collect(Collectors.toList());
        return success(new PageResult<>(voList, pageResult.getTotal()));
    }

    // TODO @AI：参考别的模块，buildVO；包括注释；
    /**
     * DO → RespVO：JOIN mes_qc_indicator 补充检测项信息，JOIN mes_md_unit_measure 补充单位名称
     */
    private MesQcTemplateIndicatorRespVO convertIndicatorRespVO(MesQcTemplateIndicatorDO do0) {
        // TODO @AI：应该是批量的操作；
        if (do0 == null) {
            return null;
        }
        MesQcTemplateIndicatorRespVO vo = BeanUtils.toBean(do0, MesQcTemplateIndicatorRespVO.class);
        // JOIN 质检指标
        if (do0.getIndicatorId() != null) {
            MesQcIndicatorDO indicator = indicatorMapper.selectById(do0.getIndicatorId());
            if (indicator != null) {
                vo.setIndicatorCode(indicator.getCode());
                vo.setIndicatorName(indicator.getName());
                vo.setIndicatorType(indicator.getType());
                vo.setIndicatorTool(indicator.getTool());
            }
        }
        // JOIN 计量单位
        if (do0.getUnitMeasureId() != null) {
            MesMdUnitMeasureDO unit = unitMeasureMapper.selectById(do0.getUnitMeasureId());
            if (unit != null) {
                vo.setUnitMeasureName(unit.getName());
            }
        }
        return vo;
    }

}
