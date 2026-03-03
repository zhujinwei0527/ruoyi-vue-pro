package cn.iocoder.yudao.module.mes.service.wm.miscreceipt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.miscreceipt.vo.MesWmMiscReceiptPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.miscreceipt.vo.MesWmMiscReceiptSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.miscreceipt.MesWmMiscReceiptDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.miscreceipt.MesWmMiscReceiptLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.miscreceipt.MesWmMiscReceiptLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.miscreceipt.MesWmMiscReceiptMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmMiscReceiptStatusEnum;
import cn.iocoder.yudao.module.mes.service.wm.materialstock.MesWmMaterialStockService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 杂项入库单 Service 实现类
 */
@Service
@Validated
@Slf4j
public class MesWmMiscReceiptServiceImpl implements MesWmMiscReceiptService {

    @Resource
    private MesWmMiscReceiptMapper miscReceiptMapper;

    @Resource
    private MesWmMiscReceiptLineMapper miscReceiptLineMapper;

    @Resource
    private MesWmMiscReceiptDetailService miscReceiptDetailService;

    @Resource
    private MesWmMaterialStockService materialStockService;

    @Override
    public Long createMiscReceipt(MesWmMiscReceiptSaveReqVO createReqVO) {
        // 校验编码唯一
        validateCodeUnique(null, createReqVO.getCode());

        // 插入
        MesWmMiscReceiptDO receipt = BeanUtils.toBean(createReqVO, MesWmMiscReceiptDO.class);
        receipt.setStatus(MesWmMiscReceiptStatusEnum.PREPARE.getStatus());
        miscReceiptMapper.insert(receipt);
        return receipt.getId();
    }

    @Override
    public void updateMiscReceipt(MesWmMiscReceiptSaveReqVO updateReqVO) {
        // 校验存在 + 草稿状态
        validateMiscReceiptExistsAndPrepare(updateReqVO.getId());
        // 校验编码唯一
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 更新
        MesWmMiscReceiptDO updateObj = BeanUtils.toBean(updateReqVO, MesWmMiscReceiptDO.class);
        miscReceiptMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMiscReceipt(Long id) {
        // 校验存在 + 草稿状态
        validateMiscReceiptExistsAndPrepare(id);

        // 级联删除明细
        miscReceiptDetailService.deleteMiscReceiptDetailByReceiptId(id);
        // 级联删除行
        miscReceiptLineMapper.deleteByReceiptId(id);
        // 删除主表
        miscReceiptMapper.deleteById(id);
    }

    @Override
    public MesWmMiscReceiptDO getMiscReceipt(Long id) {
        return miscReceiptMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmMiscReceiptDO> getMiscReceiptPage(MesWmMiscReceiptPageReqVO pageReqVO) {
        return miscReceiptMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitMiscReceipt(Long id) {
        // 校验存在 + 草稿状态
        validateMiscReceiptExistsAndPrepare(id);
        // 校验至少有一条行
        List<MesWmMiscReceiptLineDO> lines = miscReceiptLineMapper.selectListByReceiptId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_MISC_RECEIPT_NO_LINE);
        }

        // 提交审批（草稿 → 已审批）
        miscReceiptMapper.updateById(new MesWmMiscReceiptDO()
                .setId(id).setStatus(MesWmMiscReceiptStatusEnum.APPROVED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishMiscReceipt(Long id) {
        // 校验存在
        MesWmMiscReceiptDO receipt = validateMiscReceiptExists(id);
        if (ObjUtil.notEqual(MesWmMiscReceiptStatusEnum.APPROVED.getStatus(), receipt.getStatus())) {
            throw exception(WM_MISC_RECEIPT_STATUS_NOT_APPROVED);
        }

        // 遍历所有行，更新库存台账
        List<MesWmMiscReceiptLineDO> lines = miscReceiptLineMapper.selectListByReceiptId(id);
        for (MesWmMiscReceiptLineDO line : lines) {
            materialStockService.increaseStock(
                    line.getItemId(),
                    line.getWarehouseId(),
                    line.getLocationId(),
                    line.getAreaId(),
                    null, // batchId 传 null（杂项入库使用 batchCode）
                    line.getQuantity(),
                    null, // vendorId 传 null（杂项入库无供应商）
                    line.getProductionDate(),
                    line.getExpireDate()
            );
        }

        // 执行入库（已审批 → 已完成）
        miscReceiptMapper.updateById(new MesWmMiscReceiptDO()
                .setId(id).setStatus(MesWmMiscReceiptStatusEnum.FINISHED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelMiscReceipt(Long id) {
        // 校验存在
        MesWmMiscReceiptDO receipt = validateMiscReceiptExists(id);

        // 已完成和已取消状态不允许取消
        if (ObjUtil.equal(MesWmMiscReceiptStatusEnum.FINISHED.getStatus(), receipt.getStatus())
                || ObjUtil.equal(MesWmMiscReceiptStatusEnum.CANCELED.getStatus(), receipt.getStatus())) {
            throw exception(WM_MISC_RECEIPT_CANCEL_NOT_ALLOWED);
        }

        // 取消
        miscReceiptMapper.updateById(new MesWmMiscReceiptDO()
                .setId(id).setStatus(MesWmMiscReceiptStatusEnum.CANCELED.getStatus()));
    }

    @Override
    public MesWmMiscReceiptDO validateMiscReceiptExists(Long id) {
        MesWmMiscReceiptDO receipt = miscReceiptMapper.selectById(id);
        if (receipt == null) {
            throw exception(WM_MISC_RECEIPT_NOT_EXISTS);
        }
        return receipt;
    }

    private void validateCodeUnique(Long id, String code) {
        MesWmMiscReceiptDO receipt = miscReceiptMapper.selectByCode(code);
        if (receipt == null) {
            return;
        }
        if (id == null || ObjUtil.notEqual(id, receipt.getId())) {
            throw exception(WM_MISC_RECEIPT_CODE_DUPLICATE);
        }
    }

    private MesWmMiscReceiptDO validateMiscReceiptExistsAndPrepare(Long id) {
        MesWmMiscReceiptDO receipt = validateMiscReceiptExists(id);
        if (ObjUtil.notEqual(MesWmMiscReceiptStatusEnum.PREPARE.getStatus(), receipt.getStatus())) {
            throw exception(WM_MISC_RECEIPT_STATUS_NOT_PREPARE);
        }
        return receipt;
    }

    /**
     * 校验入库单是否可编辑（存在且为草稿状态）
     *
     * @param id 入库单ID
     */
    public void validateMiscReceiptEditable(Long id) {
        validateMiscReceiptExistsAndPrepare(id);
    }

}
