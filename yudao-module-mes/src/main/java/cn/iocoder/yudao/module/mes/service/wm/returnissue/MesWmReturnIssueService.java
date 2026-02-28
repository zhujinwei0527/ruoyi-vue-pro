package cn.iocoder.yudao.module.mes.service.wm.returnissue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnissue.vo.MesWmReturnIssuePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnissue.vo.MesWmReturnIssueSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnissue.MesWmReturnIssueDO;
import jakarta.validation.Valid;

/**
 * MES 生产退料单 Service 接口
 */
public interface MesWmReturnIssueService {

    /**
     * 创建生产退料单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createReturnIssue(@Valid MesWmReturnIssueSaveReqVO createReqVO);

    /**
     * 修改生产退料单
     *
     * @param updateReqVO 修改信息
     */
    void updateReturnIssue(@Valid MesWmReturnIssueSaveReqVO updateReqVO);

    /**
     * 删除生产退料单
     *
     * @param id 编号
     */
    void deleteReturnIssue(Long id);

    /**
     * 获得生产退料单
     *
     * @param id 编号
     * @return 生产退料单
     */
    MesWmReturnIssueDO getReturnIssue(Long id);

    /**
     * 获得生产退料单分页
     *
     * @param pageReqVO 分页参数
     * @return 生产退料单分页
     */
    PageResult<MesWmReturnIssueDO> getReturnIssuePage(MesWmReturnIssuePageReqVO pageReqVO);

    /**
     * 校验生产退料单是否存在
     *
     * @param id 编号
     * @return 生产退料单
     */
    MesWmReturnIssueDO validateReturnIssueExists(Long id);

    /**
     * 确认生产退料单（草稿 → 待检验）
     *
     * @param id 编号
     */
    void confirmReturnIssue(Long id);

    /**
     * 提交生产退料单（待检验 → 待上架）
     *
     * @param id 编号
     */
    void submitReturnIssue(Long id);

    /**
     * 入库上架（待上架 → 待执行退料）
     *
     * @param id 编号
     */
    void stockReturnIssue(Long id);

    /**
     * 完成退料（待执行退料 → 已完成），更新库存台账
     *
     * @param id 编号
     */
    void finishReturnIssue(Long id);

    /**
     * 取消生产退料单（任意非已完成/已取消状态 → 已取消）
     *
     * @param id 编号
     */
    void cancelReturnIssue(Long id);

    /**
     * 校验生产退料单的数量：每行明细数量之和是否等于行退料数量
     *
     * @param id 编号
     * @return 是否全部一致
     */
    Boolean checkReturnIssueQuantity(Long id);

}
