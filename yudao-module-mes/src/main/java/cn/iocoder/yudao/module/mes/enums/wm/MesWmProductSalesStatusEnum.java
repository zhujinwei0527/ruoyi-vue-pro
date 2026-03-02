package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productsales.vo.MesWmProductSalesSaveReqVO;
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

    // （草稿）提交 =》（待拣货）执行拣货 =》（待填写运单）填写运单；=》（待执行出库）执行出库=》（已完成）

    /**
     * 草稿
     *
     * 对应方法：{@link MesWmProductSalesService#createProductSales(MesWmProductSalesSaveReqVO)}
     */
    PREPARE(MesOrderStatusConstants.PREPARE, "草稿"),
    /**
     * 待拣货
     *
     * 对应方法：{@link MesWmProductSalesService#submitProductSales(Long)}
     */
    APPROVING(MesOrderStatusConstants.APPROVING, "待拣货"),
    // TODO @AI：在 APPROVING、APPROVED 之间加一个 10，SHIPPING，待填写运单；执行填写运单方法：{@link MesWmProductSalesService#shippingProductSales(Long)}
    // TODO @AI：pick 这个方法要改成 stock；
    /**
     * 待执行出库
     *
     * 对应方法：{@link MesWmProductSalesService#pickProductSales(Long)}
     */
    APPROVED(MesOrderStatusConstants.APPROVED, "待执行出库"),
    /**
     * 已完成
     *
     * 对应方法：{@link MesWmProductSalesService#executeProductSales(Long)}
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
