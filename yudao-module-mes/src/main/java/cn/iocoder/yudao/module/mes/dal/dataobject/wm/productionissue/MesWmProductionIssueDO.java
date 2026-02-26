package cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.client.MesMdClientDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.task.MesProTaskDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmProductionIssueStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

// TODO @AI：【需要一起讨论】不要直接改；是不是叫 issueheader 包？主要后续别的表也有 issue，希望区分下；还是加个合理的前缀？就像 wm_outsource_issue 这种；
/**
 * MES 领料出库单 DO
 */
@TableName("mes_wm_issue_header")
@KeySequence("mes_wm_issue_header_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmProductionIssueDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 领料单编号
     */
    private String code;
    /**
     * 领料单名称
     */
    private String name;
    /**
     * 工作站 ID
     *
     * 关联 {@link MesMdWorkstationDO#getId()}
     */
    private Long workstationId;
    // TODO @AI：workOrderId；
    /**
     * 生产工单 ID
     *
     * 关联 {@link MesProWorkOrderDO#getId()}
     */
    private Long workorderId;
    // TODO @芋艿：【疑问】这个字段的来源？
    /**
     * 生产任务 ID
     *
     * 关联 {@link MesProTaskDO#getId()}
     */
    private Long taskId;
    // TODO @AI：应该不用记录？因为 workorderId 字段的；
    /**
     * 客户 ID
     *
     * 关联 {@link MesMdClientDO#getId()}
     */
    private Long clientId;
    // TODO @芋艿：【疑问】这个字段的更新时间；
    /**
     * 领料日期
     */
    private LocalDateTime issueDate;
    /**
     * 需求时间
     */
    private LocalDateTime requiredTime;
    /**
     * 状态
     *
     * 枚举 {@link MesWmProductionIssueStatusEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 预留字段 1
     */
    private String attribute1;
    /**
     * 预留字段 2
     */
    private String attribute2;
    /**
     * 预留字段 3
     */
    private Integer attribute3;
    /**
     * 预留字段 4
     */
    private Integer attribute4;

}
