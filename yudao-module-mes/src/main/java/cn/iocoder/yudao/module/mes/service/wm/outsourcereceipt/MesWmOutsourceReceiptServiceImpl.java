package cn.iocoder.yudao.module.mes.service.wm.outsourcereceipt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.outsourcereceipt.vo.MesWmOutsourceReceiptPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.outsourcereceipt.vo.MesWmOutsourceReceiptSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourcereceipt.MesWmOutsourceReceiptDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourcereceipt.MesWmOutsourceReceiptDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourcereceipt.MesWmOutsourceReceiptLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.outsourcereceipt.MesWmOutsourceReceiptDetailMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.outsourcereceipt.MesWmOutsourceReceiptLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.outsourcereceipt.MesWmOutsourceReceiptMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmOutsourceReceiptStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.vendor.MesMdVendorService;
import cn.iocoder.yudao.module.mes.service.wm.materialstock.MesWmMaterialStockService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseAreaService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseLocationService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
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
    private MesWmMaterialStockService materialStockService;

    @Resource
    private MesMdVendorService vendorService;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesWmWarehouseService warehouseService;

    @Resource
    private MesWmWarehouseLocationService warehouseLocationService;

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
        // 校验物料存在
        for (MesWmOutsourceReceiptLineDO line : lines) {
            itemService.validateItemExists(line.getItemId());
        }

        // 提交（草稿 → 审批中）
        outsourceReceiptMapper.updateById(new MesWmOutsourceReceiptDO()
                .setId(id).setStatus(MesWmOutsourceReceiptStatusEnum.APPROVING.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockOutsourceReceipt(Long id) {
        // 校验存在
        MesWmOutsourceReceiptDO receipt = validateOutsourceReceiptExists(id);
        if (ObjUtil.notEqual(MesWmOutsourceReceiptStatusEnum.APPROVING.getStatus(), receipt.getStatus())) {
            throw exception(WM_OUTSOURCE_RECEIPT_STATUS_ERROR);
        }
        // 校验每行明细数量之和是否等于行入库数量
        // TODO @AI：这个校验，不需要了；
        List<MesWmOutsourceReceiptLineDO> lines = outsourceReceiptLineMapper.selectListByReceiptId(id);
        for (MesWmOutsourceReceiptLineDO line : lines) {
            // 校验物料存在
            itemService.validateItemExists(line.getItemId());

            List<MesWmOutsourceReceiptDetailDO> details = outsourceReceiptDetailMapper.selectListByLineId(line.getId());
            BigDecimal totalDetailQty = CollectionUtils.getSumValue(details,
                    MesWmOutsourceReceiptDetailDO::getQuantity, BigDecimal::add, BigDecimal.ZERO);
            if (line.getQuantity() != null && totalDetailQty.compareTo(line.getQuantity()) != 0) {
                throw exception(WM_OUTSOURCE_RECEIPT_DETAIL_QUANTITY_MISMATCH);
            }

            // 校验明细中的仓库、库区、库位存在
            for (MesWmOutsourceReceiptDetailDO detail : details) {
                itemService.validateItemExists(detail.getItemId());
                if (detail.getWarehouseId() != null) {
                    warehouseService.validateWarehouseExists(detail.getWarehouseId());
                }
                if (detail.getLocationId() != null) {
                    warehouseLocationService.validateWarehouseLocationExists(detail.getLocationId());
                }
                if (detail.getAreaId() != null) {
                    warehouseAreaService.validateWarehouseAreaExists(detail.getAreaId());
                }
            }
        }

        // 审批（审批中 → 已审批）
        outsourceReceiptMapper.updateById(new MesWmOutsourceReceiptDO()
                .setId(id).setStatus(MesWmOutsourceReceiptStatusEnum.APPROVED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishOutsourceReceipt(Long id) {
        // 校验存在
        MesWmOutsourceReceiptDO receipt = validateOutsourceReceiptExists(id);
        if (ObjUtil.notEqual(MesWmOutsourceReceiptStatusEnum.APPROVED.getStatus(), receipt.getStatus())) {
            throw exception(WM_OUTSOURCE_RECEIPT_STATUS_ERROR);
        }

        // 遍历所有明细，校验并更新库存台账
        // TODO @AI：芋艿【暂时不处理】；后续在观察；
        List<MesWmOutsourceReceiptDetailDO> details = outsourceReceiptDetailMapper.selectListByReceiptId(id);
        for (MesWmOutsourceReceiptDetailDO detail : details) {
            // 校验物料、仓库、库区、库位存在
            // TODO @AI：warehouseAreaService 有个公用的校验；
            itemService.validateItemExists(detail.getItemId());
            if (detail.getWarehouseId() != null) {
                warehouseService.validateWarehouseExists(detail.getWarehouseId());
            }
            if (detail.getLocationId() != null) {
                warehouseLocationService.validateWarehouseLocationExists(detail.getLocationId());
            }
            if (detail.getAreaId() != null) {
                warehouseAreaService.validateWarehouseAreaExists(detail.getAreaId());
            }

            materialStockService.increaseStock(
                    detail.getItemId(), detail.getWarehouseId(), detail.getLocationId(), detail.getAreaId(),
                    detail.getBatchId(), detail.getQuantity(), receipt.getVendorId(), null, null);
        }

        // 更新入库单状态
        outsourceReceiptMapper.updateById(new MesWmOutsourceReceiptDO()
                .setId(id).setStatus(MesWmOutsourceReceiptStatusEnum.FINISHED.getStatus()));
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
