package cn.iocoder.yudao.module.mes.service.wm.returnissue;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnissue.vo.MesWmReturnIssuePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnissue.vo.MesWmReturnIssueSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnissue.MesWmReturnIssueDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnissue.MesWmReturnIssueDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnissue.MesWmReturnIssueLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.returnissue.MesWmReturnIssueMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmReturnIssueStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 生产退料单 Service 实现类
 */
@Service
@Validated
public class MesWmReturnIssueServiceImpl implements MesWmReturnIssueService {

    @Resource
    private MesWmReturnIssueMapper issueMapper;

    @Resource
    private MesWmReturnIssueLineService issueLineService;
    @Resource
    private MesWmReturnIssueDetailService issueDetailService;
    @Resource
    private MesMdWorkstationService workstationService;
    @Resource
    private MesProWorkOrderService workOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReturnIssue(MesWmReturnIssueSaveReqVO createReqVO) {
        // 1. 校验关联数据
        workOrderService.validateWorkOrderExists(createReqVO.getWorkOrderId());
        if (createReqVO.getWorkstationId() != null) {
            workstationService.validateWorkstationExists(createReqVO.getWorkstationId());
        }

        // 2. 插入主表
        MesWmReturnIssueDO issue = BeanUtils.toBean(createReqVO, MesWmReturnIssueDO.class);
        issue.setStatus(MesWmReturnIssueStatusEnum.PREPARE.getStatus());
        issueMapper.insert(issue);
        return issue.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReturnIssue(MesWmReturnIssueSaveReqVO updateReqVO) {
        // 1.1 校验存在 + 准备中状态
        MesWmReturnIssueDO oldIssue = validateReturnIssueExistsAndPrepare(updateReqVO.getId());
        // 1.2 校验关联数据
        workOrderService.validateWorkOrderExists(updateReqVO.getWorkOrderId());
        if (updateReqVO.getWorkstationId() != null) {
            workstationService.validateWorkstationExists(updateReqVO.getWorkstationId());
        }

        // 2. 更新主表
        MesWmReturnIssueDO updateObj = BeanUtils.toBean(updateReqVO, MesWmReturnIssueDO.class);
        issueMapper.updateById(updateObj);

        // 3. 退料类型变更时，刷新所有行的质量状态
        if (ObjUtil.notEqual(oldIssue.getType(), updateReqVO.getType())) {
            issueLineService.updateReturnIssueQualityStatusByIssueId(updateReqVO.getId(), updateReqVO.getType());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReturnIssue(Long id) {
        // 1. 校验存在 + 准备中状态
        validateReturnIssueExistsAndPrepare(id);

        // 2.1 级联删除明细
        issueDetailService.deleteReturnIssueDetailByIssueId(id);
        // 2.2 级联删除行
        issueLineService.deleteReturnIssueLineByIssueId(id);
        // 2.3 删除主表
        issueMapper.deleteById(id);
    }

    @Override
    public MesWmReturnIssueDO getReturnIssue(Long id) {
        return issueMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmReturnIssueDO> getReturnIssuePage(MesWmReturnIssuePageReqVO pageReqVO) {
        return issueMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReturnIssue(Long id) {
        // 校验存在 + 草稿状态
        validateReturnIssueExistsAndPrepare(id);
        // 校验至少有一条行
        List<MesWmReturnIssueLineDO> lines = issueLineService.getReturnIssueLineListByIssueId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_RETURN_ISSUE_NO_LINE);
        }

        // 确认（草稿 → 待检验）
        issueMapper.updateById(new MesWmReturnIssueDO()
                .setId(id).setStatus(MesWmReturnIssueStatusEnum.CONFIRMED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReturnIssue(Long id) {
        // 校验存在 + 待检验状态
        MesWmReturnIssueDO issue = validateReturnIssueExists(id);
        if (ObjUtil.notEqual(MesWmReturnIssueStatusEnum.CONFIRMED.getStatus(), issue.getStatus())) {
            throw exception(WM_RETURN_ISSUE_STATUS_INVALID);
        }
        // 校验至少有一条行
        List<MesWmReturnIssueLineDO> lines = issueLineService.getReturnIssueLineListByIssueId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_RETURN_ISSUE_NO_LINE);
        }

        // 提交（待检验 → 待上架）
        issueMapper.updateById(new MesWmReturnIssueDO()
                .setId(id).setStatus(MesWmReturnIssueStatusEnum.APPROVING.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockReturnIssue(Long id) {
        // 校验存在
        MesWmReturnIssueDO issue = validateReturnIssueExists(id);
        if (ObjUtil.notEqual(MesWmReturnIssueStatusEnum.APPROVING.getStatus(), issue.getStatus())) {
            throw exception(WM_RETURN_ISSUE_STATUS_INVALID);
        }
        // 入库上架（待上架 → 待执行退料）
        issueMapper.updateById(new MesWmReturnIssueDO()
                .setId(id).setStatus(MesWmReturnIssueStatusEnum.APPROVED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishReturnIssue(Long id) {
        // 校验存在
        MesWmReturnIssueDO issue = validateReturnIssueExists(id);
        if (ObjUtil.notEqual(MesWmReturnIssueStatusEnum.APPROVED.getStatus(), issue.getStatus())) {
            throw exception(WM_RETURN_ISSUE_STATUS_INVALID);
        }

        // 完成退料（待执行退料 → 已完成），更新库存台账
        // 遍历所有明细，更新库存台账（增加库存，与领料出库相反）
        // TODO @芋艿：【后续在弄】这里可能有点问题；缺少库存更新；后面在弄；
        List<MesWmReturnIssueDetailDO> details = issueDetailService.getReturnIssueDetailListByIssueId(id);
        for (MesWmReturnIssueDetailDO detail : details) {
            // materialStockService.increaseStock(
            //         detail.getItemId(), detail.getWarehouseId(), detail.getLocationId(), detail.getAreaId(),
            //         detail.getBatchId(), detail.getQuantity(), issue.getWorkOrderId(), null, null);
        }

        // 更新退料单状态
        issueMapper.updateById(new MesWmReturnIssueDO()
                .setId(id).setStatus(MesWmReturnIssueStatusEnum.FINISHED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReturnIssue(Long id) {
        // 校验存在
        MesWmReturnIssueDO issue = validateReturnIssueExists(id);
        // 已完成和已取消不允许取消
        if (ObjectUtils.equalsAny(issue.getStatus(),
                MesWmReturnIssueStatusEnum.FINISHED.getStatus(),
                MesWmReturnIssueStatusEnum.CANCELED.getStatus())) {
            throw exception(WM_RETURN_ISSUE_CANCEL_NOT_ALLOWED);
        }

        // 取消
        issueMapper.updateById(new MesWmReturnIssueDO()
                .setId(id).setStatus(MesWmReturnIssueStatusEnum.CANCELED.getStatus()));
    }

    @Override
    public Boolean checkReturnIssueQuantity(Long id) {
        List<MesWmReturnIssueLineDO> lines = issueLineService.getReturnIssueLineListByIssueId(id);
        for (MesWmReturnIssueLineDO line : lines) {
            List<MesWmReturnIssueDetailDO> details = issueDetailService.getReturnIssueDetailListByLineId(line.getId());
            BigDecimal totalDetailQty = CollectionUtils.getSumValue(details,
                    MesWmReturnIssueDetailDO::getQuantity, BigDecimal::add, BigDecimal.ZERO);
            if (line.getQuantity() != null && totalDetailQty.compareTo(line.getQuantity()) != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public MesWmReturnIssueDO validateReturnIssueExists(Long id) {
        MesWmReturnIssueDO issue = issueMapper.selectById(id);
        if (issue == null) {
            throw exception(WM_RETURN_ISSUE_NOT_EXISTS);
        }
        return issue;
    }

    /**
     * 校验生产退料单存在且为准备中状态
     */
    private MesWmReturnIssueDO validateReturnIssueExistsAndPrepare(Long id) {
        MesWmReturnIssueDO issue = validateReturnIssueExists(id);
        if (ObjUtil.notEqual(MesWmReturnIssueStatusEnum.PREPARE.getStatus(), issue.getStatus())) {
            throw exception(WM_RETURN_ISSUE_STATUS_INVALID);
        }
        return issue;
    }

}
