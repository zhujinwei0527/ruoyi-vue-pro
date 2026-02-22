package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 领料申请单状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesWmMaterialRequestStatusEnum implements ArrayValuable<Integer> {

    PREPARE(0, "草稿"),
    PREPARING(1, "备料中"),
    WAITING(2, "待领料"),
    FINISHED(3, "已完成"),
    CANCELED(4, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesWmMaterialRequestStatusEnum::getStatus).toArray(Integer[]::new);

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
