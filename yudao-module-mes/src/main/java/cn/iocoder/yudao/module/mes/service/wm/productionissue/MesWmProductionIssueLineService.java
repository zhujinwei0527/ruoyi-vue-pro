package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueLineDO;

import java.util.Collection;
import java.util.List;

/**
 * MES 领料出库单行 Service 接口
 */
public interface MesWmProductionIssueLineService {

    /**
     * 根据领料单ID获取行列表
     *
     * @param issueId 领料单ID
     * @return 行列表
     */
    List<MesWmProductionIssueLineDO> getIssueLineListByIssueId(Long issueId);

    /**
     * 根据领料单ID集合获取行列表
     *
     * @param issueIds 领料单ID集合
     * @return 行列表
     */
    List<MesWmProductionIssueLineDO> getIssueLineListByIssueIds(Collection<Long> issueIds);

    /**
     * 根据领料单ID删除行
     *
     * @param issueId 领料单ID
     */
    void deleteIssueLineByIssueId(Long issueId);

}
