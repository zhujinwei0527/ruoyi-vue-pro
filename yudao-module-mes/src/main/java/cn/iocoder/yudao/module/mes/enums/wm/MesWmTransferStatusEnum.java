package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 转移单状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesWmTransferStatusEnum implements ArrayValuable<Integer> {

    // TODO @AI：/Users/yunai/Java/yudao-all-in-one/ruoyi-vue-pro/yudao-module-mes/src/main/java/cn/iocoder/yudao/module/mes/enums/wm/MesWmOutsourceReceiptStatusEnum.java，增加 “对应方法”
    /**
     * 草稿
     */
    PREPARE(MesOrderStatusConstants.PREPARE, "草稿"),
    /**
     * 待确认（仅配送模式）
     */
    UNCONFIRMED(MesOrderStatusConstants.CONFIRMED, "待确认"),
    /**
     * 待上架
     */
    UNSTOCK(MesOrderStatusConstants.APPROVING, "待上架"),
    /**
     * 待执行
     */
    UNEXECUTE(MesOrderStatusConstants.APPROVED, "待执行"),
    /**
     * 已完成
     */
    FINISHED(MesOrderStatusConstants.FINISHED, "已完成"),
    /**
     * 已取消
     */
    CANCELED(MesOrderStatusConstants.CANCELLED, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesWmTransferStatusEnum::getStatus).toArray(Integer[]::new);

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
