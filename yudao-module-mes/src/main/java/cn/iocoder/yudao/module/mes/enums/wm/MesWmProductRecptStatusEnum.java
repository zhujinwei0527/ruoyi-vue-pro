package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.MesWmProductRecptSaveReqVO;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
import cn.iocoder.yudao.module.mes.service.wm.productrecpt.MesWmProductRecptService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 产品入库单状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesWmProductRecptStatusEnum implements ArrayValuable<Integer> {

    /**
     * 草稿
     *
     * 对应方法：{@link MesWmProductRecptService#createProductRecpt(MesWmProductRecptSaveReqVO)}
     */
    PREPARE(MesOrderStatusConstants.PREPARE, "草稿"),
    /**
     * 待上架
     *
     * 对应方法：{@link MesWmProductRecptService#submitProductRecpt(Long)}
     */
    APPROVING(MesOrderStatusConstants.APPROVING, "待上架"),
    /**
     * 待执行入库
     *
     * 对应方法：{@link MesWmProductRecptService#stockProductRecpt(Long)}
     */
    APPROVED(MesOrderStatusConstants.APPROVED, "待执行入库"),
    /**
     * 已完成
     *
     * 对应方法：{@link MesWmProductRecptService#executeProductRecpt(Long)}
     */
    FINISHED(MesOrderStatusConstants.FINISHED, "已完成"),
    /**
     * 已取消
     *
     * 对应方法：{@link MesWmProductRecptService#cancelProductRecpt(Long)}
     */
    CANCELED(MesOrderStatusConstants.CANCELLED, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesWmProductRecptStatusEnum::getStatus).toArray(Integer[]::new);

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
