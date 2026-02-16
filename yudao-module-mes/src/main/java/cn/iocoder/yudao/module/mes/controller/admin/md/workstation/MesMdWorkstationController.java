package cn.iocoder.yudao.module.mes.controller.admin.md.workstation;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkshopDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationDO;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkshopService;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
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
import java.util.Map;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.MapUtils.findAndThen;

@Tag(name = "管理后台 - MES 工位")
@RestController
@RequestMapping("/mes/md-workstation")
@Validated
public class MesMdWorkstationController {

    @Resource
    private MesMdWorkstationService workstationService;

    @Resource
    private MesMdWorkshopService workshopService;

    @PostMapping("/create")
    @Operation(summary = "创建工位")
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:create')")
    public CommonResult<Long> createWorkstation(@Valid @RequestBody MesMdWorkstationSaveReqVO createReqVO) {
        return success(workstationService.createWorkstation(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新工位")
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:update')")
    public CommonResult<Boolean> updateWorkstation(@Valid @RequestBody MesMdWorkstationSaveReqVO updateReqVO) {
        workstationService.updateWorkstation(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工位")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:delete')")
    public CommonResult<Boolean> deleteWorkstation(@RequestParam("id") Long id) {
        workstationService.deleteWorkstation(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得工位")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:query')")
    public CommonResult<MesMdWorkstationRespVO> getWorkstation(@RequestParam("id") Long id) {
        MesMdWorkstationDO workstation = workstationService.getWorkstation(id);
        MesMdWorkstationRespVO respVO = BeanUtils.toBean(workstation, MesMdWorkstationRespVO.class);
        if (respVO != null && respVO.getWorkshopId() != null) {
            MesMdWorkshopDO workshop = workshopService.getWorkshop(respVO.getWorkshopId());
            if (workshop != null) {
                respVO.setWorkshopName(workshop.getName());
            }
        }
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得工位分页")
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:query')")
    public CommonResult<PageResult<MesMdWorkstationRespVO>> getWorkstationPage(@Valid MesMdWorkstationPageReqVO pageReqVO) {
        PageResult<MesMdWorkstationDO> pageResult = workstationService.getWorkstationPage(pageReqVO);
        PageResult<MesMdWorkstationRespVO> voPageResult = BeanUtils.toBean(pageResult, MesMdWorkstationRespVO.class);
        // 拼装车间名称
        Map<Long, MesMdWorkshopDO> workshopMap = cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap(
                workshopService.getWorkshopListByStatus(null),
                MesMdWorkshopDO::getId);
        voPageResult.getList().forEach(vo -> findAndThen(workshopMap, vo.getWorkshopId(),
                workshop -> vo.setWorkshopName(workshop.getName())));
        return success(voPageResult);
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得工位精简列表", description = "只包含被开启的工位，主要用于前端的下拉选项")
    public CommonResult<List<MesMdWorkstationRespVO>> getWorkstationSimpleList() {
        List<MesMdWorkstationDO> list = workstationService.getWorkstationListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return success(convertList(list, ws -> new MesMdWorkstationRespVO()
                .setId(ws.getId()).setName(ws.getName()).setCode(ws.getCode())));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出工位 Excel")
    @PreAuthorize("@ss.hasPermission('mes:md-workstation:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWorkstationExcel(@Valid MesMdWorkstationPageReqVO pageReqVO,
                                        HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        // TODO @AI：导出是不是也要处理下关联字段？比如车间名称。
        List<MesMdWorkstationDO> list = workstationService.getWorkstationPage(pageReqVO).getList();
        ExcelUtils.write(response, "工位.xls", "数据", MesMdWorkstationRespVO.class,
                BeanUtils.toBean(list, MesMdWorkstationRespVO.class));
    }

}
