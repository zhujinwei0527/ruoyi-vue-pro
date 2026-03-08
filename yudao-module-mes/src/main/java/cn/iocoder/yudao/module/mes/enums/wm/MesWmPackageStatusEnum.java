package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 装箱单状态枚举
 */
@Getter
@AllArgsConstructor
public enum MesWmPackageStatusEnum implements ArrayValuable<Integer> {

    // TODO @AI：需要增加下：     * 对应方法：{@link MesWmOutsourceIssueService#createOutsourceIssue(MesWmOutsourceIssueSaveReqVO)}
    /**
     * 草稿
     */
    PREPARE(MesOrderStatusConstants.PREPARE, "草稿"),
    /**
     * 已完成
     */
    FINISHED(MesOrderStatusConstants.FINISHED, "已完成");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MesWmPackageStatusEnum::getStatus)
            .toArray(Integer[]::new);

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
