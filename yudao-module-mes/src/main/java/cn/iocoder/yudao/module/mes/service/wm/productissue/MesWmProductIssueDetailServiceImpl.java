package cn.iocoder.yudao.module.mes.service.wm.productissue;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productissue.vo.detail.MesWmProductIssueDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productissue.MesWmProductIssueDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productissue.MesWmProductIssueDetailMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCT_ISSUE_DETAIL_NOT_EXISTS;

/**
 * MES 领料出库明细 Service 实现类
 */
@Service
@Validated
public class MesWmProductIssueDetailServiceImpl implements MesWmProductIssueDetailService {

    @Resource
    private MesWmProductIssueDetailMapper issueDetailMapper;

    @Resource
    @Lazy
    private MesWmProductIssueLineService issueLineService;

    @Override
    public Long createProductIssueDetail(MesWmProductIssueDetailSaveReqVO createReqVO) {
        // 校验父数据存在
        issueLineService.validateProductIssueLineExists(createReqVO.getLineId());

        // 插入
        MesWmProductIssueDetailDO detail = BeanUtils.toBean(createReqVO, MesWmProductIssueDetailDO.class);
        issueDetailMapper.insert(detail);
        return detail.getId();
    }

    @Override
    public void updateProductIssueDetail(MesWmProductIssueDetailSaveReqVO updateReqVO) {
        // 校验存在
        validateProductIssueDetailExists(updateReqVO.getId());
        // 校验父数据存在
        issueLineService.validateProductIssueLineExists(updateReqVO.getLineId());

        // 更新
        MesWmProductIssueDetailDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductIssueDetailDO.class);
        issueDetailMapper.updateById(updateObj);
    }

    @Override
    public void deleteProductIssueDetail(Long id) {
        // 校验存在
        validateProductIssueDetailExists(id);
        // 删除
        issueDetailMapper.deleteById(id);
    }

    @Override
    public MesWmProductIssueDetailDO getProductIssueDetail(Long id) {
        return issueDetailMapper.selectById(id);
    }

    @Override
    public List<MesWmProductIssueDetailDO> getProductIssueDetailListByLineId(Long lineId) {
        return issueDetailMapper.selectListByLineId(lineId);
    }

    @Override
    public List<MesWmProductIssueDetailDO> getProductIssueDetailListByIssueId(Long issueId) {
        return issueDetailMapper.selectListByIssueId(issueId);
    }

    @Override
    public void deleteProductIssueDetailByIssueId(Long issueId) {
        issueDetailMapper.deleteByIssueId(issueId);
    }

    private void validateProductIssueDetailExists(Long id) {
        if (issueDetailMapper.selectById(id) == null) {
            throw exception(WM_PRODUCT_ISSUE_DETAIL_NOT_EXISTS);
        }
    }

}
