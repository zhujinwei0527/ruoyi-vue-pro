package cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

// TODO @AI：注释；以及关联；
@TableName("mes_md_workstation_tool")
@KeySequence("mes_md_workstation_tool_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesMdWorkstationToolDO extends BaseDO {

    @TableId
    private Long id;
    private Long workstationId;
    private Long toolTypeId;
    private Integer quantity;
    private String remark;
    private String attribute1;
    private String attribute2;
    private Integer attribute3;
    private Integer attribute4;

}
