package cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * MES 工作站 DO
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
     * 工作站编码
     */
    private String code;
    /**
     * 工作站名称
     */
    private String name;
    /**
     * 工作站地点
     */
    private String address;
    /**
     * 所在车间编号
     *
     * 关联 {@link MesMdWorkshopDO#getId()}
     */
    private Long workshopId;
    /**
     * 工序编号
     *
     * TODO @芋艿：等 pro 模块，关联工序表
     */
    private Long processId;
    /**
     * 线边库编号
     *
     * TODO @芋艿：等 wm 模块，关联仓库表
     */
    private Long warehouseId;
    /**
     * 库区编号
     *
     * TODO @芋艿：等 wm 模块，关联库区表
     */
    private Long locationId;
    /**
     * 库位编号
     *
     * TODO @芋艿：等 wm 模块，关联库位表
     */
    private Long areaId;
    /**
     * 状态
     *
     * 枚举 {@link cn.iocoder.yudao.framework.common.enums.CommonStatusEnum}
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
