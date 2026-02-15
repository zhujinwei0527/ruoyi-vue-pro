package cn.iocoder.yudao.module.mes.controller.admin.md.item;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.md.item.vo.MesMdItemPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.item.vo.MesMdItemRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.item.vo.MesMdItemSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
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
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

@Tag(name = "管理后台 - MES 物料产品")
@RestController
@RequestMapping("/mes/md/item")
@Validated
public class MesMdItemController {

    @Resource
    private MesMdItemService itemService;

    @PostMapping("/create")
    @Operation(summary = "创建物料产品")
    @PreAuthorize("@ss.hasPermission('mes:md-item:create')")
    public CommonResult<Long> createItem(@Valid @RequestBody MesMdItemSaveReqVO createReqVO) {
        return success(itemService.createItem(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新物料产品")
    @PreAuthorize("@ss.hasPermission('mes:md-item:update')")
    public CommonResult<Boolean> updateItem(@Valid @RequestBody MesMdItemSaveReqVO updateReqVO) {
        itemService.updateItem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除物料产品")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:md-item:delete')")
    public CommonResult<Boolean> deleteItem(@RequestParam("id") Long id) {
        itemService.deleteItem(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得物料产品")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:md-item:query')")
    public CommonResult<MesMdItemRespVO> getItem(@RequestParam("id") Long id) {
        MesMdItemDO item = itemService.getItem(id);
        return success(BeanUtils.toBean(item, MesMdItemRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得物料产品分页")
    @PreAuthorize("@ss.hasPermission('mes:md-item:query')")
    public CommonResult<PageResult<MesMdItemRespVO>> getItemPage(@Valid MesMdItemPageReqVO pageReqVO) {
        return success(itemService.getItemVOPage(pageReqVO));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得物料产品精简列表", description = "只包含被开启的物料，主要用于前端的下拉选项")
    public CommonResult<List<MesMdItemRespVO>> getItemSimpleList() {
        List<MesMdItemRespVO> list = itemService.getItemVOListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return success(convertList(list, item -> new MesMdItemRespVO()
                .setId(item.getId()).setName(item.getName()).setCode(item.getCode())
                .setItemTypeId(item.getItemTypeId()).setItemTypeName(item.getItemTypeName())
                .setUnitOfMeasure(item.getUnitOfMeasure())));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出物料产品 Excel")
    @PreAuthorize("@ss.hasPermission('mes:md-item:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportItemExcel(@Valid MesMdItemPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<MesMdItemRespVO> pageResult = itemService.getItemVOPage(pageReqVO);
        // 导出 Excel
        ExcelUtils.write(response, "物料产品.xls", "数据", MesMdItemRespVO.class,
                pageResult.getList());
    }

    // TODO @AI：缺少批量导入；

}
