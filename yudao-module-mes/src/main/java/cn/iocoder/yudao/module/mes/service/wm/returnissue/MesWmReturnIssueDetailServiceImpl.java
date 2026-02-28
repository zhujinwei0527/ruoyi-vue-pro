package cn.iocoder.yudao.module.mes.service.wm.returnissue;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnissue.vo.detail.MesWmReturnIssueDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnissue.MesWmReturnIssueDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.returnissue.MesWmReturnIssueDetailMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_RETURN_ISSUE_DETAIL_NOT_EXISTS;

/**
 * MES 生产退料明细 Service 实现类
 */
@Service
@Validated
public class MesWmReturnIssueDetailServiceImpl implements MesWmReturnIssueDetailService {

    @Resource
    private MesWmReturnIssueDetailMapper issueDetailMapper;

    @Resource
    @Lazy
    private MesWmReturnIssueLineService issueLineService;

    @Override
    public Long createReturnIssueDetail(MesWmReturnIssueDetailSaveReqVO createReqVO) {
        // 校验父数据存在
        issueLineService.validateReturnIssueLineExists(createReqVO.getLineId());
        // TODO @AI：校验物料存在

        // 插入
        MesWmReturnIssueDetailDO detail = BeanUtils.toBean(createReqVO, MesWmReturnIssueDetailDO.class);
        issueDetailMapper.insert(detail);
        return detail.getId();
    }

    @Override
    public void updateReturnIssueDetail(MesWmReturnIssueDetailSaveReqVO updateReqVO) {
        // 校验存在
        validateReturnIssueDetailExists(updateReqVO.getId());
        // 校验父数据存在
        issueLineService.validateReturnIssueLineExists(updateReqVO.getLineId());
        // TODO @AI：校验物料存在

        // 更新
        MesWmReturnIssueDetailDO updateObj = BeanUtils.toBean(updateReqVO, MesWmReturnIssueDetailDO.class);
        issueDetailMapper.updateById(updateObj);
    }

    @Override
    public void deleteReturnIssueDetail(Long id) {
        // 校验存在
        validateReturnIssueDetailExists(id);
        // 删除
        issueDetailMapper.deleteById(id);
    }

    @Override
    public MesWmReturnIssueDetailDO getReturnIssueDetail(Long id) {
        return issueDetailMapper.selectById(id);
    }

    @Override
    public List<MesWmReturnIssueDetailDO> getReturnIssueDetailListByLineId(Long lineId) {
        return issueDetailMapper.selectListByLineId(lineId);
    }

    @Override
    public List<MesWmReturnIssueDetailDO> getReturnIssueDetailListByIssueId(Long issueId) {
        return issueDetailMapper.selectListByIssueId(issueId);
    }

    @Override
    public void deleteReturnIssueDetailByIssueId(Long issueId) {
        issueDetailMapper.deleteByIssueId(issueId);
    }

    private void validateReturnIssueDetailExists(Long id) {
        if (issueDetailMapper.selectById(id) == null) {
            throw exception(WM_RETURN_ISSUE_DETAIL_NOT_EXISTS);
        }
    }

}
