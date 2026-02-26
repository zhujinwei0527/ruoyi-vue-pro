package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.detail.MesWmProductionIssueDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue.MesWmProductionIssueDetailMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue.MesWmProductionIssueLineMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCTION_ISSUE_DETAIL_NOT_EXISTS;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCTION_ISSUE_LINE_NOT_EXISTS;

/**
 * MES 领料出库明细 Service 实现类
 */
@Service
@Validated
public class MesWmProductionIssueDetailServiceImpl implements MesWmProductionIssueDetailService {

    @Resource
    private MesWmProductionIssueDetailMapper issueDetailMapper;
    @Resource
    private MesWmProductionIssueLineMapper issueLineMapper;

    @Override
    public Long createProductionIssueDetail(MesWmProductionIssueDetailSaveReqVO createReqVO) {
        // 校验父数据存在
        validateParentIssueLineExists(createReqVO.getLineId());

        // 插入
        MesWmProductionIssueDetailDO detail = BeanUtils.toBean(createReqVO, MesWmProductionIssueDetailDO.class);
        issueDetailMapper.insert(detail);
        return detail.getId();
    }

    @Override
    public void updateProductionIssueDetail(MesWmProductionIssueDetailSaveReqVO updateReqVO) {
        // 校验存在
        validateIssueDetailExists(updateReqVO.getId());
        // 校验父数据存在
        validateParentIssueLineExists(updateReqVO.getLineId());

        // 更新
        MesWmProductionIssueDetailDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductionIssueDetailDO.class);
        issueDetailMapper.updateById(updateObj);
    }

    @Override
    public void deleteProductionIssueDetail(Long id) {
        // 校验存在
        validateIssueDetailExists(id);
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
    public void deleteProductionIssueDetailByIssueId(Long issueId) {
        issueDetailMapper.deleteByIssueId(issueId);
    }

    private void validateIssueDetailExists(Long id) {
        if (issueDetailMapper.selectById(id) == null) {
            throw exception(WM_PRODUCTION_ISSUE_DETAIL_NOT_EXISTS);
        }
    }

    private void validateParentIssueLineExists(Long lineId) {
        if (issueLineMapper.selectById(lineId) == null) {
            throw exception(WM_PRODUCTION_ISSUE_LINE_NOT_EXISTS);
        }
    }

}
