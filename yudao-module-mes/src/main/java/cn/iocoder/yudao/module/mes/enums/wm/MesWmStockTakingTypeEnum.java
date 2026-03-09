package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

// TODO @AI：缺少注释；type + name 这种；
/**
 * MES 盘点类型枚举
 */
@Getter
@AllArgsConstructor
public enum MesWmStockTakingTypeEnum implements ArrayValuable<Integer> {

    // TODO @AI：只有 2 种：动态盘点、静态盘点；按照这个顺序枚举；
    OPEN(1, "开放盘点"),
    CYCLE(2, "循环盘点"),
    DYNAMIC(3, "动态盘点");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(MesWmStockTakingTypeEnum::getType).toArray(Integer[]::new);

    private final Integer type;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
