package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 到货通知单状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesWmArrivalNoticeStatusEnum implements ArrayValuable<Integer> {

    PREPARE(0, "草稿"),
    SUBMITTED(1, "已提交"),
    APPROVED(2, "已审批"),
    FINISHED(3, "已完成");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesWmArrivalNoticeStatusEnum::getStatus).toArray(Integer[]::new);

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
