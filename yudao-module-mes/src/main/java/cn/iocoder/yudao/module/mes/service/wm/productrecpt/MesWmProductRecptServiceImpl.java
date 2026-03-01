package cn.iocoder.yudao.module.mes.service.wm.productrecpt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.MesWmProductRecptPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.MesWmProductRecptSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt.MesWmProductRecptDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt.MesWmProductRecptDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt.MesWmProductRecptLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productrecpt.MesWmProductRecptMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmProductRecptStatusEnum;
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
public class MesWmProductRecptServiceImpl implements MesWmProductRecptService {

    @Resource
    private MesWmProductRecptMapper productRecptMapper;

    @Resource
    private MesWmProductRecptLineService productRecptLineService;

    @Resource
    private MesWmProductRecptDetailService productRecptDetailService;

    @Resource
    private MesWmMaterialStockService materialStockService;

    @Resource
    private cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService workOrderService;

    @Override
    public Long createProductRecpt(MesWmProductRecptSaveReqVO createReqVO) {
        // 校验编码唯一
        validateCodeUnique(null, createReqVO.getCode());
        // 校验工单存在并设置 itemId
        MesProWorkOrderDO workOrder = createReqVO.getWorkOrderId() != null ?
                workOrderService.validateWorkOrderExists(createReqVO.getWorkOrderId()) : null;

        // 插入
        MesWmProductRecptDO recpt = BeanUtils.toBean(createReqVO, MesWmProductRecptDO.class);
        if (workOrder != null) {
            recpt.setItemId(workOrder.getProductId());
        }
        recpt.setStatus(MesWmProductRecptStatusEnum.PREPARE.getStatus());
        productRecptMapper.insert(recpt);
        return recpt.getId();
    }

    @Override
    public void updateProductRecpt(MesWmProductRecptSaveReqVO updateReqVO) {
        // 校验存在 + 草稿状态
        validateProductRecptExistsAndDraft(updateReqVO.getId());
        // 校验编码唯一
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 校验工单存在
        MesProWorkOrderDO workOrder = updateReqVO.getWorkOrderId() != null ?
                workOrderService.validateWorkOrderExists(updateReqVO.getWorkOrderId()) : null;

        // 更新
        MesWmProductRecptDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductRecptDO.class);
        if (workOrder != null) {
            updateObj.setItemId(workOrder.getProductId());
        }
        productRecptMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductRecpt(Long id) {
        // 校验存在 + 草稿状态
        validateProductRecptExistsAndDraft(id);

        // 级联删除明细和行
        productRecptDetailService.deleteProductRecptDetailByRecptId(id);
        productRecptLineService.deleteProductRecptLineByRecptId(id);
        // 删除
        productRecptMapper.deleteById(id);
    }

