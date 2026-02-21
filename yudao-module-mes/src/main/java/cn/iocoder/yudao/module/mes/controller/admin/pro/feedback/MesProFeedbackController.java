package cn.iocoder.yudao.module.mes.controller.admin.pro.feedback;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.feedback.vo.MesProFeedbackPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.feedback.vo.MesProFeedbackRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.feedback.vo.MesProFeedbackSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.feedback.MesProFeedbackDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.process.MesProProcessDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProcessDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.feedback.MesProFeedbackService;
import cn.iocoder.yudao.module.mes.service.pro.process.MesProProcessService;
import cn.iocoder.yudao.module.mes.service.pro.route.MesProRouteProcessService;
import cn.iocoder.yudao.module.mes.service.pro.route.MesProRouteService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
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
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSetByFlatMap;
import static cn.iocoder.yudao.framework.common.util.collection.MapUtils.findAndThen;

@Tag(name = "管理后台 - MES 生产报工")
@RestController
@RequestMapping("/mes/pro/feedback")
@Validated
public class MesProFeedbackController {

    @Resource
    private MesProFeedbackService feedbackService;

    @Resource
    private MesMdWorkstationService workstationService;

    @Resource
    private MesProRouteService routeService;

    @Resource
    private MesProProcessService processService;

    @Resource
    private MesProWorkOrderService workOrderService;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesMdUnitMeasureService unitMeasureService;

