package cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

// TODO @AI：注释；以及关联；
@TableName("mes_md_workshop")
@KeySequence("mes_md_workshop_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesMdWorkshopDO extends BaseDO {

    @TableId
    private Long id;
    private String code;
    private String name;
    private BigDecimal area;
    private Long chargeUserId;
    private Integer status;
    private String remark;
    private String attribute1;
    private String attribute2;
    private Integer attribute3;
    private Integer attribute4;

}
