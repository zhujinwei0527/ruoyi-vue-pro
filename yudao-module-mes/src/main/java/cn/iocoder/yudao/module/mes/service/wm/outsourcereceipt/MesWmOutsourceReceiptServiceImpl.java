package cn.iocoder.yudao.module.mes.service.wm.outsourcereceipt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.outsourcereceipt.vo.MesWmOutsourceReceiptPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.outsourcereceipt.vo.MesWmOutsourceReceiptSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourcereceipt.MesWmOutsourceReceiptDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourcereceipt.MesWmOutsourceReceiptDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourcereceipt.MesWmOutsourceReceiptLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.outsourcereceipt.MesWmOutsourceReceiptDetailMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.outsourcereceipt.MesWmOutsourceReceiptLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.outsourcereceipt.MesWmOutsourceReceiptMapper;
import cn.iocoder.yudao.module.mes.enums.MesBizTypeConstants;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmOutsourceReceiptStatusEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmQualityStatusEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmTransactionTypeEnum;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.vendor.MesMdVendorService;
import cn.iocoder.yudao.module.mes.service.wm.transaction.MesWmTransactionService;
import cn.iocoder.yudao.module.mes.service.wm.transaction.dto.MesWmTransactionSaveReqDTO;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseAreaService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 外协入库单 Service 实现类
 */
@Service
@Validated
public class MesWmOutsourceReceiptServiceImpl implements MesWmOutsourceReceiptService {

    @Resource
    private MesWmOutsourceReceiptMapper outsourceReceiptMapper;

    @Resource
    private MesWmOutsourceReceiptLineMapper outsourceReceiptLineMapper;

    @Resource
    private MesWmOutsourceReceiptDetailMapper outsourceReceiptDetailMapper;

    @Resource
    private MesWmTransactionService wmTransactionService;

    @Resource
    private MesMdVendorService vendorService;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesWmWarehouseAreaService warehouseAreaService;

    @Override
    public Long createOutsourceReceipt(MesWmOutsourceReceiptSaveReqVO createReqVO) {
        // 校验编码唯一
        validateCodeUnique(null, createReqVO.getCode());
        // 校验供应商存在
        vendorService.validateVendorExists(createReqVO.getVendorId());

        // 插入
        MesWmOutsourceReceiptDO receipt = BeanUtils.toBean(createReqVO, MesWmOutsourceReceiptDO.class);
        receipt.setStatus(MesWmOutsourceReceiptStatusEnum.PREPARE.getStatus());
        outsourceReceiptMapper.insert(receipt);
        return receipt.getId();
    }

