package cn.iocoder.yudao.module.mes.dal.dataobject.pro.route;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 工艺路线产品 DO
 *
 * @author 芋道源码
 */
@TableName("mes_pro_route_product")
@KeySequence("mes_pro_route_product_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesProRouteProductDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 工艺路线编号
     *
     * 关联 {@link MesProRouteDO#getId()}
     */
    private Long routeId;
    /**
     * 产品物料编号
     *
     * 关联 {@link cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO#getId()}
     */
    private Long itemId;
    /**
     * 生产数量
     */
    private Integer quantity;
    /**
     * 生产用时
     */
    private BigDecimal productionTime;
    /**
     * 时间单位
     *
     * 字典类型 mes_time_unit_type
     */
    // TODO @AI：外链 @ 加下，字典 + 枚举类；（需要枚举出来。在 dictconstants 里）
    private String timeUnitType;
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
