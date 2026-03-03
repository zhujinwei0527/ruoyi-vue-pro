package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 委外收货单状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesWmOutsourceReceiptStatusEnum implements ArrayValuable<Integer> {

    // TODO @AI：需要 @对应的状态；参考 /Users/yunai/Java/yudao-all-in-one/ruoyi-vue-pro/yudao-module-mes/src/main/java/cn/iocoder/yudao/module/mes/enums/wm/MesWmMiscIssueStatusEnum.java 里的逻辑；
    /**
     * 准备中
     */
    PREPARE(MesOrderStatusConstants.PREPARE, "准备中"),
    /**
     * 审批中
     */
    APPROVING(MesOrderStatusConstants.APPROVING, "审批中"),
    /**
     * 已审批
     */
    APPROVED(MesOrderStatusConstants.APPROVED, "已审批"),
    /**
     * 已完成
     */
    FINISHED(MesOrderStatusConstants.FINISHED, "已完成"),
    /**
     * 已取消
     */
    CANCELED(MesOrderStatusConstants.CANCELLED, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesWmOutsourceReceiptStatusEnum::getStatus).toArray(Integer[]::new);

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
