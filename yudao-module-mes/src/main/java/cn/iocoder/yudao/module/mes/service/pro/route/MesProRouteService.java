package cn.iocoder.yudao.module.mes.service.pro.route;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.pro.route.vo.MesProRoutePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.route.vo.MesProRouteSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 工艺路线 Service 接口
 *
 * @author 芋道源码
 */
public interface MesProRouteService {

    /**
     * 创建工艺路线
     */
    Long createRoute(@Valid MesProRouteSaveReqVO createReqVO);

    /**
     * 更新工艺路线
     */
    void updateRoute(@Valid MesProRouteSaveReqVO updateReqVO);

    /**
     * 删除工艺路线
     */
    void deleteRoute(Long id);

    /**
     * 获得工艺路线
     */
    MesProRouteDO getRoute(Long id);

    /**
     * 获得工艺路线分页
     */
    PageResult<MesProRouteDO> getRoutePage(MesProRoutePageReqVO pageReqVO);

    /**
     * 获得启用状态的工艺路线列表
     */
    List<MesProRouteDO> getRouteListByStatus(Integer status);

}
