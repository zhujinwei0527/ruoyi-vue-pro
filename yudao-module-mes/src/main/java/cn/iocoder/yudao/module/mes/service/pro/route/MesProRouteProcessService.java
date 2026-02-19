package cn.iocoder.yudao.module.mes.service.pro.route;

import cn.iocoder.yudao.module.mes.controller.admin.pro.route.vo.process.MesProRouteProcessSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProcessDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 工艺路线工序 Service 接口
 *
 * @author 芋道源码
 */
public interface MesProRouteProcessService {

    /**
     * 创建工艺路线工序
     */
    Long createRouteProcess(@Valid MesProRouteProcessSaveReqVO createReqVO);

    /**
     * 更新工艺路线工序
     */
    void updateRouteProcess(@Valid MesProRouteProcessSaveReqVO updateReqVO);

    /**
     * 删除工艺路线工序
     */
    void deleteRouteProcess(Long id);

    /**
     * 获得工艺路线工序
     */
    MesProRouteProcessDO getRouteProcess(Long id);

    /**
     * 按工艺路线获得工序列表
     */
    List<MesProRouteProcessDO> getRouteProcessListByRouteId(Long routeId);

}
