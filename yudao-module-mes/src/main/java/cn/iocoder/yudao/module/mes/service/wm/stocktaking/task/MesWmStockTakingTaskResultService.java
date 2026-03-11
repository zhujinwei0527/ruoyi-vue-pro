package cn.iocoder.yudao.module.mes.service.wm.stocktaking.task;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.result.MesWmStockTakingTaskResultPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskResultDO;

import java.util.List;

/**
 * MES 盘点结果 Service 接口
 *
 * @author 芋道源码
 */
public interface MesWmStockTakingTaskResultService {

    /**
     * 分页查询盘点结果
     *
     * @param pageReqVO 分页查询条件
     * @return 盘点结果分页结果
     */
    PageResult<MesWmStockTakingTaskResultDO> getStockTakingTaskResultPage(MesWmStockTakingTaskResultPageReqVO pageReqVO);

    /**
     * 获得盘点结果
     *
     * @param id 盘点结果编号
     * @return 盘点结果
     */
    MesWmStockTakingTaskResultDO getStockTakingTaskResult(Long id);

    /**
     * 获得盘点结果列表
     *
     * @param taskId 盘点任务编号
     * @return 盘点结果列表
     */
    List<MesWmStockTakingTaskResultDO> getStockTakingTaskResultList(Long taskId);

    /**
     * 创建盘点结果
     *
     * @param result 盘点结果
     */
    void createStockTakingTaskResult(MesWmStockTakingTaskResultDO result);

    /**
     * 更新盘点结果
     *
     * @param result 盘点结果
     */
    void updateStockTakingTaskResult(MesWmStockTakingTaskResultDO result);

    /**
     * 删除盘点结果
     *
     * @param id 盘点结果编号
     */
    void deleteStockTakingTaskResult(Long id);

    /**
     * 根据任务编号删除盘点结果
     *
     * @param taskId 任务编号
     */
    void deleteStockTakingTaskResultByTaskId(Long taskId);

}
