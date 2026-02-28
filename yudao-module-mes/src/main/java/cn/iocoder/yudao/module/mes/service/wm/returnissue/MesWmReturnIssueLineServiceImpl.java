package cn.iocoder.yudao.module.mes.service.wm.returnissue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnissue.vo.line.MesWmReturnIssueLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnissue.vo.line.MesWmReturnIssueLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnissue.MesWmReturnIssueDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnissue.MesWmReturnIssueLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.returnissue.MesWmReturnIssueLineMapper;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_RETURN_ISSUE_LINE_NOT_EXISTS;

/**
 * MES 生产退料单行 Service 实现类
 */
@Service
@Validated
public class MesWmReturnIssueLineServiceImpl implements MesWmReturnIssueLineService {

    @Resource
    private MesWmReturnIssueLineMapper issueLineMapper;

    @Resource
    @Lazy
    private MesWmReturnIssueService issueService;
    @Resource
    private MesMdItemService itemService;

    @Override
    public Long createReturnIssueLine(MesWmReturnIssueLineSaveReqVO createReqVO) {
        // 校验父数据存在
        MesWmReturnIssueDO issue = issueService.validateReturnIssueExists(createReqVO.getIssueId());
        // 校验物料存在
        itemService.validateItemExists(createReqVO.getItemId());

        // 插入
        MesWmReturnIssueLineDO line = BeanUtils.toBean(createReqVO, MesWmReturnIssueLineDO.class);
        // 质量状态自动赋值
        // TODO @AI：这里需要在 review 下 @芋艿；
        if (Boolean.TRUE.equals(createReqVO.getQcFlag())) {
            line.setQualityStatus(0); // 待检
        } else if ("RMR".equals(issue.getReturnType())) {
            line.setQualityStatus(1); // 合格（余料退回直接合格）
        } else {
            line.setQualityStatus(2); // 不合格
        }
        issueLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateReturnIssueLine(MesWmReturnIssueLineSaveReqVO updateReqVO) {
        // 校验存在
        validateReturnIssueLineExists(updateReqVO.getId());
        // 校验父数据存在
        issueService.validateReturnIssueExists(updateReqVO.getIssueId());
        // 校验物料存在
        itemService.validateItemExists(updateReqVO.getItemId());

        // 更新
        MesWmReturnIssueLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmReturnIssueLineDO.class);
        issueLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteReturnIssueLine(Long id) {
        // 校验存在
        validateReturnIssueLineExists(id);
        // 删除
        issueLineMapper.deleteById(id);
    }

    @Override
    public MesWmReturnIssueLineDO getReturnIssueLine(Long id) {
        return issueLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmReturnIssueLineDO> getReturnIssueLinePage(MesWmReturnIssueLinePageReqVO pageReqVO) {
        return issueLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmReturnIssueLineDO> getReturnIssueLineListByIssueId(Long issueId) {
        return issueLineMapper.selectListByIssueId(issueId);
    }

    @Override
    public void deleteReturnIssueLineByIssueId(Long issueId) {
        issueLineMapper.deleteByIssueId(issueId);
    }

    @Override
    public MesWmReturnIssueLineDO validateReturnIssueLineExists(Long id) {
        MesWmReturnIssueLineDO line = issueLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_RETURN_ISSUE_LINE_NOT_EXISTS);
        }
        return line;
    }

}
