package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

// TODO @AI：应该使用 MesOrderStatusEnum 替代掉，只是使用其中的部分状态。需要在对应的 DO 说明下；当然，字典还是独立的！
/**
 * MES 采购入库单状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesWmItemReceiptStatusEnum implements ArrayValuable<Integer> {

    PREPARE(0, "草稿"),
    APPROVING(1, "待上架"),
    APPROVED(2, "待入库"),
    FINISHED(3, "已完成"),
    CANCELED(4, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesWmItemReceiptStatusEnum::getStatus).toArray(Integer[]::new);

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