    @Override
    public void updateOutsourceReceipt(MesWmOutsourceReceiptSaveReqVO updateReqVO) {
        // 校验存在 + 草稿状态
        validateOutsourceReceiptExistsAndDraft(updateReqVO.getId());
        // 校验编码唯一
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 校验供应商存在
        vendorService.validateVendorExists(updateReqVO.getVendorId());

        // 更新
        MesWmOutsourceReceiptDO updateObj = BeanUtils.toBean(updateReqVO, MesWmOutsourceReceiptDO.class);
        outsourceReceiptMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOutsourceReceipt(Long id) {
        // 校验存在 + 草稿状态
        validateOutsourceReceiptExistsAndDraft(id);

        // 级联删除明细和行
        outsourceReceiptDetailMapper.deleteByReceiptId(id);
        outsourceReceiptLineMapper.deleteByReceiptId(id);
        // 删除
        outsourceReceiptMapper.deleteById(id);
    }

    @Override
    public MesWmOutsourceReceiptDO getOutsourceReceipt(Long id) {
        return outsourceReceiptMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmOutsourceReceiptDO> getOutsourceReceiptPage(MesWmOutsourceReceiptPageReqVO pageReqVO) {
        return outsourceReceiptMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOutsourceReceipt(Long id) {
        // 校验存在 + 草稿状态
        validateOutsourceReceiptExistsAndDraft(id);
        // 校验至少有一条行
        List<MesWmOutsourceReceiptLineDO> lines = outsourceReceiptLineMapper.selectListByReceiptId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_OUTSOURCE_RECEIPT_NO_LINE);
        }

        // 检查是否有待检验的行
        boolean hasPendingQc = CollUtil.contains(lines,
                line -> MesWmQualityStatusEnum.PENDING.getStatus().equals(line.getQualityStatus()));
        // 根据质检状态，路由到待检验或待上架
        Integer targetStatus = hasPendingQc ? MesWmOutsourceReceiptStatusEnum.CONFIRMED.getStatus()
                : MesWmOutsourceReceiptStatusEnum.APPROVING.getStatus();
        outsourceReceiptMapper.updateById(new MesWmOutsourceReceiptDO()
                .setId(id).setStatus(targetStatus));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockOutsourceReceipt(Long id) {
        // 1.1 校验存在 + 待上架状态
        MesWmOutsourceReceiptDO receipt = validateOutsourceReceiptExists(id);
        if (ObjUtil.notEqual(MesWmOutsourceReceiptStatusEnum.APPROVING.getStatus(), receipt.getStatus())) {
            throw exception(WM_OUTSOURCE_RECEIPT_STATUS_ERROR);
        }
        // 1.2 检查每个行的明细数量是否完成上架
        List<MesWmOutsourceReceiptLineDO> lines = outsourceReceiptLineMapper.selectListByReceiptId(id);
        if (CollUtil.isNotEmpty(lines)) {
            // 批量查询所有明细
            List<MesWmOutsourceReceiptDetailDO> allDetails = outsourceReceiptDetailMapper.selectListByReceiptId(id);
            Map<Long, List<MesWmOutsourceReceiptDetailDO>> detailMap = CollectionUtils.convertMultiMap(
                    allDetails, MesWmOutsourceReceiptDetailDO::getLineId);
            // 检查每行的明细数量
            for (MesWmOutsourceReceiptLineDO line : lines) {
                List<MesWmOutsourceReceiptDetailDO> details = detailMap.getOrDefault(line.getId(), List.of());
                BigDecimal totalDetailQuantity = CollectionUtils.getSumValue(details,
                        MesWmOutsourceReceiptDetailDO::getQuantity, BigDecimal::add, BigDecimal.ZERO);
                // 对比行数量与明细总数量，不满足直接抛出
                if (line.getQuantity().compareTo(totalDetailQuantity) > 0) {
                    MesMdItemDO item = itemService.validateItemExists(line.getItemId());
                    throw exception(WM_OUTSOURCE_RECEIPT_DETAIL_QUANTITY_MISMATCH,
                            item.getCode() + " " + item.getName() + " 未完成上架");
                }
            }
        }

        // 2. 入库上架（待上架 → 已审批）
        outsourceReceiptMapper.updateById(new MesWmOutsourceReceiptDO()
                .setId(id).setStatus(MesWmOutsourceReceiptStatusEnum.APPROVED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishOutsourceReceipt(Long id) {
        // 1. 校验存在
        MesWmOutsourceReceiptDO receipt = validateOutsourceReceiptExists(id);
        if (ObjUtil.notEqual(MesWmOutsourceReceiptStatusEnum.APPROVED.getStatus(), receipt.getStatus())) {
            throw exception(WM_OUTSOURCE_RECEIPT_STATUS_ERROR);
        }

        // 2. 遍历所有明细，创建库存事务（增加库存 + 记录流水）
        createTransactionList(receipt);

        // 3. 更新入库单状态
        outsourceReceiptMapper.updateById(new MesWmOutsourceReceiptDO()
                .setId(id).setStatus(MesWmOutsourceReceiptStatusEnum.FINISHED.getStatus()));
    }

    private void createTransactionList(MesWmOutsourceReceiptDO receipt) {
        List<MesWmOutsourceReceiptDetailDO> details = outsourceReceiptDetailMapper.selectListByReceiptId(receipt.getId());
        wmTransactionService.createTransactionList(convertList(details, detail -> new MesWmTransactionSaveReqDTO()
                .setType(MesWmTransactionTypeEnum.IN.getType()).setItemId(detail.getItemId())
                .setQuantity(detail.getQuantity()) // 入库数量为正数
                .setBatchId(detail.getBatchId())
                .setWarehouseId(detail.getWarehouseId()).setLocationId(detail.getLocationId()).setAreaId(detail.getAreaId())
                .setVendorId(receipt.getVendorId()).setReceiptTime(receipt.getReceiptDate())
                .setBizType(MesBizTypeConstants.WM_OUTSOURCE_RECPT).setBizId(receipt.getId())
                .setBizCode(receipt.getCode()).setBizLineId(detail.getLineId())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOutsourceReceipt(Long id) {
        // 校验存在
        MesWmOutsourceReceiptDO receipt = validateOutsourceReceiptExists(id);
        // 已完成和已取消不允许取消
        if (ObjectUtils.equalsAny(receipt.getStatus(),
                MesWmOutsourceReceiptStatusEnum.FINISHED.getStatus(),
                MesWmOutsourceReceiptStatusEnum.CANCELED.getStatus())) {
            throw exception(WM_OUTSOURCE_RECEIPT_CANCEL_NOT_ALLOWED);
        }
        // 取消
        outsourceReceiptMapper.updateById(new MesWmOutsourceReceiptDO()
                .setId(id).setStatus(MesWmOutsourceReceiptStatusEnum.CANCELED.getStatus()));
    }

    private MesWmOutsourceReceiptDO validateOutsourceReceiptExists(Long id) {
        MesWmOutsourceReceiptDO receipt = outsourceReceiptMapper.selectById(id);
        if (receipt == null) {
            throw exception(WM_OUTSOURCE_RECEIPT_NOT_EXISTS);
        }
        return receipt;
    }

    /**
     * 校验外协入库单存在且为草稿状态
     */
    private MesWmOutsourceReceiptDO validateOutsourceReceiptExistsAndDraft(Long id) {
        MesWmOutsourceReceiptDO receipt = validateOutsourceReceiptExists(id);
        if (ObjUtil.notEqual(MesWmOutsourceReceiptStatusEnum.PREPARE.getStatus(), receipt.getStatus())) {
            throw exception(WM_OUTSOURCE_RECEIPT_STATUS_NOT_PREPARE);
        }
        return receipt;
    }

    private void validateCodeUnique(Long id, String code) {
        MesWmOutsourceReceiptDO receipt = outsourceReceiptMapper.selectByCode(code);
        if (receipt == null) {
            return;
        }
        if (ObjUtil.notEqual(id, receipt.getId())) {
            throw exception(WM_OUTSOURCE_RECEIPT_CODE_DUPLICATE);
        }
    }

}
