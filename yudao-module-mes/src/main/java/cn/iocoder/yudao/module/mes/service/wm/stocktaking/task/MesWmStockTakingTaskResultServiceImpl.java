package cn.iocoder.yudao.module.mes.service.wm.stocktaking.task;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.stocktaking.task.vo.result.MesWmStockTakingTaskResultPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskResultDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.stocktaking.task.MesWmStockTakingTaskResultMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * MES 盘点结果 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesWmStockTakingTaskResultServiceImpl implements MesWmStockTakingTaskResultService {

    @Resource
    private MesWmStockTakingTaskResultMapper stockTakingTaskResultMapper;

    @Override
    public PageResult<MesWmStockTakingTaskResultDO> getStockTakingTaskResultPage(
            MesWmStockTakingTaskResultPageReqVO pageReqVO) {
        return stockTakingTaskResultMapper.selectPage(pageReqVO);
    }

    @Override
    public MesWmStockTakingTaskResultDO getStockTakingTaskResult(Long id) {
        return stockTakingTaskResultMapper.selectById(id);
    }

    @Override
    public List<MesWmStockTakingTaskResultDO> getStockTakingTaskResultList(Long taskId) {
        return stockTakingTaskResultMapper.selectListByTaskId(taskId);
    }

    @Override
    public void createStockTakingTaskResult(MesWmStockTakingTaskResultDO result) {
        stockTakingTaskResultMapper.insert(result);
    }

    @Override
    public void updateStockTakingTaskResult(MesWmStockTakingTaskResultDO result) {
        stockTakingTaskResultMapper.updateById(result);
    }

    @Override
    public void deleteStockTakingTaskResult(Long id) {
        stockTakingTaskResultMapper.deleteById(id);
    }

    @Override
    public void deleteStockTakingTaskResultByTaskId(Long taskId) {
        stockTakingTaskResultMapper.deleteByTaskId(taskId);
    }

}
