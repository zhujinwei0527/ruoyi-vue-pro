package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productsales.vo.MesWmProductSalesSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productsales.vo.MesWmProductSalesShippingReqVO;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
import cn.iocoder.yudao.module.mes.service.wm.productsales.MesWmProductSalesService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 销售出库单状态枚举
 *
 * 对应字典 mes_wm_product_sales_status
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesWmProductSalesStatusEnum implements ArrayValuable<Integer> {

    /**
     * 草稿
     *
     * 对应方法：{@link MesWmProductSalesService#createProductSales(MesWmProductSalesSaveReqVO)}
     */
    PREPARE(MesOrderStatusConstants.PREPARE, "草稿"),
    /**
     * 待检测（需要 OQC 检验时），对接 qc 模块的 MesQcOqcDO 后续流程
     *
     * 对应方法：{@link MesWmProductSalesService#submitProductSales(Long)}
     */
    CONFIRMED(MesOrderStatusConstants.CONFIRMED, "待检测"),
    /**
     * 待拣货
     *
     * 对应方法：
     * 1. 不需要 OQC 检验时：{@link MesWmProductSalesService#submitProductSales(Long)}
     * 2. 或 OQC 检验完成时：{@link MesWmProductSalesService#confirmProductSales(Long)}
     */
    APPROVING(MesOrderStatusConstants.APPROVING, "待拣货"),
    /**
     * 待填写运单
     *
     * 对应方法：{@link MesWmProductSalesService#shippingProductSales(MesWmProductSalesShippingReqVO)}
     */
    SHIPPING(10, "待填写运单"), // 10 是一个特殊的状态值，不在 MesOrderStatusConstants 中，单独定义
    /**
     * 待执行出库
     *
     * 对应方法：{@link MesWmProductSalesService#stockProductSales(Long)}
     */
    APPROVED(MesOrderStatusConstants.APPROVED, "待执行出库"),
    /**
     * 已完成
     *
     * 对应方法：{@link MesWmProductSalesService#finishProductSales(Long)}
     */
    FINISHED(MesOrderStatusConstants.FINISHED, "已完成"),
    /**
     * 已取消
     *
     * 对应方法：{@link MesWmProductSalesService#cancelProductSales(Long)}
     */
    CANCELED(MesOrderStatusConstants.CANCELLED, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesWmProductSalesStatusEnum::getStatus).toArray(Integer[]::new);

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
