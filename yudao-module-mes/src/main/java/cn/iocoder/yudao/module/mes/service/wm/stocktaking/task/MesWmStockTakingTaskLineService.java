package cn.iocoder.yudao.module.mes.service.wm.stocktaking.task;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.line.MesWmStockTakingTaskLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.line.MesWmStockTakingTaskLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskLineDO;

import java.util.List;

/**
 * MES 盘点任务行 Service 接口
 *
 * @author 芋道源码
 */
public interface MesWmStockTakingTaskLineService {

    /**
     * 根据盘点方案生成盘点明细行
     *
     * @param task 盘点任务
     * @param isCreate 是否为创建操作（true-创建，false-更新时需先清理旧数据）
     */
    void generateStockTakingLines(MesWmStockTakingTaskDO task, boolean isCreate);

    /**
     * 分页查询盘点任务行
     *
     * @param pageReqVO 分页查询条件
     * @return 盘点任务行分页结果
     */
    PageResult<MesWmStockTakingTaskLineDO> getStockTakingTaskLinePage(MesWmStockTakingTaskLinePageReqVO pageReqVO);

    /**
     * 获得盘点任务行
     *
     * @param id 编号
     * @return 盘点任务行
     */
    MesWmStockTakingTaskLineDO getStockTakingTaskLine(Long id);

    /**
     * 创建盘点任务行
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createStockTakingTaskLine(MesWmStockTakingTaskLineSaveReqVO createReqVO);

    /**
     * 更新盘点任务行
     *
     * @param updateReqVO 更新信息
     */
    void updateStockTakingTaskLine(MesWmStockTakingTaskLineSaveReqVO updateReqVO);

    /**
     * 删除盘点任务行
     *
     * @param id 盘点任务行编号
     */
    void deleteStockTakingTaskLine(Long id);

    /**
     * 根据任务编号获取盘点任务行列表
     *
     * @param taskId 任务编号
     * @return 盘点任务行列表
     */
    List<MesWmStockTakingTaskLineDO> getStockTakingTaskLineListByTaskId(Long taskId);

    /**
     * 根据任务编号删除盘点任务行
     *
     * @param taskId 任务编号
     */
    void deleteStockTakingTaskLineByTaskId(Long taskId);

}
