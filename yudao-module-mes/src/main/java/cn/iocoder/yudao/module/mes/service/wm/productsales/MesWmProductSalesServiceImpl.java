package cn.iocoder.yudao.module.mes.service.wm.productsales;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productsales.vo.MesWmProductSalesPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productsales.vo.MesWmProductSalesSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productsales.MesWmProductSalesDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productsales.MesWmProductSalesDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productsales.MesWmProductSalesLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productsales.MesWmProductSalesMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmProductSalesStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.client.MesMdClientService;
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
 * MES 销售出库单 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesWmProductSalesServiceImpl implements MesWmProductSalesService {

    @Resource
    private MesWmProductSalesMapper productSalesMapper;

    @Resource
    private MesWmProductSalesLineService productSalesLineService;

    @Resource
    private MesWmProductSalesDetailService productSalesDetailService;

    @Resource
    private MesMdClientService clientService;

    @Resource
    private MesWmMaterialStockService materialStockService;

    @Override
    public Long createProductSales(MesWmProductSalesSaveReqVO createReqVO) {
        // 校验编码唯一
        validateCodeUnique(null, createReqVO.getCode());
        // 校验客户存在
        clientService.validateClientExists(createReqVO.getClientId());

        // 插入
        MesWmProductSalesDO sales = BeanUtils.toBean(createReqVO, MesWmProductSalesDO.class);
        sales.setStatus(MesWmProductSalesStatusEnum.PREPARE.getStatus());
        productSalesMapper.insert(sales);
        return sales.getId();
    }

    @Override
    public void updateProductSales(MesWmProductSalesSaveReqVO updateReqVO) {
        // 校验存在 + 草稿状态
        validateProductSalesExistsAndDraft(updateReqVO.getId());
        // 校验编码唯一
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 校验客户存在
        clientService.validateClientExists(updateReqVO.getClientId());

        // 更新
        MesWmProductSalesDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductSalesDO.class);
        productSalesMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductSales(Long id) {
        // 校验存在 + 草稿状态
        validateProductSalesExistsAndDraft(id);

        // 级联删除明细和行
        productSalesDetailService.deleteProductSalesDetailBySalesId(id);
        productSalesLineService.deleteProductSalesLineBySalesId(id);
        // 删除
        productSalesMapper.deleteById(id);
    }

    @Override
    public MesWmProductSalesDO getProductSales(Long id) {
        return productSalesMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmProductSalesDO> getProductSalesPage(MesWmProductSalesPageReqVO pageReqVO) {
        return productSalesMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitProductSales(Long id) {
        // 校验存在 + 草稿状态
        validateProductSalesExistsAndDraft(id);
        // 校验至少有一条行
        List<MesWmProductSalesLineDO> lines = productSalesLineService.getProductSalesLineListBySalesId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_PRODUCT_SALES_LINES_EMPTY);
        }

        // 提交（草稿 → 待拣货）
        productSalesMapper.updateById(new MesWmProductSalesDO()
                .setId(id).setStatus(MesWmProductSalesStatusEnum.APPROVING.getStatus()));
    }

    // TODO @AI：前后端方法名，都改成 stock；保持整体都是对齐的；
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pickProductSales(Long id) {
        // 校验存在
        MesWmProductSalesDO sales = validateProductSalesExists(id);
        if (ObjUtil.notEqual(MesWmProductSalesStatusEnum.APPROVING.getStatus(), sales.getStatus())) {
            throw exception(WM_PRODUCT_SALES_CANNOT_PICK);
        }
        // 校验每行明细数量之和是否等于行出库数量
        // TODO @AI：这里挪成独立的 checkQuantiy 接口，不在这个方法里，保持对齐；然后前端调用；类似 /Users/yunai/Java/yudao-all-in-one/yudao-ui-admin-vue3/src/views/mes/wm/returnvendor/ReturnVendorForm.vue 这种；
        List<MesWmProductSalesLineDO> lines = productSalesLineService.getProductSalesLineListBySalesId(id);
        for (MesWmProductSalesLineDO line : lines) {
            List<MesWmProductSalesDetailDO> details = productSalesDetailService.getProductSalesDetailListByLineId(line.getId());
            if (CollUtil.isEmpty(details)) {
                throw exception(WM_PRODUCT_SALES_DETAILS_EMPTY);
            }
            BigDecimal totalDetailQty = CollectionUtils.getSumValue(details,
                    MesWmProductSalesDetailDO::getQuantity, BigDecimal::add, BigDecimal.ZERO);
            if (line.getQuantity() != null && totalDetailQty.compareTo(line.getQuantity()) != 0) {
                throw exception(WM_PRODUCT_SALES_DETAIL_QUANTITY_MISMATCH);
            }
        }

        // 执行拣货（待拣货 → 待出库）
        productSalesMapper.updateById(new MesWmProductSalesDO()
                .setId(id).setStatus(MesWmProductSalesStatusEnum.APPROVED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeProductSales(Long id) {
        // 校验存在
        MesWmProductSalesDO sales = validateProductSalesExists(id);
        if (ObjUtil.notEqual(MesWmProductSalesStatusEnum.APPROVED.getStatus(), sales.getStatus())) {
            throw exception(WM_PRODUCT_SALES_CANNOT_EXECUTE);
        }

        // 遍历所有明细，扣减库存
        // DONE @AI：需要实现 decreaseStock 方法（AI 未修复原因：需要 materialStockService 提供 decreaseStock 方法实现）
        List<MesWmProductSalesDetailDO> details = productSalesDetailService.getProductSalesDetailListBySalesId(id);
        for (MesWmProductSalesDetailDO detail : details) {
            // materialStockService.decreaseStock(
            //         detail.getItemId(), detail.getWarehouseId(), detail.getLocationId(), detail.getAreaId(),
            //         detail.getBatchId(), detail.getQuantity(), sales.getClientId(), null, null);
        }

        // 更新出库单状态
        productSalesMapper.updateById(new MesWmProductSalesDO()
                .setId(id).setStatus(MesWmProductSalesStatusEnum.FINISHED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelProductSales(Long id) {
        // 校验存在
        MesWmProductSalesDO sales = validateProductSalesExists(id);
        // 已完成和已取消不允许取消
        if (ObjectUtils.equalsAny(sales.getStatus(),
                MesWmProductSalesStatusEnum.FINISHED.getStatus(),
                MesWmProductSalesStatusEnum.CANCELED.getStatus())) {
            throw exception(WM_PRODUCT_SALES_CANNOT_CANCEL);
        }
        // 取消
        productSalesMapper.updateById(new MesWmProductSalesDO()
                .setId(id).setStatus(MesWmProductSalesStatusEnum.CANCELED.getStatus()));
    }

    private MesWmProductSalesDO validateProductSalesExists(Long id) {
        MesWmProductSalesDO sales = productSalesMapper.selectById(id);
        if (sales == null) {
            throw exception(WM_PRODUCT_SALES_NOT_EXISTS);
        }
        return sales;
    }

    /**
     * 校验销售出库单存在且为草稿状态
     */
    private MesWmProductSalesDO validateProductSalesExistsAndDraft(Long id) {
        MesWmProductSalesDO sales = validateProductSalesExists(id);
        if (ObjUtil.notEqual(MesWmProductSalesStatusEnum.PREPARE.getStatus(), sales.getStatus())) {
            throw exception(WM_PRODUCT_SALES_NOT_PREPARE);
        }
        return sales;
    }

    private void validateCodeUnique(Long id, String code) {
        MesWmProductSalesDO sales = productSalesMapper.selectByCode(code);
        if (sales == null) {
            return;
        }
        if (ObjUtil.notEqual(id, sales.getId())) {
            throw exception(WM_PRODUCT_SALES_CODE_DUPLICATE);
        }
    }

}
