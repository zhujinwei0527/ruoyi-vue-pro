package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue.MesWmProductionIssueDetailMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

/**
 * MES 领料出库明细 Service 实现类
 */
@Service
@Validated
public class MesWmProductionIssueDetailServiceImpl implements MesWmProductionIssueDetailService {

    @Resource
    private MesWmProductionIssueDetailMapper issueDetailMapper;

    @Override
    public List<MesWmProductionIssueDetailDO> getIssueDetailListByIssueId(Long issueId) {
        return issueDetailMapper.selectListByIssueId(issueId);
    }

    @Override
    public List<MesWmProductionIssueDetailDO> getIssueDetailListByIssueIds(Collection<Long> issueIds) {
        return issueDetailMapper.selectListByIssueIds(issueIds);
    }

    @Override
    public List<MesWmProductionIssueDetailDO> getIssueDetailListByLineId(Long lineId) {
        return issueDetailMapper.selectListByLineId(lineId);
    }

    @Override
    public void deleteIssueDetailByIssueId(Long issueId) {
        issueDetailMapper.deleteByIssueId(issueId);
    }

}
