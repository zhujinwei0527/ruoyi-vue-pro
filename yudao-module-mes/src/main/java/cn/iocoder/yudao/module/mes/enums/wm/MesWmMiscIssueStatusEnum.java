package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 杂项出库单状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesWmMiscIssueStatusEnum implements ArrayValuable<Integer> {

    // TODO @AI：/Users/yunai/Java/yudao-all-in-one/ruoyi-vue-pro/yudao-module-mes/src/main/java/cn/iocoder/yudao/module/mes/enums/wm/MesWmProductSalesStatusEnum.java，关联 orderstatus；然后有“对应方法”
    /**
     * 草稿
     */
    PREPARE(0, "草稿"),
    /**
     * 待执行出库
     */
    APPROVED(1, "待执行出库"),
    /**
     * 已完成
     */
    FINISHED(2, "已完成"),
    /**
     * 已取消
     */
    CANCELED(3, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesWmMiscIssueStatusEnum::getStatus).toArray(Integer[]::new);

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
