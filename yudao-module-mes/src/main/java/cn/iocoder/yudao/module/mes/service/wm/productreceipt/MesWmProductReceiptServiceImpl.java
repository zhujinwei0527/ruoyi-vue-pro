package cn.iocoder.yudao.module.mes.service.wm.productreceipt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productreceipt.vo.MesWmProductReceiptPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productreceipt.vo.MesWmProductReceiptSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productreceipt.MesWmProductReceiptDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productreceipt.MesWmProductReceiptDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productreceipt.MesWmProductReceiptLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productreceipt.MesWmProductReceiptMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmProductReceiptStatusEnum;
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
 * MES 产品收货单 Service 实现类
 */
@Service
@Validated
public class MesWmProductReceiptServiceImpl implements MesWmProductReceiptService {

    @Resource
    private MesWmProductReceiptMapper productReceiptMapper;

    @Resource
    private MesWmProductReceiptLineService productReceiptLineService;

    @Resource
    private MesWmProductReceiptDetailService productReceiptDetailService;

    @Resource
    private MesWmMaterialStockService materialStockService;

    @Resource
    private cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService workOrderService;

    @Override
    public Long createProductReceipt(MesWmProductReceiptSaveReqVO createReqVO) {
        // 校验编码唯一
        validateCodeUnique(null, createReqVO.getCode());
        // 校验工单存在并设置 itemId
        MesProWorkOrderDO workOrder = createReqVO.getWorkOrderId() != null ?
                workOrderService.validateWorkOrderExists(createReqVO.getWorkOrderId()) : null;

        // 插入
        MesWmProductReceiptDO receipt = BeanUtils.toBean(createReqVO, MesWmProductReceiptDO.class);
        if (workOrder != null) {
            receipt.setItemId(workOrder.getProductId());
        }
        receipt.setStatus(MesWmProductReceiptStatusEnum.PREPARE.getStatus());
        productReceiptMapper.insert(receipt);
        return receipt.getId();
    }

    @Override
    public void updateProductReceipt(MesWmProductReceiptSaveReqVO updateReqVO) {
        // 校验存在 + 草稿状态
        validateProductReceiptExistsAndDraft(updateReqVO.getId());
        // 校验编码唯一
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 校验工单存在
        MesProWorkOrderDO workOrder = updateReqVO.getWorkOrderId() != null ?
                workOrderService.validateWorkOrderExists(updateReqVO.getWorkOrderId()) : null;

        // 更新
        MesWmProductReceiptDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductReceiptDO.class);
        if (workOrder != null) {
            updateObj.setItemId(workOrder.getProductId());
        }
        productReceiptMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductReceipt(Long id) {
        // 校验存在 + 草稿状态
        validateProductReceiptExistsAndDraft(id);

        // 级联删除明细和行
        productReceiptDetailService.deleteProductReceiptDetailByRecptId(id);
        productReceiptLineService.deleteProductReceiptLineByRecptId(id);
        // 删除
        productReceiptMapper.deleteById(id);
    }

