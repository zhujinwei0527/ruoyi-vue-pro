package cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssuePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssueRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssueSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssueLineRespVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueLineDO;
import cn.iocoder.yudao.module.mes.service.wm.productionissue.MesWmProductionIssueService;
import cn.iocoder.yudao.module.mes.service.wm.productionissue.MesWmProductionIssueLineService;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

// TODO @AI：方法的排序，参考别的模块；
// TODO @AI：controller、service、包括其他的，都需要全。createProductitonIssue 这种；其它模块也是；
// TODO @AI：需要新增 line、detail 的 controller；参考 /Users/yunai/Java/yudao-all-in-one/ruoyi-vue-pro/yudao-module-mes/src/main/java/cn/iocoder/yudao/module/mes/controller/admin/wm/itemreceipt
@Tag(name = "管理后台 - MES 领料出库单")
@RestController
@RequestMapping("/mes/wm/production-issue")
@Validated
public class MesWmProductionIssueController {

    @Resource
    private MesWmProductionIssueService issueService;
    @Resource
    private MesWmProductionIssueLineService issueLineService;

    @PostMapping("/create")
    @Operation(summary = "创建领料出库单")
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:create')")
    public CommonResult<Long> createIssue(@Valid @RequestBody MesWmProductionIssueSaveReqVO createReqVO) {
        return success(issueService.createIssue(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改领料出库单")
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:update')")
    public CommonResult<Boolean> updateIssue(@Valid @RequestBody MesWmProductionIssueSaveReqVO updateReqVO) {
        issueService.updateIssue(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除领料出库单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:delete')")
    public CommonResult<Boolean> deleteIssue(@RequestParam("id") Long id) {
        issueService.deleteIssue(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得领料出库单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:query')")
    public CommonResult<MesWmProductionIssueRespVO> getIssue(@RequestParam("id") Long id) {
        MesWmProductionIssueDO issueHeader = issueService.getIssue(id);
        if (issueHeader == null) {
            return success(null);
        }
        return success(buildRespVOList(Collections.singletonList(issueHeader)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得领料出库单分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:query')")
    public CommonResult<PageResult<MesWmProductionIssueRespVO>> getIssuePage(
            @Valid MesWmProductionIssuePageReqVO pageReqVO) {
        PageResult<MesWmProductionIssueDO> pageResult = issueService.getIssuePage(pageReqVO);
        return success(new PageResult<>(buildRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出领料出库单 Excel")
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportIssueExcel(@Valid MesWmProductionIssuePageReqVO pageReqVO,
                                       HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<MesWmProductionIssueDO> pageResult = issueService.getIssuePage(pageReqVO);
        ExcelUtils.write(response, "领料出库单.xls", "数据", MesWmProductionIssueRespVO.class,
                buildRespVOList(pageResult.getList()));
    }

    @PutMapping("/finish")
    @Operation(summary = "完成领料出库单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-production-issue:update-status')")
    public CommonResult<Boolean> finishIssue(@RequestParam("id") Long id) {
        issueService.finishIssue(id);
        return success(true);
    }

    // ==================== 拼接 VO ====================

    // TODO @AI：这个方法的拼接，在优化下；
    private List<MesWmProductionIssueRespVO> buildRespVOList(List<MesWmProductionIssueDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 1. 转换基础 VO
        List<MesWmProductionIssueRespVO> respVOList = BeanUtils.toBean(list, MesWmProductionIssueRespVO.class);

        // 2. 查询行数据
        Map<Long, List<MesWmProductionIssueLineDO>> lineMap = issueLineService.getIssueLineListByIssueIds(
                convertSet(list, MesWmProductionIssueDO::getId))
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(MesWmProductionIssueLineDO::getIssueId));

        // 3. 填充行数据
        respVOList.forEach(respVO -> {
            List<MesWmProductionIssueLineDO> lines = lineMap.get(respVO.getId());
            if (CollUtil.isNotEmpty(lines)) {
                respVO.setLines(BeanUtils.toBean(lines, MesWmProductionIssueLineRespVO.class));
            }
        });

        // TODO @AI：关联查询工作站、工单、任务、客户等数据，填充到响应 VO 中

        return respVOList;
    }

}
