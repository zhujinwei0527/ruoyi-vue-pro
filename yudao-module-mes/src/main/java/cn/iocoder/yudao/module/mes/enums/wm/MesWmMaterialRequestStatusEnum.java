package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

// TODO @AI：这里需要迁移下；
/**
 * MES 领料申请单状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesWmMaterialRequestStatusEnum implements ArrayValuable<Integer> {

    PREPARE(MesOrderStatusConstants.PREPARE, "草稿"),
    PREPARING(MesOrderStatusConstants.APPROVING, "备料中"),
    WAITING(MesOrderStatusConstants.APPROVED, "待领料"),
    FINISHED(MesOrderStatusConstants.FINISHED, "已完成"),
    CANCELED(MesOrderStatusConstants.CANCELLED, "已取消");

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
