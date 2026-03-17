package cn.iocoder.yudao.module.mes.service.pro.feedback;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.feedback.vo.MesProFeedbackPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.feedback.vo.MesProFeedbackSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.feedback.MesProFeedbackDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProcessDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.feedback.MesProFeedbackMapper;
import cn.iocoder.yudao.module.mes.enums.pro.MesProFeedbackStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.route.MesProRouteProcessService;
import cn.iocoder.yudao.module.mes.service.pro.task.MesProTaskService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Resource
    private MesMdWorkstationService workstationService;

    @Resource
    @Lazy // 避免循环依赖
    private MesProTaskService taskService;

    @Override
    public Long createFeedback(MesProFeedbackSaveReqVO createReqVO) {
        // 1. 校验
        validateFeedbackData(createReqVO);

        // 2. 插入
        MesProFeedbackDO feedback = BeanUtils.toBean(createReqVO, MesProFeedbackDO.class)
                .setStatus(MesProFeedbackStatusEnum.PREPARE.getStatus());
        feedbackMapper.insert(feedback);
        return feedback.getId();
    }

    @Override
    public void updateFeedback(MesProFeedbackSaveReqVO updateReqVO) {
        // 1.1 校验存在 + 草稿状态
        validateFeedbackStatusPrepare(updateReqVO.getId());
        // 1.2 校验业务数据
        validateFeedbackData(updateReqVO);

        // 2. 更新
        MesProFeedbackDO updateObj = BeanUtils.toBean(updateReqVO, MesProFeedbackDO.class);
        feedbackMapper.updateById(updateObj);
    }

    @Override
    public void deleteFeedback(Long id) {
        // 1. 校验存在 + 草稿状态
        validateFeedbackStatusPrepare(id);

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
        validateFeedbackStatusPrepare(id);

        // 2. 更新状态为审批中，记录报工人和报工时间
        feedbackMapper.updateById(new MesProFeedbackDO().setId(id)
                .setStatus(MesProFeedbackStatusEnum.APPROVING.getStatus())
                .setFeedbackUserId(getLoginUserId())
                .setFeedbackTime(LocalDateTime.now()));
    }

    @Override
    public void rejectFeedback(Long id) {
        // 1. 校验存在 + 审批中状态
        validateFeedbackStatusApproving(id);

        // 2. 更新状态为草稿
        feedbackMapper.updateById(new MesProFeedbackDO().setId(id)
                .setStatus(MesProFeedbackStatusEnum.PREPARE.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishFeedback(Long id) {
        // 1. 校验存在 + 审批中状态
        MesProFeedbackDO feedback = validateFeedbackStatusApproving(id);

        // 2.1 查询工序的 checkFlag，决定目标状态
        boolean checkFlag = getCheckFlag(feedback.getRouteId(), feedback.getProcessId());
        Integer targetStatus = checkFlag
                ? MesProFeedbackStatusEnum.UNCHECK.getStatus()    // 需要检验 → 待检验
                : MesProFeedbackStatusEnum.FINISHED.getStatus();  // 无需检验 → 已完成
        // 2.2 更新状态 + 审核人
        feedbackMapper.updateById(new MesProFeedbackDO().setId(id)
                .setStatus(targetStatus)
                .setApproveUserId(getLoginUserId()));

        // 3. 如果不需要检验（直接完成），执行后续操作
        if (BooleanUtil.isFalse(checkFlag)) {
            // TODO @芋艿：WM 物料消耗（WmItemConsumeService）未迁移，此处需补充物料消耗逻辑
            // TODO @芋艿：WM 产品产出（WmProductProduceService）未迁移，此处需补充产品产出逻辑
            // TODO @芋艿：updateProTaskAndWorkorder 待 pro_task 服务迁移后补充级联更新任务/工单的已生产数量
        }
    }

    @Override
    public void cancelFeedback(Long id) {
        // 1.1 校验存在
        MesProFeedbackDO feedback = validateFeedbackExists(id);
        // 1.2 只有草稿或审批中可以取消（已完成、已取消不可操作）
        if (ObjectUtils.equalsAny(feedback.getStatus(),
                MesProFeedbackStatusEnum.FINISHED.getStatus(),
                MesProFeedbackStatusEnum.CANCELED.getStatus())) {
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

    private MesProFeedbackDO validateFeedbackStatusPrepare(Long id) {
        MesProFeedbackDO feedback = validateFeedbackExists(id);
        if (ObjUtil.notEqual(feedback.getStatus(), MesProFeedbackStatusEnum.PREPARE.getStatus())) {
            throw exception(PRO_FEEDBACK_NOT_PREPARE);
        }
        return feedback;
    }

    private MesProFeedbackDO validateFeedbackStatusApproving(Long id) {
        MesProFeedbackDO feedback = validateFeedbackExists(id);
        if (ObjUtil.notEqual(feedback.getStatus(), MesProFeedbackStatusEnum.APPROVING.getStatus())) {
            throw exception(PRO_FEEDBACK_NOT_APPROVING);
        }
        return feedback;
    }

    /**
     * 校验报工单的业务数据（创建 & 修改共用）
     *
     * @param reqVO 报工请求
     */
    private void validateFeedbackData(MesProFeedbackSaveReqVO reqVO) {
        // 1. 校验工作站存在
        workstationService.validateWorkstationExists(reqVO.getWorkstationId());

        // 2.1 校验工艺路线 + 工序配置有效
        MesProRouteProcessDO routeProcess = routeProcessService.getRouteProcessByRouteIdAndProcessId(
                reqVO.getRouteId(), reqVO.getProcessId());
        if (routeProcess == null) {
            throw exception(PRO_FEEDBACK_ROUTE_PROCESS_INVALID);
        }
        // 2.2 校验数量
        boolean checkFlag = Boolean.TRUE.equals(routeProcess.getCheckFlag());
        if (checkFlag) {
            // 需要检验：只需填报工数量，且必须 > 0
            if (reqVO.getFeedbackQuantity() == null
                    || reqVO.getFeedbackQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw exception(PRO_FEEDBACK_QUANTITY_MUST_POSITIVE);
            }
        } else {
            // 不需检验：需填合格品 + 不良品数量，合计 > 0
            BigDecimal qualified = ObjectUtil.defaultIfNull(reqVO.getQualifiedQuantity(), BigDecimal.ZERO);
            BigDecimal unqualified = ObjectUtil.defaultIfNull(reqVO.getUnqualifiedQuantity(), BigDecimal.ZERO);
            if (qualified.add(unqualified).compareTo(BigDecimal.ZERO) <= 0) {
                throw exception(PRO_FEEDBACK_QUALIFIED_UNQUALIFIED_REQUIRED);
            }
        }

        // 3. 校验工单已确认
        workOrderService.validateWorkOrderConfirmed(reqVO.getWorkOrderId());

        // 4. 校验任务存在且未终态（已完成/已取消）
        taskService.validateTaskNotFinished(reqVO.getTaskId());
    }

    /**
     * 获取指定工艺路线中指定工序的 checkFlag
     *
     * @param routeId   工艺路线编号
     * @param processId 工序编号
     * @return 是否需要检验
     */
    private boolean getCheckFlag(Long routeId, Long processId) {
        MesProRouteProcessDO routeProcess = routeProcessService.getRouteProcessByRouteIdAndProcessId(routeId, processId);
        return routeProcess != null && Boolean.TRUE.equals(routeProcess.getCheckFlag());
    }

}
