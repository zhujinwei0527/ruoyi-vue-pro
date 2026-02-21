package cn.iocoder.yudao.module.mes.enums.pro;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

// TODO @AI：注释下，状态的流转，不要太复杂；
/**
 * MES 生产报工状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum MesProFeedbackStatusEnum implements ArrayValuable<Integer> {

    PREPARE(0, "草稿"),
    APPROVING(1, "审批中"),
    UNCHECK(2, "待检验"),
    FINISHED(3, "已完成"),
    CANCELED(4, "已取消");

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
