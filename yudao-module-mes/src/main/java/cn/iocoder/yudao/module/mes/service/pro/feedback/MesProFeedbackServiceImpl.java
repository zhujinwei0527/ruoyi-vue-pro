package cn.iocoder.yudao.module.mes.service.pro.feedback;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.feedback.vo.MesProFeedbackPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.feedback.vo.MesProFeedbackSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.feedback.MesProFeedbackDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProcessDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemconsume.MesWmItemConsumeDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.task.MesProTaskDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.feedback.MesProFeedbackMapper;
import cn.iocoder.yudao.module.mes.enums.pro.MesProFeedbackStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.route.MesProRouteProcessService;
import cn.iocoder.yudao.module.mes.service.pro.task.MesProTaskService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import cn.iocoder.yudao.module.mes.service.wm.itemconsume.MesWmItemConsumeService;
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

    @Resource
    private MesWmItemConsumeService itemConsumeService;

    @Override
    public Long createFeedback(MesProFeedbackSaveReqVO createReqVO) {
        // 1. 校验
        MesProTaskDO task = validateFeedbackData(createReqVO);

        // 2. 插入（自动填充 itemId）
        MesProFeedbackDO feedback = BeanUtils.toBean(createReqVO, MesProFeedbackDO.class)
                .setStatus(MesProFeedbackStatusEnum.PREPARE.getStatus())
                .setItemId(task.getItemId());
        feedbackMapper.insert(feedback);
        return feedback.getId();
    }

    @Override
    public void updateFeedback(MesProFeedbackSaveReqVO updateReqVO) {
        // 1.1 校验存在 + 草稿状态
        validateFeedbackStatusPrepare(updateReqVO.getId());
        // 1.2 校验业务数据
        MesProTaskDO task = validateFeedbackData(updateReqVO);

        // 2. 更新（自动填充 itemId）
        MesProFeedbackDO updateObj = BeanUtils.toBean(updateReqVO, MesProFeedbackDO.class)
                .setItemId(task.getItemId());
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
    public boolean approveFeedback(Long id, Long userId) {
        // 1.1.a 校验存在 + 审批中状态
        MesProFeedbackDO feedback = validateFeedbackStatusApproving(id);
        // 1.1.b 校验报工数量 > 0
        if (feedback.getFeedbackQuantity() == null
                || feedback.getFeedbackQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw exception(PRO_FEEDBACK_QUANTITY_MUST_POSITIVE);
        }
        // 1.2.a 校验任务未完成
        taskService.validateTaskNotFinished(feedback.getTaskId());
        // 1.2.b 仍有待检数量时不能执行
        if (feedback.getUncheckQuantity() != null
                && feedback.getUncheckQuantity().compareTo(BigDecimal.ZERO) > 0) {
            throw exception(PRO_FEEDBACK_UNCHECK_QUANTITY_EXISTS, feedback.getUncheckQuantity());
        }

        // 2. 物料消耗：根据工序 BOM 生成消耗记录
        MesWmItemConsumeDO itemConsume = itemConsumeService.generateItemConsume(feedback);
        if (itemConsume != null) {
            itemConsumeService.finishItemConsume(itemConsume.getId());
        }

        // 3. 查询工序的 keyFlag + checkFlag
        MesProRouteProcessDO routeProcess = routeProcessService.getRouteProcessByRouteIdAndProcessId(
                feedback.getRouteId(), feedback.getProcessId());
        boolean keyFlag = routeProcess != null && Boolean.TRUE.equals(routeProcess.getKeyFlag());
        boolean checkFlag = routeProcess != null && Boolean.TRUE.equals(routeProcess.getCheckFlag());

        // 4. 情况一：关键工序 + 需要检验 → 待检验状态（等质检完成后入库）
        if (keyFlag && checkFlag) {
            feedbackMapper.updateById(new MesProFeedbackDO().setId(id)
                    .setStatus(MesProFeedbackStatusEnum.UNCHECK.getStatus())
                    .setApproveUserId(userId));
            // TODO @芋艿：WM 产品产出（WmProductProduceService）待补充 generateProductProduce（质量状态=待检验）
            return false;
        }

        // 4.1 情况二：关键工序 + 无需检验 → 直接完成
        if (keyFlag) {
            // TODO @芋艿：WM 产品产出（WmProductProduceService）待补充 generateProductProduce + executeProductProduce
            // TODO @芋艿：updateProTaskAndWorkorder 待补充级联更新任务/工单的已生产数量
        }
        // 4.2 更新状态为已完成 + 审核人
        feedbackMapper.updateById(new MesProFeedbackDO().setId(id)
                .setStatus(MesProFeedbackStatusEnum.FINISHED.getStatus())
                .setApproveUserId(userId));
        return true; // 已完成
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
     * @return 关联的生产任务
     */
    private MesProTaskDO validateFeedbackData(MesProFeedbackSaveReqVO reqVO) {
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

        // 4. 校验任务存在且未终态（已完成/已取消），并返回任务用于冗余 itemId
        return taskService.validateTaskNotFinished(reqVO.getTaskId());
    }

}
