package cn.iocoder.yudao.module.mes.service.wm.miscissue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.miscissue.vo.line.MesWmMiscIssueLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.miscissue.vo.line.MesWmMiscIssueLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.miscissue.MesWmMiscIssueLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.miscissue.MesWmMiscIssueLineMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 杂项出库单行 Service 实现类
 */
@Service
@Validated
public class MesWmMiscIssueLineServiceImpl implements MesWmMiscIssueLineService {

    @Resource
    private MesWmMiscIssueLineMapper miscIssueLineMapper;

    @Resource
    @Lazy
    private MesWmMiscIssueService miscIssueService;

    @Override
    public Long createMiscIssueLine(MesWmMiscIssueLineSaveReqVO createReqVO) {
        // 校验父单据存在且为可编辑状态
        miscIssueService.validateMiscIssueEditable(createReqVO.getIssueId());
        // TODO AI：item 存在；
        // TODO @AI：校验 areaid 等字段存在；areaservice 有统一方法；

        // 新增
        MesWmMiscIssueLineDO line = BeanUtils.toBean(createReqVO, MesWmMiscIssueLineDO.class);
        miscIssueLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateMiscIssueLine(MesWmMiscIssueLineSaveReqVO updateReqVO) {
        // 校验存在
        MesWmMiscIssueLineDO line = validateMiscIssueLineExists(updateReqVO.getId());
        // 校验父单据存在且为可编辑状态
        miscIssueService.validateMiscIssueEditable(line.getIssueId());
        // TODO AI：item 存在；
        // TODO @AI：校验 areaid 等字段存在；areaservice 有统一方法；

        // 更新
        MesWmMiscIssueLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmMiscIssueLineDO.class);
        miscIssueLineMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMiscIssueLine(Long id) {
        // 校验存在
        validateMiscIssueLineExists(id);

        // 删除
        miscIssueLineMapper.deleteById(id);
    }

    @Override
    public MesWmMiscIssueLineDO getMiscIssueLine(Long id) {
        return miscIssueLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmMiscIssueLineDO> getMiscIssueLinePage(MesWmMiscIssueLinePageReqVO pageReqVO) {
        return miscIssueLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmMiscIssueLineDO> getMiscIssueLineListByIssueId(Long issueId) {
        return miscIssueLineMapper.selectListByIssueId(issueId);
    }

    @Override
    public void deleteMiscIssueLineByIssueId(Long issueId) {
        miscIssueLineMapper.deleteByIssueId(issueId);
    }

    private MesWmMiscIssueLineDO validateMiscIssueLineExists(Long id) {
        MesWmMiscIssueLineDO line = miscIssueLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_MISC_ISSUE_LINE_NOT_EXISTS);
        }
        return line;
    }

}
