package cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * MES 工位 DO
 *
 * @author 芋道源码
 */
@TableName("mes_md_workstation")
@KeySequence("mes_md_workstation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesMdWorkstationDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 工位编码
     */
    private String code;
    /**
     * 工位名称
     */
    private String name;
    /**
     * 工位地点
     */
    private String address;
    // TODO @AI：通过 @ 去关联，按照别的模块
    /**
     * 所在车间编号，关联 {@link MesMdWorkshopDO#getId()}
     */
    private Long workshopId;
    // TODO @AI：通过 @ 去关联，按照别的模块
    /**
     * 工序编号
     */
    private Long processId;
    // TODO @AI：通过 @ 去关联，按照别的模块
    /**
     * 线边库编号
     */
    private Long warehouseId;
    // TODO @AI：通过 @ 去关联，按照别的模块
    /**
     * 库区编号
     */
    private Long locationId;
    /**
     * 库位编号
     */
    private Long areaId;
    // TODO @AI：通过 @ 去关联，按照别的模块
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
