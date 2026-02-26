package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue.MesWmProductionIssueLineMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

/**
 * MES 领料出库单行 Service 实现类
 */
@Service
@Validated
public class MesWmProductionIssueLineServiceImpl implements MesWmProductionIssueLineService {

    @Resource
    private MesWmProductionIssueLineMapper issueLineMapper;

    @Override
    public List<MesWmProductionIssueLineDO> getIssueLineListByIssueId(Long issueId) {
        return issueLineMapper.selectListByIssueId(issueId);
    }

    @Override
    public List<MesWmProductionIssueLineDO> getIssueLineListByIssueIds(Collection<Long> issueIds) {
        return issueLineMapper.selectListByIssueIds(issueIds);
    }

    @Override
    public void deleteIssueLineByIssueId(Long issueId) {
        issueLineMapper.deleteByIssueId(issueId);
    }

}
