package cn.iocoder.yudao.module.mes.dal.dataobject.wm.stocktaking.plan;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmStockTakingPlanStatusEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmStockTakingTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

// TODO @AI：缺少注释；

/**
 * MES 盘点方案 DO
 */
@TableName("mes_wm_stock_taking_plan")
@KeySequence("mes_wm_stock_taking_plan_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmStockTakingPlanDO extends BaseDO {

    @TableId
    private Long id;

    private String code;

    private String name;

    /**
     * 枚举 {@link MesWmStockTakingTypeEnum}
     */
    private Integer type;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Boolean blindFlag;

    private Boolean frozenFlag;

    // TODO @AI：这个 enableFlag 改成 status，对应 CommonStatus；
    private Boolean enableFlag;

    // TODO @AI：这个字段没用，可以删除掉；
    /**
     * 枚举 {@link MesWmStockTakingPlanStatusEnum}
     */
    private Integer status;

    private String remark;

}
