package cn.iocoder.yudao.module.mes.service.pro.task;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.pro.task.vo.MesProTaskPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.task.vo.MesProTaskSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.task.MesProTaskDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 生产任务 Service 接口
 *
 * @author 芋道源码
 */
public interface MesProTaskService {

    /**
     * 创建生产任务
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTask(@Valid MesProTaskSaveReqVO createReqVO);

    /**
     * 更新生产任务
     *
     * @param updateReqVO 更新信息
     */
    void updateTask(@Valid MesProTaskSaveReqVO updateReqVO);

    /**
     * 删除生产任务
     *
     * @param id 编号
     */
    void deleteTask(Long id);

    /**
     * 获得生产任务
     *
     * @param id 编号
     * @return 生产任务
     */
    MesProTaskDO getTask(Long id);

    /**
     * 获得生产任务分页
     *
     * @param pageReqVO 分页查询
     * @return 生产任务分页
     */
    PageResult<MesProTaskDO> getTaskPage(MesProTaskPageReqVO pageReqVO);

    /**
     * 获得生产任务精简列表
     *
     * @param workOrderId 工单编号（可选）
     * @return 生产任务列表
     */
    List<MesProTaskDO> getTaskSimpleList(Long workOrderId);

    /**
     * 校验任务是否存在
     *
     * @param id 编号
     */
    void validateTaskExists(Long id);

}
