package cn.iocoder.yudao.module.mes.service.wm.transfer;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialstock.vo.MesWmMaterialStockFreezeReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.MesWmTransferPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.MesWmTransferSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.transfer.MesWmTransferDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.transfer.MesWmTransferDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.transfer.MesWmTransferLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.transfer.MesWmTransferMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmTransferStatusEnum;
import cn.iocoder.yudao.module.mes.service.wm.materialstock.MesWmMaterialStockService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 转移单 Service 实现类
 */
@Service
@Validated
public class MesWmTransferServiceImpl implements MesWmTransferService {

    @Resource
    private MesWmTransferMapper transferMapper;

    @Resource
    private MesWmTransferLineService transferLineService;
    @Resource
    private MesWmTransferDetailService transferDetailService;
    @Resource
    private MesWmMaterialStockService materialStockService;

    @Override
    public Long createTransfer(MesWmTransferSaveReqVO createReqVO) {
        // 校验编码唯一
        validateCodeUnique(null, createReqVO.getCode());

        // 插入
        MesWmTransferDO transfer = BeanUtils.toBean(createReqVO, MesWmTransferDO.class);
        transfer.setStatus(MesWmTransferStatusEnum.PREPARE.getStatus());
        transferMapper.insert(transfer);
        return transfer.getId();
    }

