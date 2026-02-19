package cn.iocoder.yudao.module.mes.service.pro.workorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.MesProWorkOrderPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.MesProWorkOrderSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

/**
 * MES 生产工单 Service 接口
 *
 * @author 芋道源码
 */
public interface MesProWorkOrderService {

    /**
     * 创建生产工单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createWorkOrder(@Valid MesProWorkOrderSaveReqVO createReqVO);

    /**
     * 更新生产工单
     *
     * @param updateReqVO 更新信息
     */
    void updateWorkOrder(@Valid MesProWorkOrderSaveReqVO updateReqVO);

    /**
     * 删除生产工单
     *
     * @param id 编号
     */
    void deleteWorkOrder(Long id);

    /**
     * 校验生产工单存在
     *
     * @param id 编号
     * @return 生产工单
     */
    MesProWorkOrderDO validateWorkOrderExists(Long id);

    /**
     * 获得生产工单
     *
     * @param id 编号
     * @return 生产工单
     */
    MesProWorkOrderDO getWorkOrder(Long id);

    /**
     * 获得生产工单分页
     *
     * @param pageReqVO 分页查询
     * @return 生产工单分页
     */
    PageResult<MesProWorkOrderDO> getWorkOrderPage(MesProWorkOrderPageReqVO pageReqVO);

    /**
     * 完成工单
     *
     * @param id 编号
     */
    void finishWorkOrder(Long id);

    /**
     * 取消工单
     *
     * @param id 编号
     */
    void cancelWorkOrder(Long id);

    /**
     * 获得工单列表
     *
     * @param ids 编号数组
     * @return 工单列表
     */
    List<MesProWorkOrderDO> getWorkOrderList(Collection<Long> ids);

}
