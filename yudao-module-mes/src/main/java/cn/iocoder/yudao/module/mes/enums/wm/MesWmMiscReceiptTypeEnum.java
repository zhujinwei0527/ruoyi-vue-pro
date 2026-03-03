package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 杂项入库类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesWmMiscReceiptTypeEnum implements ArrayValuable<String> {

    RETURN("RETURN", "退料入库"),
    ADJUST("ADJUST", "调整入库"),
    TRANSFER("TRANSFER", "调拨入库"),
    OTHER("OTHER", "其他入库");

    public static final String[] ARRAYS = Arrays.stream(values()).map(MesWmMiscReceiptTypeEnum::getType).toArray(String[]::new);

    /**
     * 类型值
     */
    private final String type;
    /**
     * 类型名
     */
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }

}
