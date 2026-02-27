package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.detail.MesWmProductionIssueDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDetailDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 领料出库明细 Service 接口
 */
public interface MesWmProductionIssueDetailService {

    /**
     * 创建领料出库明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProductionIssueDetail(@Valid MesWmProductionIssueDetailSaveReqVO createReqVO);

    /**
     * 更新领料出库明细
     *
     * @param updateReqVO 更新信息
     */
    void updateProductionIssueDetail(@Valid MesWmProductionIssueDetailSaveReqVO updateReqVO);

    /**
     * 删除领料出库明细
     *
     * @param id 编号
     */
    void deleteProductionIssueDetail(Long id);

    /**
     * 获得领料出库明细
     *
     * @param id 编号
     * @return 领料出库明细
     */
    MesWmProductionIssueDetailDO getProductionIssueDetail(Long id);

    /**
     * 根据行ID获取明细列表
     *
     * @param lineId 行ID
     * @return 明细列表
     */
    List<MesWmProductionIssueDetailDO> getProductionIssueDetailListByLineId(Long lineId);

    /**
     * 根据领料单ID获取明细列表
     *
     * @param issueId 领料单ID
     * @return 明细列表
     */
    List<MesWmProductionIssueDetailDO> getProductionIssueDetailListByIssueId(Long issueId);

    /**
     * 根据领料单ID删除明细
     *
     * @param issueId 领料单ID
     */
    void deleteProductionIssueDetailByIssueId(Long issueId);

}