    @Resource
    private MesProRouteProcessService routeProcessService;

    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/create")
    @Operation(summary = "创建生产报工")
    @PreAuthorize("@ss.hasPermission('mes:pro-feedback:create')")
    public CommonResult<Long> createFeedback(@Valid @RequestBody MesProFeedbackSaveReqVO createReqVO) {
        return success(feedbackService.createFeedback(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新生产报工")
    @PreAuthorize("@ss.hasPermission('mes:pro-feedback:update')")
    public CommonResult<Boolean> updateFeedback(@Valid @RequestBody MesProFeedbackSaveReqVO updateReqVO) {
        feedbackService.updateFeedback(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除生产报工")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:pro-feedback:delete')")
    public CommonResult<Boolean> deleteFeedback(@RequestParam("id") Long id) {
        feedbackService.deleteFeedback(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得生产报工")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:pro-feedback:query')")
    public CommonResult<MesProFeedbackRespVO> getFeedback(@RequestParam("id") Long id) {
        MesProFeedbackDO feedback = feedbackService.getFeedback(id);
        if (feedback == null) {
            return success(null);
        }
        return success(buildFeedbackRespVOList(List.of(feedback)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得生产报工分页")
    @PreAuthorize("@ss.hasPermission('mes:pro-feedback:query')")
    public CommonResult<PageResult<MesProFeedbackRespVO>> getFeedbackPage(@Valid MesProFeedbackPageReqVO pageReqVO) {
        PageResult<MesProFeedbackDO> pageResult = feedbackService.getFeedbackPage(pageReqVO);
        return success(new PageResult<>(buildFeedbackRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出生产报工 Excel")
    @PreAuthorize("@ss.hasPermission('mes:pro-feedback:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportFeedbackExcel(@Valid MesProFeedbackPageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MesProFeedbackDO> list = feedbackService.getFeedbackPage(pageReqVO).getList();
        List<MesProFeedbackRespVO> voList = buildFeedbackRespVOList(list);
        ExcelUtils.write(response, "生产报工.xls", "数据", MesProFeedbackRespVO.class, voList);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交报工")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:pro-feedback:update')")
    public CommonResult<Boolean> submitFeedback(@RequestParam("id") Long id) {
        feedbackService.submitFeedback(id);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "驳回报工")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:pro-feedback:update')")
    public CommonResult<Boolean> rejectFeedback(@RequestParam("id") Long id) {
        feedbackService.rejectFeedback(id);
        return success(true);
    }

    @PutMapping("/execute")
    @Operation(summary = "执行报工")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:pro-feedback:update')")
    public CommonResult<Boolean> executeFeedback(@RequestParam("id") Long id) {
        feedbackService.executeFeedback(id);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消报工")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:pro-feedback:update')")
    public CommonResult<Boolean> cancelFeedback(@RequestParam("id") Long id) {
        feedbackService.cancelFeedback(id);
        return success(true);
    }

    // ==================== 拼接 VO ====================

    private List<MesProFeedbackRespVO> buildFeedbackRespVOList(List<MesProFeedbackDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1.1 物料、计量单位
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(
                convertSet(list, MesProFeedbackDO::getItemId));
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(list, MesProFeedbackDO::getUnitMeasureId));
        // 1.2 工单
        Map<Long, MesProWorkOrderDO> workOrderMap = workOrderService.getWorkOrderMap(
                convertSet(list, MesProFeedbackDO::getWorkOrderId));
        // 1.3 工作站（逐个查询，因为没有批量方法）
        // TODO @AI：getWorkstationMap 必须有办法
        Set<Long> workstationIds = convertSet(list, MesProFeedbackDO::getWorkstationId);
        Map<Long, MesMdWorkstationDO> workstationMap = new HashMap<>();
        workstationIds.forEach(wsId -> {
            MesMdWorkstationDO ws = workstationService.getWorkstation(wsId);
            if (ws != null) {
                workstationMap.put(wsId, ws);
            }
        });
        // 1.4 工艺路线（逐个查询）
        // TODO @AI：getRouteMap 必须有办法；里面调用 getRouteList；如果不行，就加个；
        Set<Long> routeIds = convertSet(list, MesProFeedbackDO::getRouteId);
        Map<Long, MesProRouteDO> routeMap = new HashMap<>();
        routeIds.forEach(rId -> {
            MesProRouteDO route = routeService.getRoute(rId);
            if (route != null) {
                routeMap.put(rId, route);
            }
        });
        // 1.5 工序
        // TODO @AI：getProcessMap 必须有办法；里面调用 getProcessList；如果不行，就加个；
        Set<Long> processIds = convertSet(list, MesProFeedbackDO::getProcessId);
        List<MesProProcessDO> processList = processService.getProcessList(new ArrayList<>(processIds));
        Map<Long, MesProProcessDO> processMap = processList.stream()
                .collect(Collectors.toMap(MesProProcessDO::getId, p -> p, (a, b) -> a));
        // 1.6 工序的 checkFlag（按路线维度缓存）
        // TODO @AI：collutils 里面有个 convertMultiMap 的方法；
        Map<Long, List<MesProRouteProcessDO>> routeProcessMap = new HashMap<>();
        routeIds.forEach(rId -> routeProcessMap.put(rId, routeProcessService.getRouteProcessListByRouteId(rId)));
        // 1.7 报工人/审核人
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(
                convertSetByFlatMap(list, feedback -> {
                    // TODO @AI：必须要判空，里面已经处理了；
                    Set<Long> userIds = new HashSet<>();
                    if (feedback.getFeedbackUserId() != null) {
                        userIds.add(feedback.getFeedbackUserId());
                    }
                    if (feedback.getApproveUserId() != null) {
                        userIds.add(feedback.getApproveUserId());
                    }
                    return userIds.stream();
                }));

        // 2. 拼接 VO
        return BeanUtils.toBean(list, MesProFeedbackRespVO.class, vo -> {
            // 工作站
            findAndThen(workstationMap, vo.getWorkstationId(), ws ->
                    vo.setWorkstationCode(ws.getCode()).setWorkstationName(ws.getName()));
            // 工艺路线
            findAndThen(routeMap, vo.getRouteId(), route ->
                    vo.setRouteCode(route.getCode()));
            // 工序
            findAndThen(processMap, vo.getProcessId(), process ->
                    vo.setProcessCode(process.getCode()).setProcessName(process.getName()));
            // checkFlag
            // TODO @AI：这个判断有点复杂，是不是上面构建更合适的 map？
            List<MesProRouteProcessDO> rps = routeProcessMap.get(vo.getRouteId());
            if (rps != null) {
                rps.stream()
                        .filter(rp -> rp.getProcessId().equals(vo.getProcessId()))
                        .findFirst()
                        .ifPresent(rp -> vo.setCheckFlag(Boolean.TRUE.equals(rp.getCheckFlag())));
            }
            // 工单
            findAndThen(workOrderMap, vo.getWorkOrderId(), wo ->
                    vo.setWorkOrderCode(wo.getCode()).setWorkOrderName(wo.getName()));
            // TODO @芋艿：task code 待 pro_task 服务迁移后补充
            // 物料、计量单位
            findAndThen(itemMap, vo.getItemId(), item ->
                    vo.setItemCode(item.getCode()).setItemName(item.getName()).setItemSpec(item.getSpecification()));
            findAndThen(unitMeasureMap, vo.getUnitMeasureId(), unit ->
                    vo.setUnitMeasureName(unit.getName()));
            // 报工人、审核人
            findAndThen(userMap, vo.getFeedbackUserId(), user ->
                    vo.setFeedbackUserNickname(user.getNickname()));
            findAndThen(userMap, vo.getApproveUserId(), user ->
                    vo.setApproveUserNickname(user.getNickname()));
        });
    }

}
