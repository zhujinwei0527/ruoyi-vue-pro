package cn.iocoder.yudao.module.mes.service.wm.outsourceissue;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.outsourceissue.vo.MesWmOutsourceIssuePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.outsourceissue.vo.MesWmOutsourceIssueSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourceissue.MesWmOutsourceIssueDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourceissue.MesWmOutsourceIssueDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourceissue.MesWmOutsourceIssueLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.outsourceissue.MesWmOutsourceIssueMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmOutsourceIssueStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 外协发料单 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class MesWmOutsourceIssueServiceImpl implements MesWmOutsourceIssueService {

    @Resource
    private MesWmOutsourceIssueMapper outsourceIssueMapper;

    @Resource
    private MesWmOutsourceIssueLineService outsourceIssueLineService;

    @Resource
    private MesWmOutsourceIssueDetailService outsourceIssueDetailService;

    @Override
    public Long createOutsourceIssue(MesWmOutsourceIssueSaveReqVO createReqVO) {
        // 校验编码唯一
        validateCodeUnique(null, createReqVO.getCode());
        // TODO @AI：vendorId 非空时，校验供应商存在；
        // TODO @AI：workorderId 无需判断空，需要校验工单存在；

        // 插入
        MesWmOutsourceIssueDO issue = BeanUtils.toBean(createReqVO, MesWmOutsourceIssueDO.class);
        issue.setStatus(MesWmOutsourceIssueStatusEnum.PREPARE.getStatus());
        outsourceIssueMapper.insert(issue);
        return issue.getId();
    }

    @Override
    public void updateOutsourceIssue(MesWmOutsourceIssueSaveReqVO updateReqVO) {
        // 校验存在 + 草稿状态
        validateOutsourceIssueExistsAndDraft(updateReqVO.getId());
        // 校验编码唯一
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // TODO @AI：vendorId 非空时，校验供应商存在；
        // TODO @AI：workorderId 无需判断空，需要校验工单存在；

        // 更新
        MesWmOutsourceIssueDO updateObj = BeanUtils.toBean(updateReqVO, MesWmOutsourceIssueDO.class);
        outsourceIssueMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOutsourceIssue(Long id) {
        // 校验存在 + 草稿状态
        validateOutsourceIssueExistsAndDraft(id);

        // 级联删除行和明细
        outsourceIssueLineService.deleteOutsourceIssueLineByIssueId(id);
        outsourceIssueDetailService.deleteOutsourceIssueDetailByIssueId(id);
        // 删除主表
        outsourceIssueMapper.deleteById(id);
    }

    @Override
    public MesWmOutsourceIssueDO getOutsourceIssue(Long id) {
        return outsourceIssueMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmOutsourceIssueDO> getOutsourceIssuePage(MesWmOutsourceIssuePageReqVO pageReqVO) {
        return outsourceIssueMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOutsourceIssue(Long id) {
        // 1.1 校验存在 + 草稿状态
        validateOutsourceIssueExistsAndDraft(id);
        // 1.2 检查是否有发料行
        List<MesWmOutsourceIssueLineDO> lines = outsourceIssueLineService.getOutsourceIssueLineListByIssueId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_OUTSOURCE_ISSUE_NO_LINE);
        }

        // 2. 更新单据状态为待拣货
        outsourceIssueMapper.updateById(new MesWmOutsourceIssueDO()
                .setId(id).setStatus(MesWmOutsourceIssueStatusEnum.APPROVING.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockOutsourceIssue(Long id) {
        // 1.1 校验存在 + 待拣货状态
        MesWmOutsourceIssueDO issue = validateOutsourceIssueExists(id);
        if (ObjUtil.notEqual(MesWmOutsourceIssueStatusEnum.APPROVING.getStatus(), issue.getStatus())) {
            throw exception(WM_OUTSOURCE_ISSUE_STATUS_NOT_APPROVING);
        }
        // 1.2 校验数量一致性（行数量 = 明细数量之和）
        List<MesWmOutsourceIssueLineDO> lines = outsourceIssueLineService.getOutsourceIssueLineListByIssueId(id);
        if (CollUtil.isNotEmpty(lines)) {
            // 批量查询所有明细
            List<MesWmOutsourceIssueDetailDO> allDetails = outsourceIssueDetailService.getOutsourceIssueDetailListByIssueId(id);
            Map<Long, List<MesWmOutsourceIssueDetailDO>> detailMap = CollectionUtils.convertMultiMap(
                    allDetails, MesWmOutsourceIssueDetailDO::getLineId);
            // 检查每行的明细数量
            for (MesWmOutsourceIssueLineDO line : lines) {
                List<MesWmOutsourceIssueDetailDO> details = detailMap.getOrDefault(line.getId(), List.of());
                BigDecimal totalDetailQuantity = CollectionUtils.getSumValue(details,
                        MesWmOutsourceIssueDetailDO::getQuantity, BigDecimal::add, BigDecimal.ZERO);
                // 对比行数量与明细总数量，不满足直接抛出
                if (line.getQuantity().compareTo(totalDetailQuantity) != 0) {
                    throw exception(WM_OUTSOURCE_ISSUE_QUANTITY_MISMATCH);
                }
            }
        }

        // 2. 更新单据状态为待执行出库
        outsourceIssueMapper.updateById(new MesWmOutsourceIssueDO()
                .setId(id).setStatus(MesWmOutsourceIssueStatusEnum.APPROVED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishOutsourceIssue(Long id) {
        // 1. 校验存在 + 待执行出库状态
        MesWmOutsourceIssueDO issue = validateOutsourceIssueExists(id);
        if (ObjUtil.notEqual(MesWmOutsourceIssueStatusEnum.APPROVED.getStatus(), issue.getStatus())) {
            throw exception(WM_OUTSOURCE_ISSUE_STATUS_NOT_APPROVED);
        }

        // 2. 扣减库存（遍历明细表，逐条扣减）
        List<MesWmOutsourceIssueDetailDO> details = outsourceIssueDetailService.getOutsourceIssueDetailListByIssueId(id);
        for (MesWmOutsourceIssueDetailDO detail : details) {
            // TODO: 调用库存服务扣减库存
            // materialStockService.decreaseStock(detail.getMaterialStockId(), detail.getQuantity());
            log.info("[finishOutsourceIssue][发料单({}) 扣减库存 - 库存ID: {}, 数量: {}]",
                    id, detail.getMaterialStockId(), detail.getQuantity());
        }

        // 3. 更新单据状态为已完成
        outsourceIssueMapper.updateById(new MesWmOutsourceIssueDO()
                .setId(id).setStatus(MesWmOutsourceIssueStatusEnum.FINISHED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOutsourceIssue(Long id) {
        // 校验存在
        MesWmOutsourceIssueDO issue = validateOutsourceIssueExists(id);
        // 已完成和已取消不允许取消
        if (ObjectUtils.equalsAny(issue.getStatus(),
                MesWmOutsourceIssueStatusEnum.FINISHED.getStatus(),
                MesWmOutsourceIssueStatusEnum.CANCELLED.getStatus())) {
            throw exception(WM_OUTSOURCE_ISSUE_CANCEL_NOT_ALLOWED);
        }

        // 取消
        outsourceIssueMapper.updateById(new MesWmOutsourceIssueDO()
                .setId(id).setStatus(MesWmOutsourceIssueStatusEnum.CANCELLED.getStatus()));
    }

    /**
     * 校验外协发料单存在
     */
    private MesWmOutsourceIssueDO validateOutsourceIssueExists(Long id) {
        MesWmOutsourceIssueDO issue = outsourceIssueMapper.selectById(id);
        if (issue == null) {
            throw exception(WM_OUTSOURCE_ISSUE_NOT_EXISTS);
        }
        return issue;
    }

    /**
     * 校验外协发料单存在且为草稿状态
     */
    private MesWmOutsourceIssueDO validateOutsourceIssueExistsAndDraft(Long id) {
        MesWmOutsourceIssueDO issue = validateOutsourceIssueExists(id);
        if (ObjUtil.notEqual(MesWmOutsourceIssueStatusEnum.PREPARE.getStatus(), issue.getStatus())) {
            throw exception(WM_OUTSOURCE_ISSUE_STATUS_NOT_PREPARE);
        }
        return issue;
    }

    /**
     * 校验编码唯一性
     */
    private void validateCodeUnique(Long id, String code) {
        MesWmOutsourceIssueDO issue = outsourceIssueMapper.selectByCode(code);
        if (issue == null) {
            return;
        }
        if (ObjUtil.notEqual(id, issue.getId())) {
            throw exception(WM_OUTSOURCE_ISSUE_CODE_DUPLICATE);
        }
    }

}
