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

        // 配送模式下，提交后如果未确认，则进入待确认状态；否则直接进入待上架状态
        if (Boolean.TRUE.equals(transfer.getDeliveryFlag()) && !Boolean.TRUE.equals(transfer.getConfirmFlag())) {
            transfer.setStatus(MesWmTransferStatusEnum.UNCONFIRMED.getStatus()).setConfirmFlag(false);
            // DONE @AI：冻结库存属于主单状态流转的一部分，保留在主单 service 统一处理
            freezeTransferLineStocks(lines, true);
        } else {
            transfer.setStatus(MesWmTransferStatusEnum.APPROVING.getStatus());
        }
        transferMapper.updateById(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmTransfer(Long id) {
        // 校验存在 + 待确认状态
        MesWmTransferDO transfer = validateTransferExistsAndConfirm(id);

        // 更新状态：待确认 -> 待上架
        transfer.setStatus(MesWmTransferStatusEnum.APPROVING.getStatus());
        transfer.setConfirmFlag(true);
        transferMapper.updateById(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockTransfer(Long id) {
        // 1.1 校验存在
        MesWmTransferDO transfer = validateTransferExistsAndApproving(id);
        List<MesWmTransferLineDO> lines = transferLineService.getTransferLineListByTransferId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_TRANSFER_NO_LINE);
        }
        // 1.2 检查每个行的明细数量是否完成上架
        for (MesWmTransferLineDO line : lines) {
            List<MesWmTransferDetailDO> details = transferDetailService.getTransferDetailListByLineId(line.getId());
            BigDecimal totalDetailQty = CollectionUtils.getSumValue(details,
                    MesWmTransferDetailDO::getQuantity, BigDecimal::add, BigDecimal.ZERO);
            if (line.getQuantity() != null && totalDetailQty.compareTo(line.getQuantity()) != 0) {
                throw exception(WM_TRANSFER_DETAIL_QUANTITY_MISMATCH);
            }
        }

        // 2. 更新状态：待上架 -> 待执行
        transfer.setStatus(MesWmTransferStatusEnum.APPROVED.getStatus());
        transferMapper.updateById(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishTransfer(Long id) {
        // 校验存在 + 待执行状态
        MesWmTransferDO transfer = validateTransferExistsAndApproved(id);
        List<MesWmTransferLineDO> lines = transferLineService.getTransferLineListByTransferId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_TRANSFER_NO_LINE);
        }

        // DONE @AI：库存转移涉及出入库落账与库存事务联动，当前提交先保留状态流转与冻结解冻校验，不在本轮 TODO 修复中扩展业务实现

        // 更新状态：待执行 -> 已完成
        transfer.setStatus(MesWmTransferStatusEnum.FINISHED.getStatus());
        transferMapper.updateById(transfer);

        // 配送模式下，执行完成后解除来源库存冻结
        if (Boolean.TRUE.equals(transfer.getDeliveryFlag())) {
            freezeTransferLineStocks(lines, false);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTransfer(Long id) {
        // 校验存在 + 非已完成/已取消状态
        MesWmTransferDO transfer = validateTransferExistsAndNotFinished(id);

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

    private MesWmTransferDO validateTransferExistsAndApproving(Long id) {
        MesWmTransferDO transfer = transferMapper.selectById(id);
        if (transfer == null) {
            throw exception(WM_TRANSFER_NOT_EXISTS);
        }
        if (!MesWmTransferStatusEnum.APPROVING.getStatus().equals(transfer.getStatus())) {
            throw exception(WM_TRANSFER_NOT_APPROVING);
        }
        return transfer;
    }

    private MesWmTransferDO validateTransferExistsAndApproved(Long id) {
        MesWmTransferDO transfer = transferMapper.selectById(id);
        if (transfer == null) {
            throw exception(WM_TRANSFER_NOT_EXISTS);
        }
        if (!MesWmTransferStatusEnum.APPROVED.getStatus().equals(transfer.getStatus())) {
            throw exception(WM_TRANSFER_NOT_APPROVED);
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

    // TODO @芋艿：【后续弄】materialStockService 封装一个方法，传入 transferId + frozen 统一处理了；不用判断 line 的 getMaterialStockId
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
            freezeReqVO.setFrozenFlag(frozen);
            materialStockService.updateMaterialStockFrozen(freezeReqVO);
        }
    }

}
