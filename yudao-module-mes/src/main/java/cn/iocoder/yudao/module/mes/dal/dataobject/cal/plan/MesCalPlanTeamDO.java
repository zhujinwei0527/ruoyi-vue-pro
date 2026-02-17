package cn.iocoder.yudao.module.mes.dal.dataobject.cal.plan;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * MES 计划班组关联 DO
 *
 * @author 芋道源码
 */
@TableName("mes_cal_plan_team")
@KeySequence("mes_cal_plan_team_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesCalPlanTeamDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 排班计划编号
     *
     * 关联 {@link MesCalPlanDO#getId()}
     */
    private Long planId;
    /**
     * 班组编号
     *
     * TODO @AI：关联 cal_team 表，等 cal_team 迁移后补充
     */
    private Long teamId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 预留字段1
     */
    private String attribute1;
    /**
     * 预留字段2
     */
    private String attribute2;
    /**
     * 预留字段3
     */
    private Integer attribute3;
    /**
     * 预留字段4
     */
    private Integer attribute4;

}
