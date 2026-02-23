package cn.iocoder.yudao.module.mes.enums;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 单据状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesOrderStatusEnum implements ArrayValuable<Integer> {

    DRAFT(0, "草稿"),
    CONFIRMED(1, "已确认"),
    APPROVING(2, "审批中"),
    APPROVED(3, "已审批"),
    FINISHED(4, "已完成"),
    CANCELLED(5, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesOrderStatusEnum::getType).toArray(Integer[]::new);

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
