package cn.iocoder.yudao.module.mes.controller.admin.pro.route;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.route.vo.process.MesProRouteProcessRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.route.vo.process.MesProRouteProcessSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.MesProProcessDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProcessDO;
import cn.iocoder.yudao.module.mes.service.pro.MesProProcessService;
import cn.iocoder.yudao.module.mes.service.pro.route.MesProRouteProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

@Tag(name = "管理后台 - MES 工艺路线工序")
@RestController
@RequestMapping("/mes/pro/route-process")
@Validated
public class MesProRouteProcessController {

    @Resource
    private MesProRouteProcessService routeProcessService;

    @Resource
    private MesProProcessService processService;

    @PostMapping("/create")
    @Operation(summary = "创建工艺路线工序")
    @PreAuthorize("@ss.hasPermission('mes:pro-route:update')")
    public CommonResult<Long> createRouteProcess(@Valid @RequestBody MesProRouteProcessSaveReqVO createReqVO) {
        return success(routeProcessService.createRouteProcess(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新工艺路线工序")
    @PreAuthorize("@ss.hasPermission('mes:pro-route:update')")
    public CommonResult<Boolean> updateRouteProcess(@Valid @RequestBody MesProRouteProcessSaveReqVO updateReqVO) {
        routeProcessService.updateRouteProcess(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工艺路线工序")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:pro-route:update')")
    public CommonResult<Boolean> deleteRouteProcess(@RequestParam("id") Long id) {
        routeProcessService.deleteRouteProcess(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得工艺路线工序")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:pro-route:query')")
    public CommonResult<MesProRouteProcessRespVO> getRouteProcess(@RequestParam("id") Long id) {
        MesProRouteProcessDO routeProcess = routeProcessService.getRouteProcess(id);
        return success(buildRouteProcessRespVO(routeProcess));
    }

    @GetMapping("/list-by-route")
    @Operation(summary = "按工艺路线获得工序列表")
    @Parameter(name = "routeId", description = "工艺路线编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:pro-route:query')")
    public CommonResult<List<MesProRouteProcessRespVO>> getRouteProcessListByRoute(@RequestParam("routeId") Long routeId) {
        List<MesProRouteProcessDO> list = routeProcessService.getRouteProcessListByRouteId(routeId);
        return success(buildRouteProcessRespVOList(list));
    }

    // ==================== 拼接 VO ====================

    private List<MesProRouteProcessRespVO> buildRouteProcessRespVOList(List<MesProRouteProcessDO> list) {
        // TODO @AI：collutil 去判断空；
        if (list.isEmpty()) {
            return List.of();
        }
        // 批量查询工序信息
        // TODO @AI：我记得有 CollUtil 可以搞多个的；
        List<Long> processIds = new ArrayList<>();
        for (MesProRouteProcessDO item : list) {
            processIds.add(item.getProcessId());
            if (item.getNextProcessId() != null) {
                processIds.add(item.getNextProcessId());
            }
        }
        // TODO @AI：processService.getProcessMap 直接返回 Map 就好了；
        List<MesProProcessDO> processList = processService.getProcessList(processIds);
        Map<Long, MesProProcessDO> processMap = convertMap(processList, MesProProcessDO::getId);
        // 拼装
        List<MesProRouteProcessRespVO> result = BeanUtils.toBean(list, MesProRouteProcessRespVO.class);
        for (MesProRouteProcessRespVO vo : result) {
            // TODO @AI：MapUtil findMap
            MesProProcessDO process = processMap.get(vo.getProcessId());
            if (process != null) {
                vo.setProcessCode(process.getCode()).setProcessName(process.getName());
            }
            // TODO @AI：MapUtil findMap
            if (vo.getNextProcessId() != null) {
                MesProProcessDO nextProcess = processMap.get(vo.getNextProcessId());
                if (nextProcess != null) {
                    vo.setNextProcessName(nextProcess.getName());
                }
            }
        }
        return result;
    }

    // TODO @AI：需要的地方，直接调用 buildRouteProcessRespVOList；
    private MesProRouteProcessRespVO buildRouteProcessRespVO(MesProRouteProcessDO routeProcess) {
        if (routeProcess == null) {
            return null;
        }
        return buildRouteProcessRespVOList(List.of(routeProcess)).get(0);
    }

}
