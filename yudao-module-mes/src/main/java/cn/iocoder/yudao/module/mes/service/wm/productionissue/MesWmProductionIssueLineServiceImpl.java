package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.line.MesWmProductionIssueLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.line.MesWmProductionIssueLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue.MesWmProductionIssueLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue.MesWmProductionIssueMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCTION_ISSUE_LINE_NOT_EXISTS;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCTION_ISSUE_NOT_EXISTS;

/**
 * MES 领料出库单行 Service 实现类
 */
@Service
@Validated
public class MesWmProductionIssueLineServiceImpl implements MesWmProductionIssueLineService {

    @Resource
    private MesWmProductionIssueLineMapper issueLineMapper;
    @Resource
    private MesWmProductionIssueMapper issueMapper;

    @Override
    public Long createProductionIssueLine(MesWmProductionIssueLineSaveReqVO createReqVO) {
        // 校验父数据存在
        validateParentIssueExists(createReqVO.getIssueId());

        // 插入
        MesWmProductionIssueLineDO line = BeanUtils.toBean(createReqVO, MesWmProductionIssueLineDO.class);
        issueLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateProductionIssueLine(MesWmProductionIssueLineSaveReqVO updateReqVO) {
        // 校验存在
        validateIssueLineExists(updateReqVO.getId());
        // 校验父数据存在
        validateParentIssueExists(updateReqVO.getIssueId());

        // 更新
        MesWmProductionIssueLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductionIssueLineDO.class);
        issueLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteProductionIssueLine(Long id) {
        // 校验存在
        validateIssueLineExists(id);
        // 删除
        issueLineMapper.deleteById(id);
    }

    @Override
    public MesWmProductionIssueLineDO getProductionIssueLine(Long id) {
        return issueLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmProductionIssueLineDO> getProductionIssueLinePage(MesWmProductionIssueLinePageReqVO pageReqVO) {
        return issueLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmProductionIssueLineDO> getProductionIssueLineListByIssueId(Long issueId) {
        return issueLineMapper.selectListByIssueId(issueId);
    }

    @Override
    public void deleteProductionIssueLineByIssueId(Long issueId) {
        issueLineMapper.deleteByIssueId(issueId);
    }

    private void validateIssueLineExists(Long id) {
        if (issueLineMapper.selectById(id) == null) {
            throw exception(WM_PRODUCTION_ISSUE_LINE_NOT_EXISTS);
        }
    }

    private void validateParentIssueExists(Long issueId) {
        if (issueMapper.selectById(issueId) == null) {
            throw exception(WM_PRODUCTION_ISSUE_NOT_EXISTS);
        }
    }

}
