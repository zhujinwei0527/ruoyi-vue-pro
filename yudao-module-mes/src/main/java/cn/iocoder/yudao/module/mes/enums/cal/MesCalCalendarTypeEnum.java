package cn.iocoder.yudao.module.mes.enums.cal;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 班组类型（日历类型）枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesCalCalendarTypeEnum implements ArrayValuable<Integer> {

    GROUP_ROTATION(1, "分组轮班"),
    FIXED_TEAM(2, "固定班组");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesCalCalendarTypeEnum::getType).toArray(Integer[]::new);

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
