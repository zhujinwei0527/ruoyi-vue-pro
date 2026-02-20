package cn.iocoder.yudao.module.mes.dal.dataobject.dv.checkplan;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.enums.dv.MesDvCheckPlanStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MES 点检保养方案 DO
 *
 * @author 芋道源码
 */
@TableName("mes_dv_check_plan")
@KeySequence("mes_dv_check_plan_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesDvCheckPlanDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 方案编码
     */
    private String code;
    /**
     * 方案名称
     */
    private String name;
    // TODO @AI：需要在 dict constants 里枚举下，然后 @过去
    /**
     * 方案类型
     *
     * 字典类型 mes_dv_subject_type（1=设备点检，2=设备保养）
     */
    private Integer type;
    /**
     * 开始日期
     */
    private LocalDateTime startDate;
    /**
     * 结束日期
     */
    private LocalDateTime endDate;
    // TODO @AI：需要在 dict constants 里枚举下，然后 @过去
    /**
     * 周期类型
     *
     * 字典类型 mes_dv_cycle_type（1=天，2=周，3=月，4=年）
     */
    private Integer cycleType;
    /**
     * 周期数量
     */
    private Integer cycleCount;
    /**
     * 状态
     *
     * 枚举 {@link MesDvCheckPlanStatusEnum}
     */
    private Integer status;
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
