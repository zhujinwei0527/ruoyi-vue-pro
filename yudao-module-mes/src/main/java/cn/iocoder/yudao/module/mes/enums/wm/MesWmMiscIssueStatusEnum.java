package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
import cn.iocoder.yudao.module.mes.service.wm.miscissue.MesWmMiscIssueService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 杂项出库单状态枚举
 *
 * 对应字典 mes_wm_misc_issue_status
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesWmMiscIssueStatusEnum implements ArrayValuable<Integer> {

    /**
     * 草稿
     *
     * 对应方法：{@link MesWmMiscIssueService#createMiscIssue}
     */
    PREPARE(MesOrderStatusConstants.PREPARE, "草稿"),
    /**
     * 待执行出库
     *
     * 对应方法：{@link MesWmMiscIssueService#submitMiscIssue}
     */
    APPROVED(MesOrderStatusConstants.APPROVED, "待执行出库"),
    /**
     * 已完成
     *
     * 对应方法：{@link MesWmMiscIssueService#finishMiscIssue}
     */
    FINISHED(MesOrderStatusConstants.FINISHED, "已完成"),
    /**
     * 已取消
     *
     * 对应方法：{@link MesWmMiscIssueService#cancelMiscIssue}
     */
    CANCELED(MesOrderStatusConstants.CANCELLED, "已取消");

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
