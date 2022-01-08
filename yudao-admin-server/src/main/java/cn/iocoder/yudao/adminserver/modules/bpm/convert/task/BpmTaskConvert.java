package cn.iocoder.yudao.adminserver.modules.bpm.convert.task;

import cn.iocoder.yudao.adminserver.modules.bpm.controller.task.vo.task.BpmTaskTodoPageItemRespVO;
import cn.iocoder.yudao.adminserver.modules.bpm.controller.task.vo.task.TaskStepVO;
import cn.iocoder.yudao.coreservice.modules.system.dal.dataobject.user.SysUserDO;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.SuspensionState;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * Bpm 任务 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface BpmTaskConvert {

    BpmTaskConvert INSTANCE = Mappers.getMapper(BpmTaskConvert.class);

    @Mappings(value = {
            @Mapping(source = "activityName", target = "stepName"),
            @Mapping(source = "assignee", target = "assignee")
    })
    TaskStepVO convert(HistoricActivityInstance instance);

    default List<BpmTaskTodoPageItemRespVO> convertList(List<Task> tasks, Map<String, ProcessInstance> processInstanceMap,
                                                        Map<Long, SysUserDO> userMap) {
        return CollectionUtils.convertList(tasks, task -> {
            ProcessInstance processInstance = processInstanceMap.get(task.getProcessInstanceId());
            return convert(task, processInstance, userMap.get(Long.valueOf(processInstance.getStartUserId())));
        });
    }

    @Mappings({
            @Mapping(source = "task.id", target = "id"),
            @Mapping(source = "task.name", target = "name"),
            @Mapping(source = "task.claimTime", target = "claimTime"),
            @Mapping(source = "task.suspended", target = "status", qualifiedByName = "convertSuspendedToStatus"),
            @Mapping(source = "processInstance.id", target = "processInstance.id"),
            @Mapping(source = "processInstance.startUserId", target = "processInstance.startUserId"),
            @Mapping(source = "processInstance.processDefinitionId", target = "processInstance.processDefinitionId"),
            @Mapping(source = "user.nickname", target = "processInstance.startUserNickname")
    })
    BpmTaskTodoPageItemRespVO convert(Task task, ProcessInstance processInstance, SysUserDO user);

    @Named("convertSuspendedToStatus")
    default Integer convertAssigneeToStatus(boolean suspended) {
        return suspended ? SuspensionState.SUSPENDED.getStateCode() :
                SuspensionState.ACTIVE.getStateCode();
    }

}