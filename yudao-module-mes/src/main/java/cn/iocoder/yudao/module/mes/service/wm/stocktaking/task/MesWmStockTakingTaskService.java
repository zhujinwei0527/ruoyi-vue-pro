package cn.iocoder.yudao.module.mes.service.wm.stocktaking.task;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.line.MesWmStockTakingTaskLineBatchUpdateReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.MesWmStockTakingTaskPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.MesWmStockTakingTaskSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskLineDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * MES 盘点任务 Service 接口
 *
 * @author 芋道源码
 */
public interface MesWmStockTakingTaskService {

    /**
     * 创建盘点任务
     *
     * @param createReqVO 创建信息
     * @return 盘点任务编号
     */
    Long createStockTakingTask(@Valid MesWmStockTakingTaskSaveReqVO createReqVO);

    /**
     * 更新盘点任务
     *
     * @param updateReqVO 更新信息
     */
    void updateStockTakingTask(@Valid MesWmStockTakingTaskSaveReqVO updateReqVO);

    /**
     * 删除盘点任务
     *
     * @param id 盘点任务编号
     */
    void deleteStockTakingTask(Long id);

    /**
     * 提交盘点任务
     *
     * @param id 盘点任务编号
     */
    void submitStockTakingTask(Long id);

    /**
     * 完成盘点任务
     *
     * @param id 盘点任务编号
     */
    void finishStockTakingTask(Long id);

    /**
     * 取消盘点任务
     *
     * @param id 盘点任务编号
     */
    void cancelStockTakingTask(Long id);

    /**
     * 批量更新盘点任务行
     *
     * @param updateReqVO 更新信息
     */
    void updateStockTakingTaskLines(@Valid MesWmStockTakingTaskLineBatchUpdateReqVO updateReqVO);

    /**
     * 获得盘点任务
     *
     * @param id 盘点任务编号
     * @return 盘点任务
     */
    MesWmStockTakingTaskDO getStockTakingTask(Long id);

    /**
     * 校验盘点任务是否存在
     *
     * @param id 盘点任务编号
     * @return 盘点任务
     */
    MesWmStockTakingTaskDO validateStockTakingTaskExists(Long id);

    /**
     * 获得盘点任务列表
     *
     * @param ids 盘点任务编号集合
     * @return 盘点任务列表
     */
    List<MesWmStockTakingTaskDO> getStockTakingTaskList(Collection<Long> ids);

    /**
     * 获得盘点任务 Map
     *
     * @param ids 盘点任务编号集合
     * @return 盘点任务 Map
     */
    default Map<Long, MesWmStockTakingTaskDO> getStockTakingTaskMap(Collection<Long> ids) {
        return convertMap(getStockTakingTaskList(ids), MesWmStockTakingTaskDO::getId);
    }

    /**
     * 分页查询盘点任务
     *
     * @param pageReqVO 分页查询条件
     * @return 盘点任务分页结果
     */
    PageResult<MesWmStockTakingTaskDO> getStockTakingTaskPage(MesWmStockTakingTaskPageReqVO pageReqVO);

    /**
     * 获得盘点任务行列表
     *
     * @param taskId 盘点任务编号
     * @return 盘点任务行列表
     */
    List<MesWmStockTakingTaskLineDO> getStockTakingTaskLineList(Long taskId);

}
