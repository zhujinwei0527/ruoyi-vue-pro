package cn.iocoder.yudao.module.mes.enums.pro;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 生产报工状态枚举
 *
 * 状态流转：
 * 1. 创建 → 草稿（PREPARE）
 * 2. 草稿 → 提交 → 审批中（APPROVING）
 * 3. 审批中 → 驳回 → 草稿（PREPARE）
 * 4. 审批中 → 执行 → 待检验（UNCHECK）或已完成（FINISHED），取决于工序 checkFlag
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesProFeedbackStatusEnum implements ArrayValuable<Integer> {

    // TODO @AI：数据库的字典，需要迁移；

    // TODO @AI：对齐下 /Users/yunai/Java/yudao-all-in-one/ruoyi-vue-pro/yudao-module-mes/src/main/java/cn/iocoder/yudao/module/mes/enums/MesOrderStatusConstants.java

    // TODO @AI：整体注释，变量注释，对齐 /Users/yunai/Java/yudao-all-in-one/ruoyi-vue-pro/yudao-module-mes/src/main/java/cn/iocoder/yudao/module/mes/enums/pro/MesProTaskStatusEnum.java
    PREPARE(0, "草稿"),
    APPROVING(1, "审批中"),
    UNCHECK(2, "待检验"),
    FINISHED(3, "已完成");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesProFeedbackStatusEnum::getStatus).toArray(Integer[]::new);

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