    @Override
    public void updateTransfer(MesWmTransferSaveReqVO updateReqVO) {
        // 校验存在 + 草稿状态
        validateTransferExistsAndDraft(updateReqVO.getId());
        // 校验编码唯一
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 更新
        MesWmTransferDO updateObj = BeanUtils.toBean(updateReqVO, MesWmTransferDO.class);
        transferMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTransfer(Long id) {
        // 校验存在 + 草稿状态
        validateTransferExistsAndDraft(id);

        // 级联删除明细和行
        transferDetailService.deleteTransferDetailByTransferId(id);
        transferLineService.deleteTransferLineByTransferId(id);
        // 删除
        transferMapper.deleteById(id);
    }

    @Override
    public MesWmTransferDO getTransfer(Long id) {
        return transferMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmTransferDO> getTransferPage(MesWmTransferPageReqVO pageReqVO) {
        return transferMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitTransfer(Long id) {
        // 校验存在 + 草稿状态
        MesWmTransferDO transfer = validateTransferExistsAndDraft(id);
        List<MesWmTransferLineDO> lines = transferLineService.getTransferLineListByTransferId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_TRANSFER_NO_LINE);
        }

        // 先按是否配送决定是否进入待确认，并冻结来源库存
        // TODO @AI：！Boolean.TRUE.equals 改成 FALSE 平判断，减少取反。
        if (Boolean.TRUE.equals(transfer.getDeliveryFlag()) && !Boolean.TRUE.equals(transfer.getConfirmFlag())) {
            transfer.setStatus(MesWmTransferStatusEnum.UNCONFIRMED.getStatus()).setConfirmFlag(false);
            // TODO @AI：这个是不是应该 lineservice 里；
            freezeTransferLineStocks(lines, true);
        } else {
            transfer.setStatus(MesWmTransferStatusEnum.UNSTOCK.getStatus());
        }
        transferMapper.updateById(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmTransfer(Long id) {
        // 校验存在 + 待确认状态
        MesWmTransferDO transfer = validateTransferExistsAndConfirm(id);

        // 更新状态：待确认 -> 待上架
        transfer.setStatus(MesWmTransferStatusEnum.UNSTOCK.getStatus());
        transfer.setConfirmFlag(true);
        transferMapper.updateById(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishTransfer(Long id) {
        // 校验存在 + 待执行状态
        MesWmTransferDO transfer = validateTransferExistsAndUnexecute(id);
        List<MesWmTransferLineDO> lines = transferLineService.getTransferLineListByTransferId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_TRANSFER_NO_LINE);
        }

        // 校验每行明细数量之和等于行数量
        validateTransferDetailQuantity(lines);

        // 配送模式下，执行完成后解除来源库存冻结
        if (Boolean.TRUE.equals(transfer.getDeliveryFlag())) {
            freezeTransferLineStocks(lines, false);
        }

        // TODO: 执行库存转移逻辑

        // 更新状态：待执行 -> 已完成
        transfer.setStatus(MesWmTransferStatusEnum.FINISHED.getStatus());
        transferMapper.updateById(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTransfer(Long id) {
        // 校验存在 + 非已完成/已取消状态
        MesWmTransferDO transfer = validateTransferExistsAndNotFinished(id);
        // TODO @AI：这个逻辑不需要，只是取消主的。
        if (Boolean.TRUE.equals(transfer.getDeliveryFlag())
                && (MesWmTransferStatusEnum.UNCONFIRMED.getStatus().equals(transfer.getStatus())
                || MesWmTransferStatusEnum.UNEXECUTE.getStatus().equals(transfer.getStatus()))) {
            List<MesWmTransferLineDO> lines = transferLineService.getTransferLineListByTransferId(id);
            freezeTransferLineStocks(lines, false);
        }

        // 更新状态 -> 已取消
        transfer.setStatus(MesWmTransferStatusEnum.CANCELED.getStatus());
        transferMapper.updateById(transfer);
    }

    @Override
    public MesWmTransferDO validateTransferEditable(Long id) {
        MesWmTransferDO transfer = transferMapper.selectById(id);
        if (transfer == null) {
            throw exception(WM_TRANSFER_NOT_EXISTS);
        }
        if (!MesWmTransferStatusEnum.PREPARE.getStatus().equals(transfer.getStatus())) {
            throw exception(WM_TRANSFER_NOT_EDITABLE);
        }
        return transfer;
    }

    // ==================== 私有方法 ====================

    private void validateCodeUnique(Long id, String code) {
        MesWmTransferDO transfer = transferMapper.selectByCode(code);
        if (transfer == null) {
            return;
        }
        // 如果 id 为空，说明是新增，存在同名则报错
        if (id == null) {
            throw exception(WM_TRANSFER_CODE_DUPLICATE);
        }
        // 如果 id 不为空，说明是修改，存在同名且不是自己则报错
        if (!transfer.getId().equals(id)) {
            throw exception(WM_TRANSFER_CODE_DUPLICATE);
        }
    }

    private MesWmTransferDO validateTransferExistsAndDraft(Long id) {
        MesWmTransferDO transfer = transferMapper.selectById(id);
        if (transfer == null) {
            throw exception(WM_TRANSFER_NOT_EXISTS);
        }
        if (!MesWmTransferStatusEnum.PREPARE.getStatus().equals(transfer.getStatus())) {
            throw exception(WM_TRANSFER_NOT_DRAFT);
        }
        return transfer;
    }

    private MesWmTransferDO validateTransferExistsAndConfirm(Long id) {
        MesWmTransferDO transfer = transferMapper.selectById(id);
        if (transfer == null) {
            throw exception(WM_TRANSFER_NOT_EXISTS);
        }
        if (!MesWmTransferStatusEnum.UNCONFIRMED.getStatus().equals(transfer.getStatus())) {
            throw exception(WM_TRANSFER_NOT_CONFIRMED);
        }
        return transfer;
    }

    private MesWmTransferDO validateTransferExistsAndUnexecute(Long id) {
        MesWmTransferDO transfer = transferMapper.selectById(id);
        if (transfer == null) {
            throw exception(WM_TRANSFER_NOT_EXISTS);
        }
        if (!MesWmTransferStatusEnum.UNEXECUTE.getStatus().equals(transfer.getStatus())) {
            throw exception(WM_TRANSFER_NOT_UNEXECUTE);
        }
        return transfer;
    }

    private MesWmTransferDO validateTransferExistsAndNotFinished(Long id) {
        MesWmTransferDO transfer = transferMapper.selectById(id);
        if (transfer == null) {
            throw exception(WM_TRANSFER_NOT_EXISTS);
        }
        if (MesWmTransferStatusEnum.FINISHED.getStatus().equals(transfer.getStatus())
                || MesWmTransferStatusEnum.CANCELED.getStatus().equals(transfer.getStatus())) {
            throw exception(WM_TRANSFER_ALREADY_FINISHED);
        }
        return transfer;
    }

    // TODO @AI：参考别的模块，不用独立方法，直接写在 UNEXECUTE 变更里里；。目前还少一个操作；    UNEXECUTE(MesOrderStatusConstants.APPROVED, "待执行"), 你看看别的模块；
    private void validateTransferDetailQuantity(List<MesWmTransferLineDO> lines) {
        for (MesWmTransferLineDO line : lines) {
            List<MesWmTransferDetailDO> details = transferDetailService.getTransferDetailListByLineId(line.getId());
            BigDecimal totalDetailQty = CollectionUtils.getSumValue(details,
                    MesWmTransferDetailDO::getQuantity, BigDecimal::add, BigDecimal.ZERO);
            if (line.getQuantity() != null && totalDetailQty.compareTo(line.getQuantity()) != 0) {
                throw exception(WM_TRANSFER_DETAIL_QUANTITY_MISMATCH);
            }
        }
    }

    private void freezeTransferLineStocks(List<MesWmTransferLineDO> lines, boolean frozen) {
        if (CollUtil.isEmpty(lines)) {
            return;
        }
        for (MesWmTransferLineDO line : lines) {
            if (line.getMaterialStockId() == null) {
                continue;
            }
            MesWmMaterialStockFreezeReqVO freezeReqVO = new MesWmMaterialStockFreezeReqVO();
            freezeReqVO.setId(line.getMaterialStockId());
            freezeReqVO.setFrozen(frozen);
            materialStockService.updateMaterialStockFrozen(freezeReqVO);
        }
    }

}
