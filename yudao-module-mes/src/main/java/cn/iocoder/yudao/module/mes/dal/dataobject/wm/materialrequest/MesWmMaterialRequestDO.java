package cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialrequest;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MES 领料申请单 DO
 */
@TableName("mes_wm_material_request")
@KeySequence("mes_wm_material_request_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmMaterialRequestDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 工作站ID
     *
     * 关联 {@link MesMdWorkstationDO#getId()}
     */
    private Long workstationId;
    /**
     * 生产工单ID
     *
     * 关联 {@link MesProWorkOrderDO#getId()}
     */
    private Long workOrderId;
    /**
     * 需求人用户ID
     *
     * 关联 system_users.id
     */
    private Long userId;
    /**
     * 需求时间
     */
    private LocalDateTime requestTime;
    /**
     * 开始备料时间
     */
    private LocalDateTime startTime;
    /**
     * 完成时间
     */
    private LocalDateTime endTime;
    // TODO @AI：枚举类；
    // TODO @AI：字典；
    /**
     * 状态
     *
     * 0=草稿 1=备料中 2=待领料 3=已完成 4=已取消
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
