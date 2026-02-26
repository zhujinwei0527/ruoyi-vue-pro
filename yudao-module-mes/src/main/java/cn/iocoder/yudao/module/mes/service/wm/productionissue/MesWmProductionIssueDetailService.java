package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDetailDO;

import java.util.Collection;
import java.util.List;

/**
 * MES 领料出库明细 Service 接口
 */
public interface MesWmProductionIssueDetailService {

    /**
     * 根据领料单ID获取明细列表
     *
     * @param issueId 领料单ID
     * @return 明细列表
     */
    List<MesWmProductionIssueDetailDO> getIssueDetailListByIssueId(Long issueId);

    /**
     * 根据领料单ID集合获取明细列表
     *
     * @param issueIds 领料单ID集合
     * @return 明细列表
     */
    List<MesWmProductionIssueDetailDO> getIssueDetailListByIssueIds(Collection<Long> issueIds);

    /**
     * 根据行ID获取明细列表
     *
     * @param lineId 行ID
     * @return 明细列表
     */
    List<MesWmProductionIssueDetailDO> getIssueDetailListByLineId(Long lineId);

    /**
     * 根据领料单ID删除明细
     *
     * @param issueId 领料单ID
     */
    void deleteIssueDetailByIssueId(Long issueId);

}
