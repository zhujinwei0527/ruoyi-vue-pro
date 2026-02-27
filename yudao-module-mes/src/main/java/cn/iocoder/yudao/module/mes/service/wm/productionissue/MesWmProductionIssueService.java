package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssuePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssueSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDO;
import jakarta.validation.Valid;

/**
 * MES 领料出库单 Service 接口
 */
public interface MesWmProductionIssueService {

    /**
     * 创建领料出库单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProductionIssue(@Valid MesWmProductionIssueSaveReqVO createReqVO);

    /**
     * 修改领料出库单
     *
     * @param updateReqVO 修改信息
     */
    void updateProductionIssue(@Valid MesWmProductionIssueSaveReqVO updateReqVO);

    /**
     * 删除领料出库单
     *
     * @param id 编号
     */
    void deleteProductionIssue(Long id);

    /**
     * 获得领料出库单
     *
     * @param id 编号
     * @return 领料出库单
     */
    MesWmProductionIssueDO getProductionIssue(Long id);

    /**
     * 获得领料出库单分页
     *
     * @param pageReqVO 分页参数
     * @return 领料出库单分页
     */
    PageResult<MesWmProductionIssueDO> getProductionIssuePage(MesWmProductionIssuePageReqVO pageReqVO);

    /**
     * 校验领料出库单是否存在
     *
     * @param id 编号
     * @return 领料出库单
     */
    MesWmProductionIssueDO validateProductionIssueExists(Long id);

    /**
     * 提交领料出库单（草稿 → 待拣货）
     *
     * @param id 编号
     */
    void submitProductionIssue(Long id);

    /**
     * 执行拣货（待拣货 → 待执行领出）
     *
     * @param id 编号
     */
    void stockProductionIssue(Long id);

    /**
     * 执行领出/出库（待执行领出 → 已完成），更新库存台账
     *
     * @param id 编号
     */
    void finishProductionIssue(Long id);

    /**
     * 取消领料出库单（任意非已完成/已取消状态 → 已取消）
     *
     * @param id 编号
     */
    void cancelProductionIssue(Long id);

    /**
     * 校验领料出库单的数量：每行明细数量之和是否等于行领料数量
     *
     * @param id 编号
     * @return 是否全部一致
     */
    Boolean checkProductionIssueQuantity(Long id);

}
