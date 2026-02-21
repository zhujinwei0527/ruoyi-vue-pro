package cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo.*;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicator.MesQcIndicatorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicatorresult.MesQcIndicatorResultDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicatorresult.MesQcIndicatorResultDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcLineDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.tm.tool.MesTmToolDO;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.qc.indicator.MesQcIndicatorService;
import cn.iocoder.yudao.module.mes.service.qc.indicatorresult.MesQcIndicatorResultService;
import cn.iocoder.yudao.module.mes.service.qc.lqc.MesQcIqcLineService;
import cn.iocoder.yudao.module.mes.service.qc.lqc.MesQcIqcService;
import cn.iocoder.yudao.module.mes.service.tm.tool.MesTmToolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.MapUtils.findAndThen;

@Tag(name = "管理后台 - MES 检验结果")
@RestController
@RequestMapping("/mes/qc/indicator-result")
@Validated
public class MesQcIndicatorResultController {

    @Resource
    private MesQcIndicatorResultService resultService;
    @Resource
    private MesQcIqcService iqcService;
    @Resource
    private MesQcIqcLineService iqcLineService;
    @Resource
    private MesMdItemService itemService;
    @Resource
    private MesMdUnitMeasureService unitMeasureService;
    @Resource
    private MesQcIndicatorService indicatorService;
    @Resource
    private MesTmToolService toolService;

