package cn.iocoder.yudao.module.mes.service.wm.itemreceipt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.MesWmItemReceiptPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.MesWmItemReceiptSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.itemreceipt.MesWmItemReceiptMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmItemReceiptStatusEnum;
import cn.iocoder.yudao.module.mes.service.wm.arrivalnotice.MesWmArrivalNoticeService;
import cn.iocoder.yudao.module.mes.service.wm.materialstock.MesWmMaterialStockService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 采购入库单 Service 实现类
 */
@Service
@Validated
public class MesWmItemReceiptServiceImpl implements MesWmItemReceiptService {

    @Resource
    private MesWmItemReceiptMapper itemReceiptMapper;

    @Resource
    private MesWmItemReceiptLineService itemReceiptLineService;

    @Resource
    private MesWmItemReceiptDetailService itemReceiptDetailService;

    @Resource
    @Lazy
    private MesWmArrivalNoticeService arrivalNoticeService;

    @Resource
    private MesWmMaterialStockService materialStockService;

    @Override
    public Long createItemReceipt(MesWmItemReceiptSaveReqVO createReqVO) {
        // 校验编码唯一
        validateCodeUnique(null, createReqVO.getCode());

        // 插入
        MesWmItemReceiptDO receipt = BeanUtils.toBean(createReqVO, MesWmItemReceiptDO.class);
        receipt.setStatus(MesWmItemReceiptStatusEnum.PREPARE.getStatus());
        itemReceiptMapper.insert(receipt);
        return receipt.getId();
    }

