package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 销售出库单状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesWmProductSalesStatusEnum implements ArrayValuable<Integer> {

    /**
     * 草稿
     */
    PREPARE(MesOrderStatusConstants.PREPARE, "草稿"),
    /**
     * 待拣货
     */
    APPROVING(MesOrderStatusConstants.APPROVING, "待拣货"),
    /**
     * 待出库
     */
    APPROVED(MesOrderStatusConstants.APPROVED, "待出库"),
    /**
     * 已完成
     */
    FINISHED(MesOrderStatusConstants.FINISHED, "已完成"),
    /**
     * 已取消
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
