package cn.iocoder.yudao.module.mes.controller.admin.pro.card;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.card.vo.MesProCardPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.card.vo.MesProCardRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.card.vo.MesProCardSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.card.MesProCardDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.pro.card.MesProCardService;
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
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - MES 生产流转卡")
@RestController
@RequestMapping("/mes/pro/card")
@Validated
public class MesProCardController {

    @Resource
    private MesProCardService cardService;

    @Resource
    private MesProWorkOrderService workOrderService;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesMdUnitMeasureService unitMeasureService;

    @PostMapping("/create")
    @Operation(summary = "创建生产流转卡")
    @PreAuthorize("@ss.hasPermission('mes:pro-card:create')")
    public CommonResult<Long> createCard(@Valid @RequestBody MesProCardSaveReqVO createReqVO) {
        return success(cardService.createCard(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新生产流转卡")
    @PreAuthorize("@ss.hasPermission('mes:pro-card:update')")
    public CommonResult<Boolean> updateCard(@Valid @RequestBody MesProCardSaveReqVO updateReqVO) {
        cardService.updateCard(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除生产流转卡")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:pro-card:delete')")
    public CommonResult<Boolean> deleteCard(@RequestParam("id") Long id) {
        cardService.deleteCard(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得生产流转卡")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:pro-card:query')")
    public CommonResult<MesProCardRespVO> getCard(@RequestParam("id") Long id) {
        MesProCardDO card = cardService.getCard(id);
        if (card == null) {
            return success(null);
        }
        return success(buildCardRespVOList(List.of(card)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得生产流转卡分页")
    @PreAuthorize("@ss.hasPermission('mes:pro-card:query')")
    public CommonResult<PageResult<MesProCardRespVO>> getCardPage(@Valid MesProCardPageReqVO pageReqVO) {
        PageResult<MesProCardDO> pageResult = cardService.getCardPage(pageReqVO);
        return success(new PageResult<>(buildCardRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出生产流转卡 Excel")
    @PreAuthorize("@ss.hasPermission('mes:pro-card:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCardExcel(@Valid MesProCardPageReqVO pageReqVO,
                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MesProCardDO> list = cardService.getCardPage(pageReqVO).getList();
        List<MesProCardRespVO> voList = buildCardRespVOList(list);
        ExcelUtils.write(response, "生产流转卡.xls", "数据", MesProCardRespVO.class, voList);
    }

    // ==================== 拼接 VO ====================

    private List<MesProCardRespVO> buildCardRespVOList(List<MesProCardDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 批量获取关联数据
        Map<Long, MesProWorkOrderDO> workOrderMap = workOrderService.getWorkOrderMap(
                convertSet(list, MesProCardDO::getWorkOrderId));
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(
                convertSet(list, MesProCardDO::getItemId));
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(list, MesProCardDO::getUnitMeasureId));
        // 2. 拼接 VO
        return BeanUtils.toBean(list, MesProCardRespVO.class, vo -> {
            MapUtils.findAndThen(workOrderMap, vo.getWorkOrderId(), workOrder ->
                    vo.setWorkOrderCode(workOrder.getCode()).setWorkOrderName(workOrder.getName()));
            MapUtils.findAndThen(itemMap, vo.getItemId(), item ->
                    vo.setItemCode(item.getCode()).setItemName(item.getName()));
            MapUtils.findAndThen(unitMeasureMap, vo.getUnitMeasureId(),
                    unitMeasure -> vo.setUnitMeasureName(unitMeasure.getName()));
        });
    }

}