    @Override
    public void updateItemReceipt(MesWmItemReceiptSaveReqVO updateReqVO) {
        // 校验存在 + 草稿状态
        validateItemReceiptExistsAndDraft(updateReqVO.getId());
        // 校验编码唯一
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 更新
        MesWmItemReceiptDO updateObj = BeanUtils.toBean(updateReqVO, MesWmItemReceiptDO.class);
        itemReceiptMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItemReceipt(Long id) {
        // 校验存在 + 草稿状态
        validateItemReceiptExistsAndDraft(id);

        // 级联删除明细和行
        itemReceiptDetailService.deleteItemReceiptDetailByReceiptId(id);
        itemReceiptLineService.deleteItemReceiptLineByReceiptId(id);
        // 删除
        itemReceiptMapper.deleteById(id);
    }

    @Override
    public MesWmItemReceiptDO getItemReceipt(Long id) {
        return itemReceiptMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmItemReceiptDO> getItemReceiptPage(MesWmItemReceiptPageReqVO pageReqVO) {
        return itemReceiptMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitItemReceipt(Long id) {
        // 校验存在 + 草稿状态
        validateItemReceiptExistsAndDraft(id);
        // 校验至少有一条行
        List<MesWmItemReceiptLineDO> lines = itemReceiptLineService.getItemReceiptLineListByReceiptId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_ITEM_RECEIPT_NO_LINE);
        }

        // 提交（草稿 → 待上架）
        itemReceiptMapper.updateById(new MesWmItemReceiptDO()
                .setId(id).setStatus(MesWmItemReceiptStatusEnum.APPROVING.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shelvingItemReceipt(Long id) {
        // 校验存在
        MesWmItemReceiptDO receipt = validateItemReceiptExists(id);
        if (ObjUtil.notEqual(MesWmItemReceiptStatusEnum.APPROVING.getStatus(), receipt.getStatus())) {
            throw exception(WM_ITEM_RECEIPT_STATUS_ERROR);
        }
        // 校验每行的 SUM(detail.quantity) = line.receivedQuantity
        List<MesWmItemReceiptLineDO> lines = itemReceiptLineService.getItemReceiptLineListByReceiptId(id);
        for (MesWmItemReceiptLineDO line : lines) {
            List<MesWmItemReceiptDetailDO> details = itemReceiptDetailService.getItemReceiptDetailListByLineId(line.getId());
            BigDecimal totalDetailQty = CollectionUtils.getSumValue(details,
                    MesWmItemReceiptDetailDO::getQuantity, BigDecimal::add, BigDecimal.ZERO);
            if (line.getReceivedQuantity() != null && totalDetailQty.compareTo(line.getReceivedQuantity()) != 0) {
                throw exception(WM_ITEM_RECEIPT_DETAIL_QUANTITY_MISMATCH);
            }
        }

        // 执行上架（待上架 → 待入库）
        itemReceiptMapper.updateById(new MesWmItemReceiptDO()
                .setId(id).setStatus(MesWmItemReceiptStatusEnum.APPROVED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeItemReceipt(Long id) {
        // 校验存在
        MesWmItemReceiptDO receipt = validateItemReceiptExists(id);
        if (ObjUtil.notEqual(MesWmItemReceiptStatusEnum.APPROVED.getStatus(), receipt.getStatus())) {
            throw exception(WM_ITEM_RECEIPT_STATUS_ERROR);
        }

        // 遍历所有明细，更新库存台账
        // TODO @AI：这里可能有点问题；缺少库存更新；后面在弄；
        List<MesWmItemReceiptDetailDO> details = itemReceiptDetailService.getItemReceiptDetailListByReceiptId(id);
        for (MesWmItemReceiptDetailDO detail : details) {
            materialStockService.increaseStock(
                    detail.getItemId(), detail.getWarehouseId(), detail.getLocationId(), detail.getAreaId(),
                    detail.getBatchId(), detail.getQuantity(), receipt.getVendorId(), null, null);
        }

        // 更新入库单状态
        itemReceiptMapper.updateById(new MesWmItemReceiptDO()
                .setId(id).setStatus(MesWmItemReceiptStatusEnum.FINISHED.getStatus()));

        // 更新关联的到货通知单状态
        if (receipt.getNoticeId() != null) {
            arrivalNoticeService.finishArrivalNotice(receipt.getNoticeId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelItemReceipt(Long id) {
        // 校验存在
        MesWmItemReceiptDO receipt = validateItemReceiptExists(id);
        // 已完成和已取消不允许取消
        // TODO DONE @芋艿：确认只有已完成和已取消 2 个状态不允许取消
        if (ObjectUtils.equalsAny(receipt.getStatus(),
                MesWmItemReceiptStatusEnum.FINISHED.getStatus(),
                MesWmItemReceiptStatusEnum.CANCELED.getStatus())) {
            throw exception(WM_ITEM_RECEIPT_CANCEL_NOT_ALLOWED);
        }
        // 取消
        itemReceiptMapper.updateById(new MesWmItemReceiptDO()
                .setId(id).setStatus(MesWmItemReceiptStatusEnum.CANCELED.getStatus()));
    }

    @Override
    public void validateItemReceiptEditable(Long id) {
        MesWmItemReceiptDO receipt = validateItemReceiptExists(id);
        // TODO DONE @AI：已使用 ObjectUtils.equalsAny 简化多值判断，方法已迁移到 Service 层
        // TODO @AI：ObjectUtils 封装一个方法，避免取反；脑子理解起来麻烦
        if (!ObjectUtils.equalsAny(receipt.getStatus(),
                MesWmItemReceiptStatusEnum.PREPARE.getStatus(),
                MesWmItemReceiptStatusEnum.APPROVING.getStatus())) {
            throw exception(WM_ITEM_RECEIPT_STATUS_NOT_PREPARE);
        }
    }

    private MesWmItemReceiptDO validateItemReceiptExists(Long id) {
        MesWmItemReceiptDO receipt = itemReceiptMapper.selectById(id);
        if (receipt == null) {
            throw exception(WM_ITEM_RECEIPT_NOT_EXISTS);
        }
        return receipt;
    }

    /**
     * 校验采购入库单存在且为草稿状态
     */
    private MesWmItemReceiptDO validateItemReceiptExistsAndDraft(Long id) {
        MesWmItemReceiptDO receipt = validateItemReceiptExists(id);
        if (ObjUtil.notEqual(MesWmItemReceiptStatusEnum.PREPARE.getStatus(), receipt.getStatus())) {
            throw exception(WM_ITEM_RECEIPT_STATUS_NOT_PREPARE);
        }
        return receipt;
    }

    private void validateCodeUnique(Long id, String code) {
        MesWmItemReceiptDO receipt = itemReceiptMapper.selectByCode(code);
        if (receipt == null) {
            return;
        }
        if (ObjUtil.notEqual(id, receipt.getId())) {
            throw exception(WM_ITEM_RECEIPT_CODE_DUPLICATE);
        }
    }

}