    @Override
    public MesWmProductReceiptDO getProductReceipt(Long id) {
        return productReceiptMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmProductReceiptDO> getProductReceiptPage(MesWmProductReceiptPageReqVO pageReqVO) {
        return productReceiptMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitProductReceipt(Long id) {
        // 校验存在 + 草稿状态
        validateProductReceiptExistsAndDraft(id);
        // 校验至少有一条行
        List<MesWmProductReceiptLineDO> lines = productReceiptLineService.getProductReceiptLineListByRecptId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_PRODUCT_RECPT_NO_LINE);
        }

        // 提交（草稿 → 待上架）
        productReceiptMapper.updateById(new MesWmProductReceiptDO()
                .setId(id).setStatus(MesWmProductReceiptStatusEnum.APPROVING.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockProductReceipt(Long id) {
        // 校验存在
        MesWmProductReceiptDO receipt = validateProductReceiptExists(id);
        if (ObjUtil.notEqual(MesWmProductReceiptStatusEnum.APPROVING.getStatus(), receipt.getStatus())) {
            throw exception(WM_PRODUCT_RECPT_STATUS_ERROR);
        }

        // 执行上架（待上架 → 待入库）
        productReceiptMapper.updateById(new MesWmProductReceiptDO()
                .setId(id).setStatus(MesWmProductReceiptStatusEnum.APPROVED.getStatus()));
    }

    @Override
    public Boolean checkProductReceiptQuantity(Long id) {
        List<MesWmProductReceiptLineDO> lines = productReceiptLineService.getProductReceiptLineListByRecptId(id);
        for (MesWmProductReceiptLineDO line : lines) {
            List<MesWmProductReceiptDetailDO> details = productReceiptDetailService.getProductReceiptDetailListByLineId(line.getId());
            BigDecimal totalDetailQty = CollectionUtils.getSumValue(details,
                    MesWmProductReceiptDetailDO::getQuantity, BigDecimal::add, BigDecimal.ZERO);
            if (line.getQuantity() != null && totalDetailQty.compareTo(line.getQuantity()) != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeProductReceipt(Long id) {
        // 校验存在
        MesWmProductReceiptDO receipt = validateProductReceiptExists(id);
        if (ObjUtil.notEqual(MesWmProductReceiptStatusEnum.APPROVED.getStatus(), receipt.getStatus())) {
            throw exception(WM_PRODUCT_RECPT_STATUS_ERROR);
        }

        // 遍历所有明细，更新库存台账
        // TODO @芋艿：【后面优化】
        List<MesWmProductReceiptDetailDO> details = productReceiptDetailService.getProductReceiptDetailListByRecptId(id);
        for (MesWmProductReceiptDetailDO detail : details) {
            materialStockService.increaseStock(
                    detail.getItemId(), detail.getWarehouseId(), detail.getLocationId(), detail.getAreaId(),
                    detail.getBatchId(), detail.getQuantity(), null, null, null);
        }

        // 更新收货单状态
        productReceiptMapper.updateById(new MesWmProductReceiptDO()
                .setId(id).setStatus(MesWmProductReceiptStatusEnum.FINISHED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelProductReceipt(Long id) {
        // 校验存在
        MesWmProductReceiptDO receipt = validateProductReceiptExists(id);
        // 已完成和已取消不允许取消
        if (ObjectUtils.equalsAny(receipt.getStatus(),
                MesWmProductReceiptStatusEnum.FINISHED.getStatus(),
                MesWmProductReceiptStatusEnum.CANCELED.getStatus())) {
            throw exception(WM_PRODUCT_RECPT_CANCEL_NOT_ALLOWED);
        }

        // 取消
        productReceiptMapper.updateById(new MesWmProductReceiptDO()
                .setId(id).setStatus(MesWmProductReceiptStatusEnum.CANCELED.getStatus()));
    }

    @Override
    public MesWmProductReceiptDO validateProductReceiptEditable(Long id) {
        MesWmProductReceiptDO receipt = validateProductReceiptExists(id);
        if (ObjUtil.notEqual(receipt.getStatus(), MesWmProductReceiptStatusEnum.PREPARE.getStatus())
                && ObjUtil.notEqual(receipt.getStatus(), MesWmProductReceiptStatusEnum.APPROVING.getStatus())) {
            throw exception(WM_PRODUCT_RECPT_STATUS_NOT_PREPARE);
        }
        return receipt;
    }

    private MesWmProductReceiptDO validateProductReceiptExists(Long id) {
        MesWmProductReceiptDO receipt = productReceiptMapper.selectById(id);
        if (receipt == null) {
            throw exception(WM_PRODUCT_RECPT_NOT_EXISTS);
        }
        return receipt;
    }

    /**
     * 校验产品收货单存在且为草稿状态
     */
    private MesWmProductReceiptDO validateProductReceiptExistsAndDraft(Long id) {
        MesWmProductReceiptDO receipt = validateProductReceiptExists(id);
        if (ObjUtil.notEqual(MesWmProductReceiptStatusEnum.PREPARE.getStatus(), receipt.getStatus())) {
            throw exception(WM_PRODUCT_RECPT_STATUS_NOT_PREPARE);
        }
        return receipt;
    }

    private void validateCodeUnique(Long id, String code) {
        MesWmProductReceiptDO receipt = productReceiptMapper.selectByCode(code);
        if (receipt == null) {
            return;
        }
        if (ObjUtil.notEqual(id, receipt.getId())) {
            throw exception(WM_PRODUCT_RECPT_CODE_DUPLICATE);
        }
    }

}
