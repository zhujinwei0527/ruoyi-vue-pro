package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

// TODO @AI：缺少注释；status + name 这种；
/**
 * MES 盘点任务行状态枚举
 */
@Getter
@AllArgsConstructor
public enum MesWmStockTakingTaskLineStatusEnum implements ArrayValuable<Integer> {

    UNCOUNTED(0, "未盘点"),
    NORMAL(1, "正常"),
    GAIN(2, "盘盈"),
    LOSS(3, "盘亏");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(MesWmStockTakingTaskLineStatusEnum::getStatus).toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
