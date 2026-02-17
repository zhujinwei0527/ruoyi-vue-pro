package cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 库位 DO
 */
@TableName("mes_wm_warehouse_area")
@KeySequence("mes_wm_warehouse_area_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmWarehouseAreaDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 库位编码
     */
    private String code;
    /**
     * 库位名称
     */
    private String name;
    // TODO @AI：需要关联下 mes_wm_warehouse_location 表；
    /**
     * 库区编号
     */
    private Long locationId;
    /**
     * 面积
     */
    private BigDecimal area;
    /**
     * 最大载重
     */
    private BigDecimal maxLoad;
    /**
     * 坐标 X
     */
    private Integer positionX;
    /**
     * 坐标 Y
     */
    private Integer positionY;
    /**
     * 坐标 Z
     */
    private Integer positionZ;
    /**
     * 是否启用
     */
    private Boolean enabled;
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
     * 是否允许物料混放
     */
    private Boolean allowItemMixing;
    /**
     * 是否允许批次混放
     */
    private Boolean allowBatchMixing;
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
