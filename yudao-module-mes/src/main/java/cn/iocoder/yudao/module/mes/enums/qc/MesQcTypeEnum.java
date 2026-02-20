package cn.iocoder.yudao.module.mes.enums.qc;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 检测种类枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesQcTypeEnum implements ArrayValuable<Integer> {

    IQC(1, "来料检验"),
    IPQC(2, "过程检验"),
    OQC(3, "出货检验"),
    RQC(4, "退货检验");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesQcTypeEnum::getType).toArray(Integer[]::new);

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
