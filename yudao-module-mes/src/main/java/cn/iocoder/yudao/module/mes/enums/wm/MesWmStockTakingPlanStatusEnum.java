package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

// TODO @AI：缺少注释；status + name 这种；
/**
 * MES 盘点方案状态枚举
 */
@Getter
@AllArgsConstructor
public enum MesWmStockTakingPlanStatusEnum implements ArrayValuable<Integer> {

    PREPARE(MesOrderStatusConstants.PREPARE, "草稿"),
    CONFIRMED(MesOrderStatusConstants.CONFIRMED, "已确认");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(MesWmStockTakingPlanStatusEnum::getStatus).toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
