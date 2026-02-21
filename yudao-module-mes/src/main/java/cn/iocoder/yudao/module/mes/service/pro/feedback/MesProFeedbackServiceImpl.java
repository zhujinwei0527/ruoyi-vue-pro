package cn.iocoder.yudao.module.mes.service.pro.feedback;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.feedback.vo.MesProFeedbackPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.feedback.vo.MesProFeedbackSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.feedback.MesProFeedbackDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProcessDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.feedback.MesProFeedbackMapper;
import cn.iocoder.yudao.module.mes.enums.pro.MesProFeedbackStatusEnum;
import cn.iocoder.yudao.module.mes.enums.pro.MesProWorkOrderStatusEnum;
import cn.iocoder.yudao.module.mes.service.pro.route.MesProRouteProcessService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 生产报工 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesProFeedbackServiceImpl implements MesProFeedbackService {

    @Resource
    private MesProFeedbackMapper feedbackMapper;

    @Resource
    private MesProWorkOrderService workOrderService;

    @Resource
    private MesProRouteProcessService routeProcessService;

    @Override
    public Long createFeedback(MesProFeedbackSaveReqVO createReqVO) {
        // 1.1 校验工单已确认
        // TODO @AI： workOrderService 里抽个校验方法；
        MesProWorkOrderDO workOrder = workOrderService.validateWorkOrderExists(createReqVO.getWorkOrderId());
        if (ObjUtil.notEqual(workOrder.getStatus(), MesProWorkOrderStatusEnum.CONFIRMED.getStatus())) {
            throw exception(PRO_FEEDBACK_WORK_ORDER_NOT_CONFIRMED);
        }
        // TODO @芋艿：校验 pro_task 存在且未完成（待 pro_task 服务迁移）

        // TODO @AI：2 和 3 融合成 插入；
        // 2. 自动生成报工单编号
        MesProFeedbackDO feedback = BeanUtils.toBean(createReqVO, MesProFeedbackDO.class);
        // TODO @AI：这个 id 前端生成，createReqVO 必须传递；
        feedback.setCode("FB" + IdUtil.getSnowflakeNextIdStr());
        feedback.setStatus(MesProFeedbackStatusEnum.PREPARE.getStatus());

        // 3. 插入
        feedbackMapper.insert(feedback);
        return feedback.getId();
    }

    @Override
    public void updateFeedback(MesProFeedbackSaveReqVO updateReqVO) {
        // 1.1 校验存在
        MesProFeedbackDO feedback = validateFeedbackExists(updateReqVO.getId());
        // 1.2 只能修改草稿状态
        // TODO @AI：抽个校验方法，校验状态为草稿；
        if (ObjUtil.notEqual(feedback.getStatus(), MesProFeedbackStatusEnum.PREPARE.getStatus())) {
            throw exception(PRO_FEEDBACK_NOT_PREPARE);
        }

        // 2. 更新
        MesProFeedbackDO updateObj = BeanUtils.toBean(updateReqVO, MesProFeedbackDO.class);
        feedbackMapper.updateById(updateObj);
    }

    @Override
    public void deleteFeedback(Long id) {
        // 1.1 校验存在
        MesProFeedbackDO feedback = validateFeedbackExists(id);
        // 1.2 只能删除草稿状态
        // TODO @AI：抽个校验方法，校验状态为草稿；
        if (ObjUtil.notEqual(feedback.getStatus(), MesProFeedbackStatusEnum.PREPARE.getStatus())) {
            throw exception(PRO_FEEDBACK_NOT_PREPARE);
        }

        // 2. 删除
        feedbackMapper.deleteById(id);
    }

    @Override
    public MesProFeedbackDO getFeedback(Long id) {
        return feedbackMapper.selectById(id);
    }

    @Override
    public PageResult<MesProFeedbackDO> getFeedbackPage(MesProFeedbackPageReqVO pageReqVO) {
        return feedbackMapper.selectPage(pageReqVO);
    }

    @Override
    public void submitFeedback(Long id) {
        // 1. 校验存在 + 草稿状态
        MesProFeedbackDO feedback = validateFeedbackExists(id);
        // TODO @AI：抽个校验方法，校验状态为草稿；
        if (ObjUtil.notEqual(feedback.getStatus(), MesProFeedbackStatusEnum.PREPARE.getStatus())) {
            throw exception(PRO_FEEDBACK_STATUS_ERROR);
        }

        // 2. 更新状态为审批中，记录报工人和报工时间
        feedbackMapper.updateById(new MesProFeedbackDO().setId(id)
                .setStatus(MesProFeedbackStatusEnum.APPROVING.getStatus())
                .setFeedbackUserId(getLoginUserId())
                .setFeedbackTime(LocalDateTime.now()));
    }

    @Override
    public void rejectFeedback(Long id) {
        // 1. 校验存在 + 审批中状态
        MesProFeedbackDO feedback = validateFeedbackExists(id);
        // TODO @AI：抽个校验方法，校验状态为审批中；
        if (ObjUtil.notEqual(feedback.getStatus(), MesProFeedbackStatusEnum.APPROVING.getStatus())) {
            throw exception(PRO_FEEDBACK_NOT_APPROVING);
        }

        // 2. 更新状态为草稿
        feedbackMapper.updateById(new MesProFeedbackDO().setId(id)
                .setStatus(MesProFeedbackStatusEnum.PREPARE.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeFeedback(Long id) {
        // 1. 校验存在 + 审批中状态
        MesProFeedbackDO feedback = validateFeedbackExists(id);
        // TODO @AI：抽个校验方法，校验状态为审批中；
        if (ObjUtil.notEqual(feedback.getStatus(), MesProFeedbackStatusEnum.APPROVING.getStatus())) {
            throw exception(PRO_FEEDBACK_NOT_APPROVING);
        }

        // 2.1 查询工序的 checkFlag，决定目标状态
        boolean checkFlag = getCheckFlag(feedback.getRouteId(), feedback.getProcessId());
        Integer targetStatus = checkFlag
                ? MesProFeedbackStatusEnum.UNCHECK.getStatus()    // 需要检验 → 待检验
                : MesProFeedbackStatusEnum.FINISHED.getStatus();  // 无需检验 → 已完成
        // 2.2 更新状态 + 审核人
        feedbackMapper.updateById(new MesProFeedbackDO().setId(id)
                .setStatus(targetStatus)
                .setApproveUserId(getLoginUserId()));

        // 4. 如果不需要检验（直接完成），执行后续操作
        // TODO @AI：BoolUtil.isFalse(checkFlag)
        if (!checkFlag) {
            // TODO @芋艿：WM 物料消耗（WmItemConsumeService）未迁移，此处需补充物料消耗逻辑
            // TODO @芋艿：WM 产品产出（WmProductProduceService）未迁移，此处需补充产品产出逻辑
            // TODO @芋艿：updateProTaskAndWorkorder 待 pro_task 服务迁移后补充级联更新任务/工单的已生产数量
        }
    }

    @Override
    public void cancelFeedback(Long id) {
        // 1.1 校验存在
        MesProFeedbackDO feedback = validateFeedbackExists(id);
        // 1.2 只有草稿或审批中可以取消
        // TODO @AI: ObjUtils.equalsAny，看起来更简洁；
        if (ObjUtil.equal(feedback.getStatus(), MesProFeedbackStatusEnum.FINISHED.getStatus())
                || ObjUtil.equal(feedback.getStatus(), MesProFeedbackStatusEnum.CANCELED.getStatus())) {
            throw exception(PRO_FEEDBACK_STATUS_ERROR);
        }

        // 2. 更新状态为已取消
        feedbackMapper.updateById(new MesProFeedbackDO().setId(id)
                .setStatus(MesProFeedbackStatusEnum.CANCELED.getStatus()));
    }

    // ==================== 校验方法 ====================

    private MesProFeedbackDO validateFeedbackExists(Long id) {
        MesProFeedbackDO feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            throw exception(PRO_FEEDBACK_NOT_EXISTS);
        }
        return feedback;
    }

    /**
     * 获取指定工艺路线中指定工序的 checkFlag
     *
     * @param routeId   工艺路线编号
     * @param processId 工序编号
     * @return 是否需要检验
     */
    private boolean getCheckFlag(Long routeId, Long processId) {
        List<MesProRouteProcessDO> routeProcesses = routeProcessService.getRouteProcessListByRouteId(routeId);
        // TODO @AI：CollUtil findOne 更简洁；
        return routeProcesses.stream()
                .filter(rp -> ObjUtil.equal(rp.getProcessId(), processId))
                .findFirst()
                .map(rp -> Boolean.TRUE.equals(rp.getCheckFlag()))
                .orElse(false);
    }

}
