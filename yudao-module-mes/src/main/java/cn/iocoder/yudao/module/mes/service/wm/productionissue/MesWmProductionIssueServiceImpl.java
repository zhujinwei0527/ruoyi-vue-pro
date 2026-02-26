package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssuePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssueSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue.MesWmProductionIssueMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmProductionIssueStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 领料出库单 Service 实现类
 */
@Service
@Validated
public class MesWmProductionIssueServiceImpl implements MesWmProductionIssueService {

    @Resource
    private MesWmProductionIssueMapper issueMapper;

    @Resource
    private MesWmProductionIssueLineService issueLineService;
    @Resource
    private MesWmProductionIssueDetailService issueDetailService;
    @Resource
    private MesMdWorkstationService workstationService;
    @Resource
    private MesProWorkOrderService workOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProductionIssue(MesWmProductionIssueSaveReqVO createReqVO) {
        // 1. 校验关联数据
        workstationService.validateWorkstationExists(createReqVO.getWorkstationId());
        if (createReqVO.getWorkorderId() != null) {
            workOrderService.validateWorkOrderExists(createReqVO.getWorkorderId());
        }

        // 2. 插入主表
        MesWmProductionIssueDO issue = BeanUtils.toBean(createReqVO, MesWmProductionIssueDO.class);
        issue.setStatus(MesWmProductionIssueStatusEnum.PREPARE.getStatus());
        issueMapper.insert(issue);
        return issue.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductionIssue(MesWmProductionIssueSaveReqVO updateReqVO) {
        // 1.1 校验存在 + 准备中状态
        validateIssueExistsAndPrepare(updateReqVO.getId());
        // 1.2 校验关联数据
        workstationService.validateWorkstationExists(updateReqVO.getWorkstationId());
        if (updateReqVO.getWorkorderId() != null) {
            workOrderService.validateWorkOrderExists(updateReqVO.getWorkorderId());
        }

        // 2. 更新主表
        MesWmProductionIssueDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductionIssueDO.class);
        issueMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductionIssue(Long id) {
        // 1. 校验存在 + 准备中状态
        validateIssueExistsAndPrepare(id);

        // 2.1 级联删除明细
        issueDetailService.deleteProductionIssueDetailByIssueId(id);
        // 2.2 级联删除行
        issueLineService.deleteProductionIssueLineByIssueId(id);
        // 2.3 删除主表
        issueMapper.deleteById(id);
    }

    @Override
    public MesWmProductionIssueDO getProductionIssue(Long id) {
        return issueMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmProductionIssueDO> getProductionIssuePage(MesWmProductionIssuePageReqVO pageReqVO) {
        return issueMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishProductionIssue(Long id) {
        // 1.1 校验存在
        MesWmProductionIssueDO issue = validateIssueExists(id);
        // 1.2 校验状态：只有草稿才允许完成
        if (ObjUtil.notEqual(MesWmProductionIssueStatusEnum.PREPARE.getStatus(), issue.getStatus())) {
            throw exception(WM_PRODUCTION_ISSUE_STATUS_INVALID);
        }

        // 2. 草稿 → 已完成
        // TODO @芋艿：库存扣减逻辑待完善（需要确定仓库ID的来源，可能需要在 line 或 detail 表中添加 warehouseId 字段）
        issueMapper.updateById(new MesWmProductionIssueDO()
                .setId(id).setStatus(MesWmProductionIssueStatusEnum.FINISHED.getStatus()));
    }

    private MesWmProductionIssueDO validateIssueExists(Long id) {
        MesWmProductionIssueDO issue = issueMapper.selectById(id);
        if (issue == null) {
            throw exception(WM_PRODUCTION_ISSUE_NOT_EXISTS);
        }
        return issue;
    }

    /**
     * 校验领料出库单存在且为准备中状态
     */
    private MesWmProductionIssueDO validateIssueExistsAndPrepare(Long id) {
        MesWmProductionIssueDO issue = validateIssueExists(id);
        if (ObjUtil.notEqual(MesWmProductionIssueStatusEnum.PREPARE.getStatus(), issue.getStatus())) {
            throw exception(WM_PRODUCTION_ISSUE_STATUS_INVALID);
        }
        return issue;
    }

}
