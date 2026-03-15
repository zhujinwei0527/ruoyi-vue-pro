package cn.iocoder.yudao.module.mes.controller.admin.pro.task;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.task.vo.MesProTaskPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.task.vo.MesProTaskRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.task.vo.MesProTaskSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.client.MesMdClientDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.process.MesProProcessDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.task.MesProTaskDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.service.md.client.MesMdClientService;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.process.MesProProcessService;
import cn.iocoder.yudao.module.mes.service.pro.task.MesProTaskService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
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
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.MapUtils.findAndThen;

@Tag(name = "管理后台 - MES 生产任务")
@RestController
@RequestMapping("/mes/pro/task")
@Validated
public class MesProTaskController {

    @Resource
    private MesProTaskService taskService;

    @Resource
    private MesProWorkOrderService workOrderService;

    @Resource
    private MesMdWorkstationService workstationService;

    @Resource
    private MesProProcessService processService;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesMdClientService clientService;

    @PostMapping("/create")
    @Operation(summary = "创建生产任务")
    @PreAuthorize("@ss.hasPermission('mes:pro-task:create')")
    public CommonResult<Long> createTask(@Valid @RequestBody MesProTaskSaveReqVO createReqVO) {
        return success(taskService.createTask(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新生产任务")
    @PreAuthorize("@ss.hasPermission('mes:pro-task:update')")
    public CommonResult<Boolean> updateTask(@Valid @RequestBody MesProTaskSaveReqVO updateReqVO) {
        taskService.updateTask(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除生产任务")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:pro-task:delete')")
    public CommonResult<Boolean> deleteTask(@RequestParam("id") Long id) {
        taskService.deleteTask(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得生产任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:pro-task:query')")
    public CommonResult<MesProTaskRespVO> getTask(@RequestParam("id") Long id) {
        MesProTaskDO task = taskService.getTask(id);
        if (task == null) {
            return success(null);
        }
        return success(buildTaskRespVOList(List.of(task)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得生产任务分页")
    @PreAuthorize("@ss.hasPermission('mes:pro-task:query')")
    public CommonResult<PageResult<MesProTaskRespVO>> getTaskPage(@Valid MesProTaskPageReqVO pageReqVO) {
        PageResult<MesProTaskDO> pageResult = taskService.getTaskPage(pageReqVO);
        return success(new PageResult<>(buildTaskRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得生产任务精简列表", description = "主要用于前端的下拉选项")
    public CommonResult<List<MesProTaskRespVO>> getTaskSimpleList(
            @RequestParam(value = "workOrderId", required = false) Long workOrderId) {
        List<MesProTaskDO> list = taskService.getTaskSimpleList(workOrderId);
        return success(convertList(list, task -> new MesProTaskRespVO()
                .setId(task.getId()).setCode(task.getCode()).setName(task.getName())
                .setWorkOrderId(task.getWorkOrderId()).setProcessId(task.getProcessId())
                .setStatus(task.getStatus())));
    }

    @GetMapping("/gantt-list")
    @Operation(summary = "获得甘特图任务列表", description = "非分页接口，返回所有匹配任务用于甘特图渲染")
    @PreAuthorize("@ss.hasPermission('mes:pro-task:query')")
    public CommonResult<List<MesProTaskRespVO>> listGanttTaskList(@Valid MesProTaskPageReqVO reqVO) {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MesProTaskDO> list = taskService.getTaskPage(reqVO).getList();
        return success(buildTaskRespVOList(list));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出生产任务 Excel")
    @PreAuthorize("@ss.hasPermission('mes:pro-task:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTaskExcel(@Valid MesProTaskPageReqVO pageReqVO,
                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MesProTaskDO> list = taskService.getTaskPage(pageReqVO).getList();
        List<MesProTaskRespVO> voList = buildTaskRespVOList(list);
        ExcelUtils.write(response, "生产任务.xls", "数据", MesProTaskRespVO.class, voList);
    }

    // ==================== 拼接 VO ====================

    private List<MesProTaskRespVO> buildTaskRespVOList(List<MesProTaskDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 批量查询关联数据
        Map<Long, MesProWorkOrderDO> workOrderMap = workOrderService.getWorkOrderMap(
                convertSet(list, MesProTaskDO::getWorkOrderId));
        Map<Long, MesMdWorkstationDO> workstationMap = workstationService.getWorkstationMap(
                convertSet(list, MesProTaskDO::getWorkstationId));
        Map<Long, MesProProcessDO> processMap = processService.getProcessMap(
                new java.util.ArrayList<>(convertSet(list, MesProTaskDO::getProcessId)));
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(
                convertSet(list, MesProTaskDO::getItemId));
        Map<Long, MesMdClientDO> clientMap = clientService.getClientMap(
                convertSet(list, MesProTaskDO::getClientId));
        // 拼接 VO
        return convertList(list, task -> {
            MesProTaskRespVO vo = BeanUtils.toBean(task, MesProTaskRespVO.class);
            findAndThen(workOrderMap, task.getWorkOrderId(), wo ->
                    vo.setWorkOrderCode(wo.getCode()).setWorkOrderName(wo.getName()).setRequestDate(wo.getRequestDate()));
            findAndThen(workstationMap, task.getWorkstationId(), ws ->
                    vo.setWorkstationCode(ws.getCode()).setWorkstationName(ws.getName()));
            findAndThen(processMap, task.getProcessId(), p ->
                    vo.setProcessName(p.getName()));
            findAndThen(itemMap, task.getItemId(), item ->
                    vo.setItemCode(item.getCode()).setItemName(item.getName()).setItemSpec(item.getSpecification()));
            findAndThen(clientMap, task.getClientId(), c ->
                    vo.setClientName(c.getName()));
            return vo;
        });
    }

}
