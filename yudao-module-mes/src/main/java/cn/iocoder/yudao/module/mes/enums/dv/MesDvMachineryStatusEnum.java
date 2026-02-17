package cn.iocoder.yudao.module.mes.enums.dv;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 设备状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesDvMachineryStatusEnum implements ArrayValuable<Integer> {

    RUNNING(1, "运行中"),
    STOP(2, "停机"),
    FAULT(3, "故障");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesDvMachineryStatusEnum::getStatus).toArray(Integer[]::new);

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
