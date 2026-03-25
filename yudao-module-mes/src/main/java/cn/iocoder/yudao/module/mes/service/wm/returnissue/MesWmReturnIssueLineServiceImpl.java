package cn.iocoder.yudao.module.mes.service.wm.returnissue;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnissue.vo.line.MesWmReturnIssueLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnissue.vo.line.MesWmReturnIssueLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnissue.MesWmReturnIssueDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnissue.MesWmReturnIssueLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.returnissue.MesWmReturnIssueLineMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmQualityStatusEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmReturnIssueStatusEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmReturnIssueTypeEnum;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

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
        line.setQualityStatus(calculateQualityStatus(line.getQcFlag(), issue.getType()));
        issueLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateReturnIssueLine(MesWmReturnIssueLineSaveReqVO updateReqVO) {
        // 校验存在
        validateReturnIssueLineExists(updateReqVO.getId());
        // 校验父数据存在
        MesWmReturnIssueDO issue = issueService.validateReturnIssueExists(updateReqVO.getIssueId());
        // 校验物料存在
        itemService.validateItemExists(updateReqVO.getItemId());

        // 更新
        MesWmReturnIssueLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmReturnIssueLineDO.class);
        // 质量状态自动赋值
        updateObj.setQualityStatus(calculateQualityStatus(updateObj.getQcFlag(), issue.getType()));
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

    @Override
    public void updateReturnIssueQualityStatusByIssueId(Long issueId, Integer issueType) {
        List<MesWmReturnIssueLineDO> lines = issueLineMapper.selectListByIssueId(issueId);
        for (MesWmReturnIssueLineDO line : lines) {
            Integer newStatus = calculateQualityStatus(line.getQcFlag(), issueType);
            if (ObjUtil.notEqual(newStatus, line.getQualityStatus())) {
                issueLineMapper.updateById(new MesWmReturnIssueLineDO()
                        .setId(line.getId()).setQualityStatus(newStatus));
            }
        }
    }

    /**
     * 根据 qcFlag 和退料类型，计算质量状态
     *
     * @param qcFlag 是否需要质检
     * @param issueType 退料类型
     * @return 质量状态
     */
    private Integer calculateQualityStatus(Boolean qcFlag, Integer issueType) {
        if (Boolean.TRUE.equals(qcFlag)) {
            return MesWmQualityStatusEnum.PENDING.getStatus(); // 待检
        }
        if (MesWmReturnIssueTypeEnum.REMAINDER.getType().equals(issueType)) {
            return MesWmQualityStatusEnum.PASS.getStatus(); // 合格（余料退回直接合格）
        }
        return MesWmQualityStatusEnum.FAIL.getStatus(); // 不合格
    }

    @Override
    public void updateReturnIssueLineWhenRqcFinish(Long sourceLineId, Long sourceDocId, Integer checkResult,
                                                    BigDecimal qualifiedQuantity, BigDecimal unqualifiedQuantity) {
        MesWmReturnIssueLineDO sourceLine = validateReturnIssueLineExists(sourceLineId);
        boolean hasUnqualified = unqualifiedQuantity != null && unqualifiedQuantity.compareTo(BigDecimal.ZERO) > 0;
        boolean hasQualified = qualifiedQuantity != null && qualifiedQuantity.compareTo(BigDecimal.ZERO) > 0;

        // 1. 根据合格/不合格品数量，更新退料单行的质量状态
        if (!hasUnqualified) {
            // 1.A 情况一：全部合格
            issueLineMapper.updateById(new MesWmReturnIssueLineDO()
                    .setId(sourceLineId).setQualityStatus(MesWmQualityStatusEnum.PASS.getStatus()));
        } else if (!hasQualified) {
            // 1.B 情况二：全部不合格
            issueLineMapper.updateById(new MesWmReturnIssueLineDO()
                    .setId(sourceLineId).setQualityStatus(MesWmQualityStatusEnum.FAIL.getStatus()));
        } else {
            // 1.C 情况三：部分合格部分不合格 → 拆分行
            // 1.C.1 新增一行不合格品
            MesWmReturnIssueLineDO unqualifiedLine = new MesWmReturnIssueLineDO()
                    .setIssueId(sourceLine.getIssueId())
                    .setMaterialStockId(sourceLine.getMaterialStockId())
                    .setItemId(sourceLine.getItemId())
                    .setQuantity(unqualifiedQuantity)
                    .setBatchId(sourceLine.getBatchId())
                    .setIpqcId(sourceLine.getIpqcId())
                    .setQcFlag(sourceLine.getQcFlag())
                    .setQualityStatus(MesWmQualityStatusEnum.FAIL.getStatus())
                    .setRemark(sourceLine.getRemark());
            issueLineMapper.insert(unqualifiedLine);
            // 1.C.2 更新原行为合格品（数量调整为合格数量）
            issueLineMapper.updateById(new MesWmReturnIssueLineDO()
                    .setId(sourceLineId)
                    .setQuantity(qualifiedQuantity)
                    .setQualityStatus(MesWmQualityStatusEnum.PASS.getStatus()));
        }

        // 2. 检查退料单下是否还有待检验的行，若全检完则将退料单状态设为"待上架"
        if (sourceDocId != null) {
            List<MesWmReturnIssueLineDO> allLines = issueLineMapper.selectListByIssueId(sourceDocId);
            boolean allInspected = !CollUtil.contains(allLines,
                    line -> Objects.equals(line.getQualityStatus(), MesWmQualityStatusEnum.PENDING.getStatus()));
            if (allInspected) {
                issueService.updateReturnIssueStatus(sourceDocId, MesWmReturnIssueStatusEnum.APPROVING.getStatus());
            }
        }
    }

}
