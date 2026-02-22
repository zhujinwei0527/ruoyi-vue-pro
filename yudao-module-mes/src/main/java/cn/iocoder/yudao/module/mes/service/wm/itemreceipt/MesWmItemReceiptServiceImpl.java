package cn.iocoder.yudao.module.mes.service.wm.itemreceipt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.MesWmItemReceiptPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.MesWmItemReceiptSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.itemreceipt.MesWmItemReceiptDetailMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.itemreceipt.MesWmItemReceiptLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.itemreceipt.MesWmItemReceiptMapper;
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
    private MesWmItemReceiptLineMapper itemReceiptLineMapper;

    @Resource
    private MesWmItemReceiptDetailMapper itemReceiptDetailMapper;

    @Resource
    @Lazy
    private MesWmArrivalNoticeService arrivalNoticeService;

    @Resource
    private MesWmMaterialStockService materialStockService;

    @Override
    public Long createItemReceipt(MesWmItemReceiptSaveReqVO createReqVO) {
        // 校验编码唯一
        validateCodeUnique(null, createReqVO.getCode());
        // TODO @AI：校验关联字段；

        // 插入
        MesWmItemReceiptDO receipt = BeanUtils.toBean(createReqVO, MesWmItemReceiptDO.class);
        receipt.setStatus(0); // 草稿 TODO @AI：使用枚举类
        itemReceiptMapper.insert(receipt);
        return receipt.getId();
    }

    @Override
    public void updateItemReceipt(MesWmItemReceiptSaveReqVO updateReqVO) {
        // 校验存在
        MesWmItemReceiptDO receipt = validateItemReceiptExists(updateReqVO.getId());
        // TODO @AI：校验关联字段；
        // 校验状态：只有草稿才允许修改
        // TODO @AI：校验状态，看看抽个方法，尽量几个方法复用
        if (receipt.getStatus() != 0) {
            throw exception(WM_ITEM_RECEIPT_STATUS_NOT_PREPARE);
        }
        // 校验编码唯一
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 更新
        MesWmItemReceiptDO updateObj = BeanUtils.toBean(updateReqVO, MesWmItemReceiptDO.class);
        itemReceiptMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItemReceipt(Long id) {
        // 校验存在
        MesWmItemReceiptDO receipt = validateItemReceiptExists(id);
        // 校验状态：只有草稿才允许删除
        // TODO @AI：校验状态，看看抽个方法，尽量几个方法复用
        if (receipt.getStatus() != 0) {
            throw exception(WM_ITEM_RECEIPT_STATUS_NOT_PREPARE);
        }

        // 级联删除明细和行
        itemReceiptDetailMapper.deleteByReceiptId(id);
        itemReceiptLineMapper.deleteByReceiptId(id);
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
    public void submitItemReceipt(Long id) {
        // 校验存在
        MesWmItemReceiptDO receipt = validateItemReceiptExists(id);
        // TODO @AI：校验状态，看看抽个方法，尽量几个方法复用
        if (receipt.getStatus() != 0) {
            throw exception(WM_ITEM_RECEIPT_STATUS_NOT_PREPARE);
        }
        // 校验至少有一条行
        // TODO @AI：对方抽个计算数量的方法啊；
        List<MesWmItemReceiptLineDO> lines = itemReceiptLineMapper.selectListByReceiptId(id);
        if (lines.isEmpty()) {
            throw exception(WM_ITEM_RECEIPT_NO_LINE);
        }

        // 提交
        itemReceiptMapper.updateById(new MesWmItemReceiptDO().setId(id).setStatus(1));
    }

    @Override
    public void approveItemReceipt(Long id) {
        // 校验存在
        MesWmItemReceiptDO receipt = validateItemReceiptExists(id);
        // TODO @AI：抽一些校验的存在+状态方法
        if (receipt.getStatus() != 1) {
            throw exception(WM_ITEM_RECEIPT_STATUS_ERROR);
        }
        // 校验每行的 SUM(detail.quantity) = line.receivedQuantity
        // TODO @AI：抽个计算的 count 方法，然后在行和明细都用；不然代码里到处都是计算的逻辑；
        List<MesWmItemReceiptLineDO> lines = itemReceiptLineMapper.selectListByReceiptId(id);
        for (MesWmItemReceiptLineDO line : lines) {
            List<MesWmItemReceiptDetailDO> details = itemReceiptDetailMapper.selectListByLineId(line.getId());
            BigDecimal totalDetailQty = details.stream()
                    .map(MesWmItemReceiptDetailDO::getQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (line.getReceivedQuantity() != null && totalDetailQty.compareTo(line.getReceivedQuantity()) != 0) {
                throw exception(WM_ITEM_RECEIPT_DETAIL_QUANTITY_MISMATCH);
            }
        }

        // 审批
        itemReceiptMapper.updateById(new MesWmItemReceiptDO().setId(id).setStatus(2));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeItemReceipt(Long id) {
        // 校验存在
        MesWmItemReceiptDO receipt = validateItemReceiptExists(id);
        // TODO @AI：抽一些校验的存在+状态方法
        if (receipt.getStatus() != 2) {
            throw exception(WM_ITEM_RECEIPT_STATUS_ERROR);
        }

        // 遍历所有明细，更新库存台账
        // TODO @AI：select 方法，对方搞个；
        List<MesWmItemReceiptDetailDO> details = itemReceiptDetailMapper.selectListByReceiptId(id);
        for (MesWmItemReceiptDetailDO detail : details) {
            materialStockService.increaseStock(
                    detail.getItemId(), detail.getWarehouseId(), detail.getLocationId(), detail.getAreaId(),
                    detail.getBatchId(), detail.getQuantity(), receipt.getVendorId(), null, null);
        }

        // 更新入库单状态
        // TODO @AI：枚举类；
        itemReceiptMapper.updateById(new MesWmItemReceiptDO().setId(id).setStatus(3));
        // 更新关联的到货通知单状态
        if (receipt.getNoticeId() != null) {
            arrivalNoticeService.finishArrivalNotice(receipt.getNoticeId());
        }
    }

    private MesWmItemReceiptDO validateItemReceiptExists(Long id) {
        MesWmItemReceiptDO receipt = itemReceiptMapper.selectById(id);
        if (receipt == null) {
            throw exception(WM_ITEM_RECEIPT_NOT_EXISTS);
        }
        return receipt;
    }

    private void validateCodeUnique(Long id, String code) {
        MesWmItemReceiptDO receipt = itemReceiptMapper.selectByCode(code);
        if (receipt == null) {
            return;
        }
        // TODO @AI：ObjUtil notEquals
        if (id == null || !id.equals(receipt.getId())) {
            throw exception(WM_ITEM_RECEIPT_CODE_DUPLICATE);
        }
    }

}
