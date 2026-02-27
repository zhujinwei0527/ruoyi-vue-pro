package cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialrequest;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 领料申请单行 DO
 */
@TableName("mes_wm_material_request_line")
@KeySequence("mes_wm_material_request_line_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmMaterialRequestLineDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 领料申请单ID
     *
     * 关联 {@link MesWmMaterialRequestDO#getId()}
     */
    private Long materialRequestId;
    /**
     * 产品物料ID
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long itemId;
    /**
     * 计量单位ID
     *
     * 关联 {@link MesMdUnitMeasureDO#getId()}
     */
    private Long unitMeasureId;
    /**
     * 需求数量
     */
    private BigDecimal quantity;
    /**
     * 备注
     */
    private String remark;

    // TODO @AI：删除 attribute1-4 字段
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
