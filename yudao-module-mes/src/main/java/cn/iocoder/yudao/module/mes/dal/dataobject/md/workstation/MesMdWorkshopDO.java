package cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 车间 DO
 *
 * @author 芋道源码
 */
@TableName("mes_md_workshop")
@KeySequence("mes_md_workshop_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesMdWorkshopDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 车间编码
     */
    private String code;
    /**
     * 车间名称
     */
    private String name;
    /**
     * 面积（平方米）
     */
    private BigDecimal area;
    // TODO @AI：通过 @ 去关联
    /**
     * 负责人用户编号，关联 system_users 表的 id
     */
    private Long chargeUserId;
    // TODO @AI：通过 @ 去关联
    /**
     * 状态，参见 CommonStatusEnum 枚举
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
