package cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 库区 DO
 */
@TableName("mes_wm_warehouse_location")
@KeySequence("mes_wm_warehouse_location_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmWarehouseLocationDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 库区编码
     */
    private String code;
    /**
     * 库区名称
     */
    private String name;
    // TODO @AI：需要关联下 mes_wm_warehouse 表；
    /**
     * 仓库编号
     */
    private Long warehouseId;
    /**
     * 面积
     */
    private BigDecimal area;
    /**
     * 是否启用库位管理
     */
    private Boolean areaEnabled;
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
