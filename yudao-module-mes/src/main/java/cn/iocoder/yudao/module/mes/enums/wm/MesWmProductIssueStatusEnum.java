package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productissue.vo.MesWmProductIssueSaveReqVO;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
import cn.iocoder.yudao.module.mes.service.wm.productissue.MesWmProductIssueService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 领料出库单状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesWmProductIssueStatusEnum implements ArrayValuable<Integer> {

    /**
     * 草稿
     *
     * 对应方法：{@link MesWmProductIssueService#createProductIssue(MesWmProductIssueSaveReqVO)}
     */
    PREPARE(MesOrderStatusConstants.PREPARE, "草稿"),
    /**
     * 待拣货
     *
     * 对应方法：{@link MesWmProductIssueService#submitProductIssue(Long)}
     */
    APPROVING(MesOrderStatusConstants.APPROVING, "待拣货"),
    /**
     * 待执行领出
     *
     * 对应方法：{@link MesWmProductIssueService#stockProductIssue(Long)}
     */
    APPROVED(MesOrderStatusConstants.APPROVED, "待执行领出"),
    /**
     * 已完成
     *
     * 对应方法：{@link MesWmProductIssueService#finishProductIssue(Long)}
     */
    FINISHED(MesOrderStatusConstants.FINISHED, "已完成"),
    /**
     * 已取消
     *
     * 对应方法：{@link MesWmProductIssueService#cancelProductIssue(Long)}
     */
    CANCELED(MesOrderStatusConstants.CANCELLED, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesWmProductIssueStatusEnum::getStatus).toArray(Integer[]::new);

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
