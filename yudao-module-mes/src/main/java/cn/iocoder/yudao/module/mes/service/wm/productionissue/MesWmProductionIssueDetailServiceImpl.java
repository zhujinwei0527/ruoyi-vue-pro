package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.detail.MesWmProductionIssueDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue.MesWmProductionIssueDetailMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCTION_ISSUE_DETAIL_NOT_EXISTS;

/**
 * MES 领料出库明细 Service 实现类
 */
@Service
@Validated
public class MesWmProductionIssueDetailServiceImpl implements MesWmProductionIssueDetailService {

    @Resource
    private MesWmProductionIssueDetailMapper issueDetailMapper;

    @Resource
    @Lazy
    private MesWmProductionIssueLineService issueLineService;

    @Override
    public Long createProductionIssueDetail(MesWmProductionIssueDetailSaveReqVO createReqVO) {
        // 校验父数据存在
        issueLineService.validateProductionIssueLineExists(createReqVO.getLineId());

        // 插入
        MesWmProductionIssueDetailDO detail = BeanUtils.toBean(createReqVO, MesWmProductionIssueDetailDO.class);
        issueDetailMapper.insert(detail);
        return detail.getId();
    }

    @Override
    public void updateProductionIssueDetail(MesWmProductionIssueDetailSaveReqVO updateReqVO) {
        // 校验存在
        validateProductionIssueDetailExists(updateReqVO.getId());
        // 校验父数据存在
        issueLineService.validateProductionIssueLineExists(updateReqVO.getLineId());

        // 更新
        MesWmProductionIssueDetailDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductionIssueDetailDO.class);
        issueDetailMapper.updateById(updateObj);
    }

    @Override
    public void deleteProductionIssueDetail(Long id) {
        // 校验存在
        validateProductionIssueDetailExists(id);
        // 删除
        issueDetailMapper.deleteById(id);
    }

    @Override
    public MesWmProductionIssueDetailDO getProductionIssueDetail(Long id) {
        return issueDetailMapper.selectById(id);
    }

    @Override
    public List<MesWmProductionIssueDetailDO> getProductionIssueDetailListByLineId(Long lineId) {
        return issueDetailMapper.selectListByLineId(lineId);
    }

    @Override
    public List<MesWmProductionIssueDetailDO> getProductionIssueDetailListByIssueId(Long issueId) {
        return issueDetailMapper.selectListByIssueId(issueId);
    }

    @Override
    public void deleteProductionIssueDetailByIssueId(Long issueId) {
        issueDetailMapper.deleteByIssueId(issueId);
    }

    private void validateProductionIssueDetailExists(Long id) {
        if (issueDetailMapper.selectById(id) == null) {
            throw exception(WM_PRODUCTION_ISSUE_DETAIL_NOT_EXISTS);
        }
    }

}