    @PostMapping("/create")
    @Operation(summary = "创建检验结果")
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:create')")
    public CommonResult<Long> createIndicatorResult(@Valid @RequestBody MesQcIndicatorResultSaveReqVO createReqVO) {
        return success(resultService.createIndicatorResult(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新检验结果")
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:update')")
    public CommonResult<Boolean> updateIndicatorResult(@Valid @RequestBody MesQcIndicatorResultSaveReqVO updateReqVO) {
        resultService.updateIndicatorResult(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除检验结果")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:delete')")
    public CommonResult<Boolean> deleteIndicatorResult(@RequestParam("id") Long id) {
        resultService.deleteIndicatorResult(id);
        return success(true);
    }

    // TODO @AI：是不是把 get 和 getIndicatorResultDetailTemplate 合并为一个接口？参数是 qcId + qcType；新的接口是 get-detail；
    @GetMapping("/get")
    @Operation(summary = "获得检验结果（含明细）")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:query')")
    public CommonResult<MesQcIndicatorResultRespVO> getIndicatorResult(@RequestParam("id") Long id) {
        MesQcIndicatorResultDO result = resultService.getIndicatorResult(id);
        if (result == null) {
            return success(null);
        }
        // 构建 RespVO（含关联字段）
        MesQcIndicatorResultRespVO respVO = buildIndicatorResultRespVOList(Collections.singletonList(result)).get(0);
        // 查询明细并组装
        List<MesQcIndicatorResultDetailDO> details = resultService.getIndicatorResultDetailListByResultId(result.getId());
        respVO.setItems(buildDetailItemList(details, result.getQcId(), result.getQcType()));
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得检验结果分页")
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:query')")
    public CommonResult<PageResult<MesQcIndicatorResultRespVO>> getIndicatorResultPage(@Valid MesQcIndicatorResultPageReqVO pageReqVO) {
        PageResult<MesQcIndicatorResultDO> pageResult = resultService.getIndicatorResultPage(pageReqVO);
        return success(new PageResult<>(buildIndicatorResultRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/detail-template")
    @Operation(summary = "获取空值检测项模板")
    @Parameters({
            @Parameter(name = "qcId", description = "质检单ID", required = true, example = "1"),
            @Parameter(name = "qcType", description = "质检类型", required = true, example = "1")
    })
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:query')")
    public CommonResult<List<MesQcIndicatorResultRespVO.Item>> getIndicatorResultDetailTemplate(
            @RequestParam("qcId") Long qcId,
            @RequestParam("qcType") Integer qcType) {
        return success(buildDetailItemList(Collections.emptyList(), qcId, qcType));
    }

    // ==================== 拼接 VO ====================

    /**
     * 构建主表 RespVO 列表（拼接物料、质检单等关联信息）
     */
    private List<MesQcIndicatorResultRespVO> buildIndicatorResultRespVOList(List<MesQcIndicatorResultDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 批量查询物料
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(
                convertSet(list, MesQcIndicatorResultDO::getItemId));
        // 批量查询计量单位
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(itemMap.values(), MesMdItemDO::getUnitMeasureId));
        // 批量查询 IQC 质检单
        Set<Long> iqcIds = convertSet(list, r ->
                Objects.equals(r.getQcType(), MesQcTypeEnum.IQC.getType()) ? r.getQcId() : null);
        iqcIds.remove(null);
        Map<Long, MesQcIqcDO> iqcMap = iqcService.getIqcMap(iqcIds);

        // 拼装 VO
        return BeanUtils.toBean(list, MesQcIndicatorResultRespVO.class, vo -> {
            // 拼接物料信息
            findAndThen(itemMap, vo.getItemId(), item -> {
                vo.setItemCode(item.getCode()).setItemName(item.getName()).setItemSpecification(item.getSpecification());
                findAndThen(unitMeasureMap, item.getUnitMeasureId(),
                        unit -> vo.setUnitName(unit.getName()));
            });
            // 拼接质检单编号/名称
            if (Objects.equals(vo.getQcType(), MesQcTypeEnum.IQC.getType())) {
                findAndThen(iqcMap, vo.getQcId(), iqc ->
                        vo.setQcCode(iqc.getCode()).setQcName(iqc.getName()));
            }
            // TODO @芋艿：IPQC/OQC/RQC 模块迁移后实现
        });
    }

    /**
     * 构建明细 Item 列表：从 IQC line + indicator + tool + unitMeasure + 已有结果明细组装
     *
     * @param details 已有结果明细（空列表表示获取空值模板）
     * @param qcId    质检单 ID
     * @param qcType  质检类型
     */
    private List<MesQcIndicatorResultRespVO.Item> buildDetailItemList(
            List<MesQcIndicatorResultDetailDO> details, Long qcId, Integer qcType) {
        // 1. 获取检验单行列表
        List<MesQcIqcLineDO> lines;
        if (Objects.equals(qcType, MesQcTypeEnum.IQC.getType())) {
            lines = iqcLineService.getIqcLineListByIqcId(qcId);
        } else {
            // TODO @芋艿：IPQC/OQC/RQC 模块迁移后实现
            return Collections.emptyList();
        }
        if (CollUtil.isEmpty(lines)) {
            return Collections.emptyList();
        }

        // 2. 批量查询关联信息
        Map<Long, MesQcIndicatorDO> indicatorMap = indicatorService.getIndicatorMap(
                convertSet(lines, MesQcIqcLineDO::getIndicatorId));
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(lines, MesQcIqcLineDO::getUnitMeasureId));

        // 3. 构建已有明细 Map（按 indicatorId 索引）
        Map<Long, MesQcIndicatorResultDetailDO> detailMap = CollUtil.isEmpty(details)
                ? Collections.emptyMap()
                : convertMap(details, MesQcIndicatorResultDetailDO::getIndicatorId);

        // 4. 遍历行，组装 VO
        List<MesQcIndicatorResultRespVO.Item> voList = new ArrayList<>(lines.size());
        for (MesQcIqcLineDO line : lines) {
            MesQcIndicatorResultRespVO.Item vo = new MesQcIndicatorResultRespVO.Item();
            // 来自 IQC line
            vo.setIndicatorId(line.getIndicatorId());
            vo.setCheckMethod(line.getCheckMethod());
            vo.setStandardValue(line.getStandardValue());
            vo.setMaxThreshold(line.getMaxThreshold());
            vo.setMinThreshold(line.getMinThreshold());

            // 来自 indicator
            findAndThen(indicatorMap, line.getIndicatorId(), indicator -> {
                vo.setIndicatorCode(indicator.getCode());
                vo.setIndicatorName(indicator.getName());
                vo.setIndicatorType(indicator.getType());
                vo.setValueType(indicator.getResultType());
                vo.setValueSpecification(indicator.getResultSpecification());
            });

            // 来自 unitMeasure
            findAndThen(unitMeasureMap, line.getUnitMeasureId(),
                    unit -> vo.setUnitMeasureName(unit.getName()));

            // 来自 tool（逐个查询，数量通常较少）
            if (line.getToolId() != null) {
                MesTmToolDO tool = toolService.getTool(line.getToolId());
                if (tool != null) {
                    vo.setToolName(tool.getName());
                }
            }

            // 来自已有结果明细（如有）
            MesQcIndicatorResultDetailDO detail = detailMap.get(line.getIndicatorId());
            if (detail != null) {
                vo.setId(detail.getId());
                vo.setResultId(detail.getResultId());
                vo.setValue(detail.getValue());
                vo.setRemark(detail.getRemark());
            }

            voList.add(vo);
        }
        return voList;
    }

}
