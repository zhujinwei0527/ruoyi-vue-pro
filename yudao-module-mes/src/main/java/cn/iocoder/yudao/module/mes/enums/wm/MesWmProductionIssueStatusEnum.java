package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

// TODO @AI：需要使用 status constants 去映射；
/**
 * MES 领料出库单状态枚举
 */
@Getter
@AllArgsConstructor
public enum MesWmProductionIssueStatusEnum implements ArrayValuable<Integer> {

    PREPARE(10, "准备中"),
    APPROVED(20, "已审批"),
    FINISHED(30, "已完成");

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