    @Override
    public MesWmProductRecptDO getProductRecpt(Long id) {
        return productRecptMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmProductRecptDO> getProductRecptPage(MesWmProductRecptPageReqVO pageReqVO) {
        return productRecptMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitProductRecpt(Long id) {
        // 校验存在 + 草稿状态
        validateProductRecptExistsAndDraft(id);
        // 校验至少有一条行
        List<MesWmProductRecptLineDO> lines = productRecptLineService.getProductRecptLineListByRecptId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_PRODUCT_RECPT_NO_LINE);
        }

        // 提交（草稿 → 待上架）
        productRecptMapper.updateById(new MesWmProductRecptDO()
                .setId(id).setStatus(MesWmProductRecptStatusEnum.APPROVING.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockProductRecpt(Long id) {
        // 校验存在
        MesWmProductRecptDO recpt = validateProductRecptExists(id);
        if (ObjUtil.notEqual(MesWmProductRecptStatusEnum.APPROVING.getStatus(), recpt.getStatus())) {
            throw exception(WM_PRODUCT_RECPT_STATUS_ERROR);
        }

        // 执行上架（待上架 → 待入库）
        productRecptMapper.updateById(new MesWmProductRecptDO()
                .setId(id).setStatus(MesWmProductRecptStatusEnum.APPROVED.getStatus()));
    }

    @Override
    public Boolean checkProductRecptQuantity(Long id) {
        List<MesWmProductRecptLineDO> lines = productRecptLineService.getProductRecptLineListByRecptId(id);
        for (MesWmProductRecptLineDO line : lines) {
            List<MesWmProductRecptDetailDO> details = productRecptDetailService.getProductRecptDetailListByLineId(line.getId());
            BigDecimal totalDetailQty = CollectionUtils.getSumValue(details,
                    MesWmProductRecptDetailDO::getQuantity, BigDecimal::add, BigDecimal.ZERO);
            if (line.getQuantity() != null && totalDetailQty.compareTo(line.getQuantity()) != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeProductRecpt(Long id) {
        // 校验存在
        MesWmProductRecptDO recpt = validateProductRecptExists(id);
        if (ObjUtil.notEqual(MesWmProductRecptStatusEnum.APPROVED.getStatus(), recpt.getStatus())) {
            throw exception(WM_PRODUCT_RECPT_STATUS_ERROR);
        }

        // 遍历所有明细，更新库存台账
        // TODO @芋艿：【后面优化】
        List<MesWmProductRecptDetailDO> details = productRecptDetailService.getProductRecptDetailListByRecptId(id);
        for (MesWmProductRecptDetailDO detail : details) {
            materialStockService.increaseStock(
                    detail.getItemId(), detail.getWarehouseId(), detail.getLocationId(), detail.getAreaId(),
                    detail.getBatchId(), detail.getQuantity(), null, null, null);
        }

        // 更新收货单状态
        productRecptMapper.updateById(new MesWmProductRecptDO()
                .setId(id).setStatus(MesWmProductRecptStatusEnum.FINISHED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelProductRecpt(Long id) {
        // 校验存在
        MesWmProductRecptDO recpt = validateProductRecptExists(id);
        // 已完成和已取消不允许取消
        if (ObjectUtils.equalsAny(recpt.getStatus(),
                MesWmProductRecptStatusEnum.FINISHED.getStatus(),
                MesWmProductRecptStatusEnum.CANCELED.getStatus())) {
            throw exception(WM_PRODUCT_RECPT_CANCEL_NOT_ALLOWED);
        }

        // 取消
        productRecptMapper.updateById(new MesWmProductRecptDO()
                .setId(id).setStatus(MesWmProductRecptStatusEnum.CANCELED.getStatus()));
    }

    @Override
    public MesWmProductRecptDO validateProductRecptEditable(Long id) {
        MesWmProductRecptDO recpt = validateProductRecptExists(id);
        if (ObjUtil.notEqual(recpt.getStatus(), MesWmProductRecptStatusEnum.PREPARE.getStatus())
                && ObjUtil.notEqual(recpt.getStatus(), MesWmProductRecptStatusEnum.APPROVING.getStatus())) {
            throw exception(WM_PRODUCT_RECPT_STATUS_NOT_PREPARE);
        }
        return recpt;
    }

    private MesWmProductRecptDO validateProductRecptExists(Long id) {
        MesWmProductRecptDO recpt = productRecptMapper.selectById(id);
        if (recpt == null) {
            throw exception(WM_PRODUCT_RECPT_NOT_EXISTS);
        }
        return recpt;
    }

    /**
     * 校验产品收货单存在且为草稿状态
     */
    private MesWmProductRecptDO validateProductRecptExistsAndDraft(Long id) {
        MesWmProductRecptDO recpt = validateProductRecptExists(id);
        if (ObjUtil.notEqual(MesWmProductRecptStatusEnum.PREPARE.getStatus(), recpt.getStatus())) {
            throw exception(WM_PRODUCT_RECPT_STATUS_NOT_PREPARE);
        }
        return recpt;
    }

    private void validateCodeUnique(Long id, String code) {
        MesWmProductRecptDO recpt = productRecptMapper.selectByCode(code);
        if (recpt == null) {
            return;
        }
        if (ObjUtil.notEqual(id, recpt.getId())) {
            throw exception(WM_PRODUCT_RECPT_CODE_DUPLICATE);
        }
    }

}
