package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 领料出库单状态枚举
 */
@Getter
@AllArgsConstructor
public enum MesWmProductionIssueStatusEnum implements ArrayValuable<Integer> {

    PREPARE(MesOrderStatusConstants.PREPARE, "草稿"),
    FINISHED(MesOrderStatusConstants.FINISHED, "已完成");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesWmProductionIssueStatusEnum::getStatus).toArray(Integer[]::new);

    /**
     * 状态
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
