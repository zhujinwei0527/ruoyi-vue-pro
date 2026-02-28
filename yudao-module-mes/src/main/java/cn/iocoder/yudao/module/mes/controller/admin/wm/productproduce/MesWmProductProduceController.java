package cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.MesWmProductProducePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.MesWmProductProduceRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.MesWmProductProduceSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.process.MesProProcessDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.task.MesProTaskDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceDO;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.process.MesProProcessService;
import cn.iocoder.yudao.module.mes.service.pro.task.MesProTaskService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import cn.iocoder.yudao.module.mes.service.wm.productproduce.MesWmProductProduceService;
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

@Tag(name = "管理后台 - MES 生产入库单")
@RestController
@RequestMapping("/mes/wm/product-produce")
@Validated
public class MesWmProductProduceController {

    @Resource
    private MesWmProductProduceService produceService;
    @Resource
    private MesMdWorkstationService workstationService;
    @Resource
    private MesProWorkOrderService workOrderService;
    @Resource
    private MesProProcessService processService;
    @Resource
    private MesProTaskService taskService;

    @PostMapping("/create")
    @Operation(summary = "创建生产入库单")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:create')")
    public CommonResult<Long> createProductProduce(@Valid @RequestBody MesWmProductProduceSaveReqVO createReqVO) {
        return success(produceService.createProductProduce(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改生产入库单")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:update')")
    public CommonResult<Boolean> updateProductProduce(@Valid @RequestBody MesWmProductProduceSaveReqVO updateReqVO) {
        produceService.updateProductProduce(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除生产入库单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:delete')")
    public CommonResult<Boolean> deleteProductProduce(@RequestParam("id") Long id) {
        produceService.deleteProductProduce(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得生产入库单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:query')")
    public CommonResult<MesWmProductProduceRespVO> getProductProduce(@RequestParam("id") Long id) {
        MesWmProductProduceDO produce = produceService.getProductProduce(id);
        if (produce == null) {
            return success(null);
        }
        return success(buildRespVOList(Collections.singletonList(produce)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得生产入库单分页")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:query')")
    public CommonResult<PageResult<MesWmProductProduceRespVO>> getProductProducePage(
            @Valid MesWmProductProducePageReqVO pageReqVO) {
        PageResult<MesWmProductProduceDO> pageResult = produceService.getProductProducePage(pageReqVO);
        return success(new PageResult<>(buildRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出生产入库单 Excel")
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportProductProduceExcel(@Valid MesWmProductProducePageReqVO pageReqVO,
                                          HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<MesWmProductProduceDO> pageResult = produceService.getProductProducePage(pageReqVO);
        ExcelUtils.write(response, "生产入库单.xls", "数据", MesWmProductProduceRespVO.class,
                buildRespVOList(pageResult.getList()));
    }

    @PutMapping("/finish")
    @Operation(summary = "完成生产入库单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:update-status')")
    public CommonResult<Boolean> finishProductProduce(@RequestParam("id") Long id) {
        produceService.finishProductProduce(id);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消生产入库单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:update-status')")
    public CommonResult<Boolean> cancelProductProduce(@RequestParam("id") Long id) {
        produceService.cancelProductProduce(id);
        return success(true);
    }

    @GetMapping("/check-quantity")
    @Operation(summary = "校验生产入库单数量", description = "校验每行明细数量之和是否等于行入库数量")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:wm-product-produce:query')")
    public CommonResult<Boolean> checkProductProduceQuantity(@RequestParam("id") Long id) {
        return success(produceService.checkProductProduceQuantity(id));
    }

    // ==================== 拼接 VO ====================

    private List<MesWmProductProduceRespVO> buildRespVOList(List<MesWmProductProduceDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 获得关联数据
        Map<Long, MesMdWorkstationDO> workstationMap = workstationService.getWorkstationMap(
                convertSet(list, MesWmProductProduceDO::getWorkstationId));
        Map<Long, MesProWorkOrderDO> workOrderMap = workOrderService.getWorkOrderMap(
                convertSet(list, MesWmProductProduceDO::getWorkOrderId));
        Map<Long, MesProProcessDO> processMap = processService.getProcessMap(
                convertSet(list, MesWmProductProduceDO::getProcessId));
        Map<Long, MesProTaskDO> taskMap = taskService.getTaskMap(
                convertSet(list, MesWmProductProduceDO::getTaskId));
        // 2. 构建结果
        return BeanUtils.toBean(list, MesWmProductProduceRespVO.class, vo -> {
            // 2.1 填充工作站名称
            MapUtils.findAndThen(workstationMap, vo.getWorkstationId(),
                    workstation -> vo.setWorkstationName(workstation.getName()));
            // 2.2 填充工单编号
            MapUtils.findAndThen(workOrderMap, vo.getWorkOrderId(),
                    workOrder -> vo.setWorkOrderCode(workOrder.getCode()));
            // 2.3 填充工序名称
            MapUtils.findAndThen(processMap, vo.getProcessId(),
                    process -> vo.setProcessName(process.getName()));
            // 2.4 填充任务编号
            MapUtils.findAndThen(taskMap, vo.getTaskId(),
                    task -> vo.setTaskCode(task.getCode()));
        });
    }

}
