package cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmProductRecptStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MES 产品收货（入库）单 DO
 */
@TableName("mes_wm_product_recpt")
@KeySequence("mes_wm_product_recpt_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmProductRecptDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 收货单编码
     */
    private String code;
    /**
     * 收货单名称
     */
    private String name;
    /**
     * 生产工单编号
     *
     * 关联 {@link MesProWorkOrderDO#getId()}
     */
    private Long workOrderId;
    /**
     * 产品物料编号
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long itemId;
    /**
     * 收货日期
     */
    private LocalDateTime receiptDate;
    /**
     * 状态
     *
     * 枚举 {@link MesWmProductRecptStatusEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}
