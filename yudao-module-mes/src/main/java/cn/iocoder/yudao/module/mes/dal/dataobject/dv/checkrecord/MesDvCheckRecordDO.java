package cn.iocoder.yudao.module.mes.dal.dataobject.dv.checkrecord;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.checkplan.MesDvCheckPlanDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.machinery.MesDvMachineryDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MES 设备点检记录 DO
 *
 * @author 芋道源码
 */
@TableName("mes_dv_check_record")
@KeySequence("mes_dv_check_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesDvCheckRecordDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 点检计划编号
     *
     * 关联 {@link MesDvCheckPlanDO#getId()}
     */
    private Long planId;
    // TODO @AI：这个字段可以移除；
    /**
     * 计划类型
     *
     * 字典类型 mes_dv_check_plan_type（与计划保持冗余，避免 JOIN）
     */
    private Integer planType;
    /**
     * 设备编号
     *
     * 关联 {@link MesDvMachineryDO#getId()}
     */
    private Long machineryId;
    /**
     * 点检时间
     */
    private LocalDateTime checkTime;
    /**
     * 点检人编号
     *
     * 关联 AdminUserDO#getId()
     */
    private Long userId;
    // TODO @AI：枚举、字典；
    /**
     * 状态
     *
     * 字典类型 mes_dv_check_record_status（10=待点检，20=已完成）
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}
