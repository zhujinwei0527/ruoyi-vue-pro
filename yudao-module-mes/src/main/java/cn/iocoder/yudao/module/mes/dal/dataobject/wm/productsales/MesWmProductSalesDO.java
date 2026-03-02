package cn.iocoder.yudao.module.mes.dal.dataobject.wm.productsales;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.client.MesMdClientDO;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmProductSalesStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MES 销售出库单 DO
 *
 * @author 芋道源码
 */
@TableName("mes_wm_product_sales")
@KeySequence("mes_wm_product_sales_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmProductSalesDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 出库单号
     */
    private String code;
    /**
     * 出库单名称
     */
    private String name;
    /**
     * 客户ID
     *
     * 关联 {@link MesMdClientDO#getId()}
     */
    private Long clientId;
    /**
     * 销售订单号
     */
    private String salesOrderCode;
    // TODO @AI：sales_date 改成这个字段。出库日期
    /**
     * 计划发货日期
     */
    private LocalDateTime shipmentDate;

    // TODO @AI：收货地址，删除掉；（界面里也删除掉）
    /**
     * 收货地址
     */
    private String contactAddress;
    // TODO @AI：关联下对应的字段 salesnotice；
    /**
     * 发货通知单ID
     */
    private Long noticeId;
    // TODO @AI：noticeCode 不存储；
    /**
     * 发货通知单编号
     */
    private String noticeCode;

    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系电话
     */
    private String contactTelephone;

    /**
     * 承运商
     */
    private String carrier;
    /**
     * 运输单号
     */
    private String shippingNumber;

    /**
     * 状态
     *
     * 枚举 {@link MesWmProductSalesStatusEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}