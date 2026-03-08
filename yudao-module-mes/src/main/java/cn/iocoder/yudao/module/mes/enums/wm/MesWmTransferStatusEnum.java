package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.MesWmTransferSaveReqVO;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
import cn.iocoder.yudao.module.mes.service.wm.transfer.MesWmTransferService;
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

    /**
     * 草稿
     *
     * 对应方法：{@link MesWmTransferService#createTransfer(MesWmTransferSaveReqVO)}
     */
    PREPARE(MesOrderStatusConstants.PREPARE, "草稿"),
    /**
     * 待确认（仅配送模式）
     *
     * 对应方法：{@link MesWmTransferService#submitTransfer(Long)}
     */
    UNCONFIRMED(MesOrderStatusConstants.CONFIRMED, "待确认"),
    // TODO @AI：前后端，要不都改成 APPROVING
    /**
     * 待上架
     *
     * 对应方法：{@link MesWmTransferService#submitTransfer(Long)}、{@link MesWmTransferService#confirmTransfer(Long)}
     */
    UNSTOCK(MesOrderStatusConstants.APPROVING, "待上架"),
    // TODO @AI：前后端，要不都改成 APPROVED
    /**
     * 待执行
     *
     * 对应方法：{@link MesWmTransferService#stockTransfer(Long)}
     */
    UNEXECUTE(MesOrderStatusConstants.APPROVED, "待执行"),
    /**
     * 已完成
     *
     * 对应方法：{@link MesWmTransferService#finishTransfer(Long)}
     */
    FINISHED(MesOrderStatusConstants.FINISHED, "已完成"),
    /**
     * 已取消
     *
     * 对应方法：{@link MesWmTransferService#cancelTransfer(Long)}
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
