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
     * 完成领料出库单（草稿 → 已完成，执行出库）
     *
     * @param id 编号
     */
    void finishProductionIssue(Long id);

}
