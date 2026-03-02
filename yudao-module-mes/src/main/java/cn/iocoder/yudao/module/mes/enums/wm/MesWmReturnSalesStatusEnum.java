package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 销售退货单状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesWmReturnSalesStatusEnum implements ArrayValuable<Integer> {

    /**
     * 草稿
     *
     * 对应方法：{@link cn.iocoder.yudao.module.mes.service.wm.returnsales.MesWmReturnSalesService#createReturnSales}
     */
    PREPARE(MesOrderStatusConstants.PREPARE, "草稿"),
    /**
     * 待执行
     *
     * 对应方法：{@link cn.iocoder.yudao.module.mes.service.wm.returnsales.MesWmReturnSalesService#submitReturnSales}
     */
    APPROVING(MesOrderStatusConstants.APPROVING, "待执行"),
    /**
     * 待上架
     *
     * 对应方法：{@link cn.iocoder.yudao.module.mes.service.wm.returnsales.MesWmReturnSalesService#finishReturnSales}
     */
    APPROVED(MesOrderStatusConstants.APPROVED, "待上架"),
    /**
     * 已完成
     *
     * 对应方法：{@link cn.iocoder.yudao.module.mes.service.wm.returnsales.MesWmReturnSalesService#stockReturnSales}
     */
    FINISHED(MesOrderStatusConstants.FINISHED, "已完成"),
    /**
     * 已取消
     *
     * 对应方法：{@link cn.iocoder.yudao.module.mes.service.wm.returnsales.MesWmReturnSalesService#cancelReturnSales}
     */
    CANCELED(MesOrderStatusConstants.CANCELLED, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesWmReturnSalesStatusEnum::getStatus).toArray(Integer[]::new);

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
