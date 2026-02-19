package cn.iocoder.yudao.module.mes.enums.pro;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 工序关系类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesProLinkTypeEnum implements ArrayValuable<Integer> {

    // TODO @AI：START_START，类似这样的缩写；
    SS(0, "开始-开始"),
    FF(1, "结束-结束"),
    SF(2, "开始-结束"),
    FS(3, "结束-开始");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesProLinkTypeEnum::getType).toArray(Integer[]::new);

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
