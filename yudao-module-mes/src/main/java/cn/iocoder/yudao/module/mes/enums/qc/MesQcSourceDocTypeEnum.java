package cn.iocoder.yudao.module.mes.enums.qc;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.enums.MesBizTypeConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 质检来源单据类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesQcSourceDocTypeEnum implements ArrayValuable<Integer> {

    ARRIVAL_NOTICE(MesBizTypeConstants.WM_ARRIVAL_NOTICE, "到货通知单"),
    OUTSOURCE_RECPT(MesBizTypeConstants.WM_OUTSOURCE_RECPT, "外协入库单"),
    FEEDBACK(MesBizTypeConstants.PRO_FEEDBACK, "生产报工"),
    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesQcSourceDocTypeEnum::getType).toArray(Integer[]::new);

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
