package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssuePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssueSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssueLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue.MesWmProductionIssueMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue.MesWmProductionIssueLineMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmProductionIssueStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * MES 领料出库单 Service 实现类
 */
@Service
@Validated
public class MesWmProductionIssueServiceImpl implements MesWmProductionIssueService {

    @Resource
    private MesWmProductionIssueMapper issueHeaderMapper;

    @Resource
    private MesWmProductionIssueLineMapper issueLineMapper;

    @Resource
    private MesWmProductionIssueLineService issueLineService;

    @Resource
    private MesWmProductionIssueDetailService issueDetailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createIssue(MesWmProductionIssueSaveReqVO createReqVO) {
        // TODO @AI：领料出库关联数据校验待完善（仓库、物料、工单、工位等基础数据校验）

        // 1. 插入主表
        MesWmProductionIssueDO issueHeader = BeanUtils.toBean(createReqVO, MesWmProductionIssueDO.class);
        issueHeader.setStatus(MesWmProductionIssueStatusEnum.PREPARE.getStatus());
        issueHeaderMapper.insert(issueHeader);

        // 2. 插入行表
        if (CollUtil.isNotEmpty(createReqVO.getLines())) {
            for (MesWmProductionIssueLineSaveReqVO lineVO : createReqVO.getLines()) {
                MesWmProductionIssueLineDO line = BeanUtils.toBean(lineVO, MesWmProductionIssueLineDO.class);
                line.setIssueId(issueHeader.getId());
                issueLineMapper.insert(line);
            }
        }

        return issueHeader.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIssue(MesWmProductionIssueSaveReqVO updateReqVO) {
        // 1. 校验存在 + 准备中状态
        validateIssueExistsAndPrepare(updateReqVO.getId());
        // TODO @AI：领料出库关联数据校验待完善（仓库、物料、工单、工位等基础数据校验）

        // 2. 更新主表
        MesWmProductionIssueDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductionIssueDO.class);
        issueHeaderMapper.updateById(updateObj);

        // 3. 更新行表：先删除旧数据，再插入新数据
        issueLineMapper.deleteByIssueId(updateReqVO.getId());
        if (CollUtil.isNotEmpty(updateReqVO.getLines())) {
            for (MesWmProductionIssueLineSaveReqVO lineVO : updateReqVO.getLines()) {
                MesWmProductionIssueLineDO line = BeanUtils.toBean(lineVO, MesWmProductionIssueLineDO.class);
                line.setIssueId(updateReqVO.getId());
                issueLineMapper.insert(line);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIssue(Long id) {
        // 1. 校验存在 + 准备中状态
        validateIssueExistsAndPrepare(id);

        // 2.1 级联删除明细
        issueDetailService.deleteIssueDetailByIssueId(id);
        // 2.2 级联删除行
        issueLineService.deleteIssueLineByIssueId(id);
        // 2.3 删除主表
        issueHeaderMapper.deleteById(id);
    }

    @Override
    public MesWmProductionIssueDO getIssue(Long id) {
        return issueHeaderMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmProductionIssueDO> getIssuePage(MesWmProductionIssuePageReqVO pageReqVO) {
        return issueHeaderMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveIssue(Long id) {
        // 1.1 校验存在
        MesWmProductionIssueDO issueHeader = validateIssueExists(id);
        // 1.2 校验状态：只有准备中才允许审批
        if (ObjUtil.notEqual(MesWmProductionIssueStatusEnum.PREPARE.getStatus(), issueHeader.getStatus())) {
//            throw exception(WM_ISSUE_STATUS_INVALID);
            throw new IllegalArgumentException(); // TODO @芋艿：晚点优化下；
        }

        // 2. 准备中 → 已审批
        issueHeaderMapper.updateById(new MesWmProductionIssueDO()
                .setId(id).setStatus(MesWmProductionIssueStatusEnum.APPROVED.getStatus()));

        // TODO @AI：扣减库存逻辑待完善（需要调用库存服务，根据 detail 表扣减库存）
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unapproveIssue(Long id) {
        // 1.1 校验存在
        MesWmProductionIssueDO issueHeader = validateIssueExists(id);
        // 1.2 校验状态：只有已审批才允许反审批
        if (ObjUtil.notEqual(MesWmProductionIssueStatusEnum.APPROVED.getStatus(), issueHeader.getStatus())) {
//            throw exception(WM_ISSUE_STATUS_INVALID);
            throw new IllegalArgumentException(); // TODO @芋艿：晚点优化下；
        }

        // 2. 已审批 → 准备中
        issueHeaderMapper.updateById(new MesWmProductionIssueDO()
                .setId(id).setStatus(MesWmProductionIssueStatusEnum.PREPARE.getStatus()));

        // TODO @AI：恢复库存逻辑待完善（需要调用库存服务，根据 detail 表恢复库存）
    }

    @Override
    public void finishIssue(Long id) {
        // 1.1 校验存在
        MesWmProductionIssueDO issueHeader = validateIssueExists(id);
        // 1.2 校验状态：只有已审批才允许完成
        if (ObjUtil.notEqual(MesWmProductionIssueStatusEnum.APPROVED.getStatus(), issueHeader.getStatus())) {
//            throw exception(WM_ISSUE_STATUS_INVALID);
            throw new IllegalArgumentException(); // TODO @芋艿：晚点优化下；
        }

        // 2. 已审批 → 已完成
        issueHeaderMapper.updateById(new MesWmProductionIssueDO()
                .setId(id).setStatus(MesWmProductionIssueStatusEnum.FINISHED.getStatus()));
    }

    @Override
    public List<MesWmProductionIssueDO> getIssueList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return issueHeaderMapper.selectByIds(ids);
    }

    private MesWmProductionIssueDO validateIssueExists(Long id) {
        MesWmProductionIssueDO issueHeader = issueHeaderMapper.selectById(id);
        if (issueHeader == null) {
//            throw exception(WM_ISSUE_NOT_EXISTS);
            throw new IllegalArgumentException(); // TODO @芋艿：晚点优化下；
        }
        return issueHeader;
    }

    /**
     * 校验领料出库单存在且为准备中状态
     */
    private MesWmProductionIssueDO validateIssueExistsAndPrepare(Long id) {
        MesWmProductionIssueDO issueHeader = validateIssueExists(id);
        if (ObjUtil.notEqual(MesWmProductionIssueStatusEnum.PREPARE.getStatus(), issueHeader.getStatus())) {
//            throw exception(WM_ISSUE_STATUS_INVALID);
            throw new IllegalArgumentException(); // TODO @芋艿：晚点优化下；
        }
        return issueHeader;
    }

}
