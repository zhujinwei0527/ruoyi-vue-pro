package cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.client.MesMdClientDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.vendor.MesMdVendorDO;
import cn.iocoder.yudao.module.mes.enums.pro.MesProWorkorderSourceTypeEnum;
import cn.iocoder.yudao.module.mes.enums.pro.MesProWorkorderStatusEnum;
import cn.iocoder.yudao.module.mes.enums.pro.MesProWorkorderTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 生产工单 DO
 *
 * @author 芋道源码
 */
@TableName("mes_pro_workorder")
@KeySequence("mes_pro_workorder_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesProWorkorderDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 工单编码
     */
    private String code;
    /**
     * 工单名称
     */
    private String name;
    /**
     * 工单类型
     *
     * 枚举 {@link MesProWorkorderTypeEnum}
     */
    private Integer type;
    /**
     * 来源类型
     *
     * 枚举 {@link MesProWorkorderSourceTypeEnum}
     */
    private Integer orderSourceType;
    /**
     * 来源单据编号
     */
    private String orderSourceCode;
    /**
     * 产品编号
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long productId;
    /**
     * 单位编号
     *
     * 关联 {@link MesMdUnitMeasureDO#getId()}
     */
    private Long unitMeasureId;
    /**
     * 生产数量
     */
    private BigDecimal quantity;
    /**
     * 已生产数量
     */
    private BigDecimal quantityProduced;
    /**
     * 调整数量
     */
    private BigDecimal quantityChanged;
    /**
     * 已排产数量
     */
    private BigDecimal quantityScheduled;
    /**
     * 客户编号
     *
     * 关联 {@link MesMdClientDO#getId()}
     */
    private Long clientId;
    /**
     * 供应商编号
     *
     * 关联 {@link MesMdVendorDO#getId()}
     */
    private Long vendorId;
    /**
     * 批次号
     */
    private String batchCode;
    /**
     * 需求日期
     */
    private LocalDateTime requestDate;
    /**
     * 父工单编号
     *
     * 关联 {@link MesProWorkorderDO#getId()}
     */
    private Long parentId;
    // TODO @AI：panrentId？不用父节点编号；
    /**
     * 所有父节点编号
     */
    private String ancestors;
    /**
     * 完成时间
     */
    private LocalDateTime finishDate;
    /**
     * 取消时间
     */
    private LocalDateTime cancelDate;
    /**
     * 工单状态
     *
     * 枚举 {@link MesProWorkorderStatusEnum}
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
