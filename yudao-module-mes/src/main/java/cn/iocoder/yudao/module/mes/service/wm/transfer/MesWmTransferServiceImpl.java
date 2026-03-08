package cn.iocoder.yudao.module.mes.service.wm.transfer;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.MesWmTransferPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.MesWmTransferSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.transfer.MesWmTransferDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.transfer.MesWmTransferMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmTransferStatusEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmTransferTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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

    // TODO @AI：MesWmTransferLineService、MesWmTransferDetailService 类不存在；
    @Resource
    private MesWmTransferLineService transferLineService;
    @Resource
    private MesWmTransferDetailService transferDetailService;

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

    // TODO @AI：少了事务；
    @Override
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
    public void submitTransfer(Long id) {
        // 校验存在 + 草稿状态
        MesWmTransferDO transfer = validateTransferExistsAndDraft(id);

        // TODO @芋艿：这里的逻辑对齐不对；需要再次看看；
        // 根据转移单类型设置状态
        // 内部调拨：草稿 -> 待上架
        // 外部调拨：草稿 -> 待确认
        if (MesWmTransferTypeEnum.INNER.getType().equals(transfer.getType())) {
            transfer.setStatus(MesWmTransferStatusEnum.UNSTOCK.getStatus());
        } else {
            transfer.setStatus(MesWmTransferStatusEnum.UNCONFIRMED.getStatus());
        }
        transferMapper.updateById(transfer);
    }

    @Override
    public void confirmTransfer(Long id) {
        // 校验存在 + 待确认状态
        MesWmTransferDO transfer = validateTransferExistsAndConfirm(id);

        // 更新状态：待确认 -> 待上架
        transfer.setStatus(MesWmTransferStatusEnum.UNSTOCK.getStatus());
        transfer.setConfirmFlag(true);
        transferMapper.updateById(transfer);
    }

    @Override
    public void finishTransfer(Long id) {
        // 校验存在 + 待执行状态
        MesWmTransferDO transfer = validateTransferExistsAndUnexecute(id);

        // TODO: 执行库存转移逻辑

        // 更新状态：待执行 -> 已完成
        transfer.setStatus(MesWmTransferStatusEnum.FINISHED.getStatus());
        transferMapper.updateById(transfer);
    }

    @Override
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
        // TODO @AI：错误码缺少；是不是少了 WM_
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

}
