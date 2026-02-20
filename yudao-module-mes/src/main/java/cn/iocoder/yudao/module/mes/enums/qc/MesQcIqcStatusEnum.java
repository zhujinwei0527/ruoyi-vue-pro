package cn.iocoder.yudao.module.mes.enums.qc;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 检验单状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesQcIqcStatusEnum implements ArrayValuable<Integer> {

    PREPARE(0, "草稿"),
    FINISHED(1, "已完成");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesQcIqcStatusEnum::getType).toArray(Integer[]::new);

    /**
     * 状态值
     */
    private final Integer type;
    /**
     * 状态名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
