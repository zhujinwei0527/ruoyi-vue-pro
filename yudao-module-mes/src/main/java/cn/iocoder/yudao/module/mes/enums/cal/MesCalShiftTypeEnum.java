package cn.iocoder.yudao.module.mes.enums.cal;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 轮班方式枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesCalShiftTypeEnum implements ArrayValuable<Integer> {

    SINGLE(1, "单白班"),
    TWO(2, "两班倒"),
    THREE(3, "三班倒");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesCalShiftTypeEnum::getType).toArray(Integer[]::new);

    /**
     * 类型值
     */
    private final Integer type;
    /**
     * 类型名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
