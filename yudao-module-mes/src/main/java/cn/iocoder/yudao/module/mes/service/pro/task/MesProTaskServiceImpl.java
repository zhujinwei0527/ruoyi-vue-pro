package cn.iocoder.yudao.module.mes.service.pro.task;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.task.vo.MesProTaskPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.task.vo.MesProTaskSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.task.MesProTaskDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.task.MesProTaskMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.PRO_TASK_NOT_EXISTS;

/**
 * MES 生产任务 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesProTaskServiceImpl implements MesProTaskService {

    @Resource
    private MesProTaskMapper taskMapper;

    @Override
    public Long createTask(MesProTaskSaveReqVO createReqVO) {
        MesProTaskDO task = BeanUtils.toBean(createReqVO, MesProTaskDO.class);
        taskMapper.insert(task);
        return task.getId();
    }

    @Override
    public void updateTask(MesProTaskSaveReqVO updateReqVO) {
        validateTaskExists(updateReqVO.getId());
        MesProTaskDO updateObj = BeanUtils.toBean(updateReqVO, MesProTaskDO.class);
        taskMapper.updateById(updateObj);
    }

    @Override
    public void deleteTask(Long id) {
        validateTaskExists(id);
        taskMapper.deleteById(id);
    }

    @Override
    public MesProTaskDO getTask(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public PageResult<MesProTaskDO> getTaskPage(MesProTaskPageReqVO pageReqVO) {
        return taskMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesProTaskDO> getTaskSimpleList(Long workOrderId) {
        if (workOrderId != null) {
            return taskMapper.selectListByWorkOrderId(workOrderId);
        }
        return taskMapper.selectList();
    }

    @Override
    public void validateTaskExists(Long id) {
        if (taskMapper.selectById(id) == null) {
            throw exception(PRO_TASK_NOT_EXISTS);
        }
    }

}
