package cn.iocoder.yudao.module.mes.service.pro.feedback;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.pro.feedback.vo.MesProFeedbackPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.feedback.vo.MesProFeedbackSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.feedback.MesProFeedbackDO;
import jakarta.validation.Valid;

/**
 * MES 生产报工 Service 接口
 *
 * @author 芋道源码
 */
public interface MesProFeedbackService {

    /**
     * 创建生产报工（草稿）
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFeedback(@Valid MesProFeedbackSaveReqVO createReqVO);

    /**
     * 修改生产报工
     *
     * @param updateReqVO 更新信息
     */
    void updateFeedback(@Valid MesProFeedbackSaveReqVO updateReqVO);

    /**
     * 删除生产报工（仅草稿）
     *
     * @param id 编号
     */
    void deleteFeedback(Long id);

    /**
     * 获得生产报工
     *
     * @param id 编号
     * @return 生产报工
     */
    MesProFeedbackDO getFeedback(Long id);

    /**
     * 获得生产报工分页
     *
     * @param pageReqVO 分页查询
     * @return 生产报工分页
     */
    PageResult<MesProFeedbackDO> getFeedbackPage(MesProFeedbackPageReqVO pageReqVO);

    /**
     * 提交报工（草稿 → 审批中）
     *
     * @param id 编号
     */
    void submitFeedback(Long id);

    /**
     * 驳回报工（审批中 → 草稿）
     *
     * @param id 编号
     */
    void rejectFeedback(Long id);

    /**
     * 审批报工（审批中 -> 已完成 或 待检验）
     *
     * @param id 编号
     * @return 审批后的状态
     */
    Integer approveFeedback(Long id);

}

