package cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * MES 工位设备 DO
 *
 * @author 芋道源码
 */
@TableName("mes_md_workstation_machine")
@KeySequence("mes_md_workstation_machine_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesMdWorkstationMachineDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    // TODO @AI：通过 @ 去关联，按照别的模块
    /**
     * 工位编号，关联 {@link MesMdWorkstationDO#id}
     */
    private Long workstationId;
    // TODO @AI：通过 @ 去关联，按照别的模块
    /**
     * 设备编号
     */
    private Long machineryId;
    /**
     * 数量
     */
    private Integer quantity;
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
