package cn.iocoder.yudao.module.mes.controller.admin.qc.template;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.MesQcTemplateItemDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.item.MesMdItemMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.md.unitmeasure.MesMdUnitMeasureMapper;
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

@Tag(name = "管理后台 - MES 质检方案-产品关联")
@RestController
@RequestMapping("/mes/qc/template/item")
@Validated
public class MesQcTemplateItemController {

    @Resource
    private MesQcTemplateService templateService;
    @Resource
    private MesMdItemMapper mdItemMapper;
    @Resource
    private MesMdUnitMeasureMapper unitMeasureMapper;

    @PostMapping("/create")
    @Operation(summary = "创建质检方案-产品关联")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:create')")
    public CommonResult<Long> createTemplateItem(@Valid @RequestBody MesQcTemplateItemSaveReqVO createReqVO) {
        return success(templateService.createTemplateItem(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新质检方案-产品关联")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:update')")
    public CommonResult<Boolean> updateTemplateItem(@Valid @RequestBody MesQcTemplateItemSaveReqVO updateReqVO) {
        templateService.updateTemplateItem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除质检方案-产品关联")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:qc-template:update')")
    public CommonResult<Boolean> deleteTemplateItem(@RequestParam("id") Long id) {
        templateService.deleteTemplateItem(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得质检方案-产品关联")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:query')")
    public CommonResult<MesQcTemplateItemRespVO> getTemplateItem(@RequestParam("id") Long id) {
        MesQcTemplateItemDO do0 = templateService.getTemplateItem(id);
        return success(convertItemRespVO(do0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得质检方案-产品关联分页")
    @PreAuthorize("@ss.hasPermission('mes:qc-template:query')")
    public CommonResult<PageResult<MesQcTemplateItemRespVO>> getTemplateItemPage(
            @Valid MesQcTemplateItemPageReqVO pageReqVO) {
        PageResult<MesQcTemplateItemDO> pageResult = templateService.getTemplateItemPage(pageReqVO);
        List<MesQcTemplateItemRespVO> voList = pageResult.getList().stream()
                .map(this::convertItemRespVO)
                .collect(Collectors.toList());
        return success(new PageResult<>(voList, pageResult.getTotal()));
    }

    // TODO @AI：参考别的模块，buildVO；包括注释；
    // 质检方案-产品关联的 VO 构建方法
    /**
     * DO → RespVO：JOIN mes_md_item 补充物料信息，JOIN mes_md_unit_measure 补充单位名称
     */
    private MesQcTemplateItemRespVO convertItemRespVO(MesQcTemplateItemDO do0) {
        if (do0 == null) {
            return null;
        }
        MesQcTemplateItemRespVO vo = BeanUtils.toBean(do0, MesQcTemplateItemRespVO.class);
        if (do0.getItemId() != null) {
            MesMdItemDO item = mdItemMapper.selectById(do0.getItemId());
            if (item != null) {
                vo.setItemCode(item.getCode());
                vo.setItemName(item.getName());
                vo.setSpecification(item.getSpecification());
                // 查询计量单位名称
                if (item.getUnitMeasureId() != null) {
                    MesMdUnitMeasureDO unit = unitMeasureMapper.selectById(item.getUnitMeasureId());
                    if (unit != null) {
                        vo.setUnitMeasureName(unit.getName());
                    }
                }
            }
        }
        return vo;
    }

}
