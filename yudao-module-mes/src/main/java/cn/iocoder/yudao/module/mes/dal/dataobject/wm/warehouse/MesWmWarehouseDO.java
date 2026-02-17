package cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 仓库 DO
 */
@TableName("mes_wm_warehouse")
@KeySequence("mes_wm_warehouse_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmWarehouseDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 仓库编码
     */
    private String code;
    /**
     * 仓库名称
     */
    private String name;
    /**
     * 仓库地址
     */
    private String address;
    /**
     * 面积
     */
    private BigDecimal area;
    // TODO @AI：需要关联下 system_users 表；
    /**
     * 负责人用户编号
     */
    private Long chargeUserId;
    /**
     * 状态
     *
     * 枚举 {@link cn.iocoder.yudao.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;
    /**
     * 是否冻结
     */
    private Boolean frozen;
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
