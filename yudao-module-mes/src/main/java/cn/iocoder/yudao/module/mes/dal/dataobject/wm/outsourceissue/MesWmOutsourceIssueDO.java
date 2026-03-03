package cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourceissue;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.vendor.MesMdVendorDO;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmOutsourceIssueStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MES 外协发料单 DO
 *
 * @author 芋道源码
 */
@TableName("mes_wm_outsource_issue")
@KeySequence("mes_wm_outsource_issue_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmOutsourceIssueDO extends BaseDO {

    /**
     * 发料单ID
     */
    @TableId
    private Long id;
    /**
     * 发料单编号
     */
    private String code;
    /**
     * 发料单名称
     */
    private String name;
    /**
     * 供应商ID
     *
     * 关联 {@link MesMdVendorDO#getId()}
     */
    private Long vendorId;
    // TODO @AI：去掉冗余的 vendorCode、vendorName 字段
    /**
     * 供应商编码
     */
    private String vendorCode;
    /**
     * 供应商名称
     */
    private String vendorName;
    // TODO @AI：去掉冗余的 workorderCode、workorderName 字段
    // TODO @AI：拼写不对；应该是 workOrderId；
    /**
     * 生产工单ID
     */
    private Long workorderId;
    /**
     * 生产工单编码
     */
    private String workorderCode;
    /**
     * 生产工单名称
     */
    private String workorderName;
    /**
     * 发料日期
     */
    private LocalDateTime issueDate;
    /**
     * 单据状态
     *
     * 枚举 {@link MesWmOutsourceIssueStatusEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}
