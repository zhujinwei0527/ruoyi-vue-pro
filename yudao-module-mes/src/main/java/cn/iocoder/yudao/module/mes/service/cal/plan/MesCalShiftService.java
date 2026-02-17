package cn.iocoder.yudao.module.mes.service.cal.plan;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.cal.plan.vo.shift.MesCalShiftPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.plan.vo.shift.MesCalShiftSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.MesCalShiftDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 计划班次 Service 接口
 *
 * @author 芋道源码
 */
public interface MesCalShiftService {

    /**
     * 创建计划班次
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createShift(@Valid MesCalShiftSaveReqVO createReqVO);

    /**
     * 更新计划班次
     *
     * @param updateReqVO 更新信息
     */
    void updateShift(@Valid MesCalShiftSaveReqVO updateReqVO);

    /**
     * 删除计划班次
     *
     * @param id 编号
     */
    void deleteShift(Long id);

    /**
     * 获得计划班次
     *
     * @param id 编号
     * @return 计划班次
     */
    MesCalShiftDO getShift(Long id);

    /**
     * 获得计划班次分页
     *
     * @param pageReqVO 分页查询
     * @return 计划班次分页
     */
    PageResult<MesCalShiftDO> getShiftPage(MesCalShiftPageReqVO pageReqVO);

    /**
     * 获得指定排班计划的班次列表
     *
     * @param planId 排班计划编号
     * @return 班次列表
     */
    List<MesCalShiftDO> getShiftListByPlanId(Long planId);

    /**
     * 获得指定排班计划的班次数量
     *
     * @param planId 排班计划编号
     * @return 班次数量
     */
    Long getShiftCountByPlanId(Long planId);

    /**
     * 根据轮班方式添加默认班次
     *
     * @param planId    排班计划编号
     * @param shiftType 轮班方式
     */
    void addDefaultShift(Long planId, Integer shiftType);

    /**
     * 根据排班计划编号删除所有班次
     *
     * @param planId 排班计划编号
     */
    void deleteShiftByPlanId(Long planId);

}
