package cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo.*;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicatorresult.MesQcResultDO;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.qc.lqc.MesQcIqcService;
import cn.iocoder.yudao.module.mes.service.qc.indicatorresult.MesQcResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.MapUtils.findAndThen;

@Tag(name = "管理后台 - MES 检验结果")
@RestController
@RequestMapping("/mes/qc/result")
@Validated
public class MesQcResultController {

    @Resource
    private MesQcResultService resultService;
    @Resource
    private MesQcIqcService iqcService;
    @Resource
    private MesMdItemService itemService;
    @Resource
    private MesMdUnitMeasureService unitMeasureService;

    @PostMapping("/create")
    @Operation(summary = "创建检验结果")
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:create')")
    public CommonResult<Long> createResult(@Valid @RequestBody MesQcResultSaveReqVO createReqVO) {
        return success(resultService.createResult(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新检验结果")
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:update')")
    public CommonResult<Boolean> updateResult(@Valid @RequestBody MesQcResultSaveReqVO updateReqVO) {
        resultService.updateResult(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除检验结果")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:delete')")
    public CommonResult<Boolean> deleteResult(@RequestParam("id") Long id) {
        resultService.deleteResult(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得检验结果（含明细）")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:query')")
    public CommonResult<MesQcResultRespVO> getResult(@RequestParam("id") Long id) {
        MesQcResultDO result = resultService.getResult(id);
        if (result == null) {
            return success(null);
        }
        // 构建 RespVO（含关联字段 + 明细列表）
        MesQcResultRespVO respVO = buildResultRespVOList(Collections.singletonList(result)).get(0);
        // 组装明细
        // TODO @AI：先查询下；然后在 buildResultRespVOList 统一 build；
        respVO.setItems(resultService.getResultDetailList(result.getId(), result.getQcId(), result.getQcType()));
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得检验结果分页")
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:query')")
    public CommonResult<PageResult<MesQcResultRespVO>> getResultPage(@Valid MesQcResultPageReqVO pageReqVO) {
        PageResult<MesQcResultDO> pageResult = resultService.getResultPage(pageReqVO);
        return success(new PageResult<>(buildResultRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    // TODO @AI：resultService.getDetailTemplate(qcId, qcType) 放在 controller 里弄啊；
    @GetMapping("/detail-template")
    @Operation(summary = "获取空值检测项模板")
    @Parameters({
            @Parameter(name = "qcId", description = "质检单ID", required = true, example = "1"),
            @Parameter(name = "qcType", description = "质检类型", required = true, example = "1")
    })
    @PreAuthorize("@ss.hasPermission('mes:qc-iqc:query')")
    public CommonResult<List<MesQcResultDetailRespVO>> getDetailTemplate(
            @RequestParam("qcId") Long qcId,
            @RequestParam("qcType") Integer qcType) {
        return success(resultService.getDetailTemplate(qcId, qcType));
    }

    // ==================== 拼接 VO ====================

    private List<MesQcResultRespVO> buildResultRespVOList(List<MesQcResultDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 批量查询物料
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(
                convertSet(list, MesQcResultDO::getItemId));
        // 批量查询计量单位
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(itemMap.values(), MesMdItemDO::getUnitMeasureId));
        // 拼装 VO
        return BeanUtils.toBean(list, MesQcResultRespVO.class, vo -> {
            // 拼接物料信息
            findAndThen(itemMap, vo.getItemId(), item -> {
                vo.setItemCode(item.getCode()).setItemName(item.getName()).setItemSpecification(item.getSpecification());
                findAndThen(unitMeasureMap, item.getUnitMeasureId(),
                        unit -> vo.setUnitName(unit.getName()));
            });
            // 拼接质检单编号/名称（根据 qcType 从对应服务获取）
            if (Objects.equals(vo.getQcType(), MesQcTypeEnum.IQC.getType())) {
                // TODO @AI：findAndThen 方式优化；
                MesQcIqcDO iqc = iqcService.getIqc(vo.getQcId());
                if (iqc != null) {
                    vo.setQcCode(iqc.getCode()).setQcName(iqc.getName());
                }
            }
            // TODO @芋艿：IPQC/OQC/RQC 模块迁移后实现
        });
    }

}
