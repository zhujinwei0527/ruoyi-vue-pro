package cn.iocoder.yudao.module.mes.dal.dataobject.qc;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 质检方案-产品关联 DO
 *
 * @author 芋道源码
 */
@TableName("mes_qc_template_item")
@KeySequence("mes_qc_template_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesQcTemplateItemDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    // TODO @AI：关联字段
    /**
     * 质检方案ID
     */
    private Long templateId;
    // TODO @AI：关联字段
    /**
     * 产品物料ID（关联 mes_md_item，通过 ID 查询物料信息）
     */
    private Long itemId;
    /**
     * 最低检测数
     */
    private Integer quantityCheck;
    /**
     * 最大不合格数（0=不启用）
     */
    private Integer quantityUnqualified;
    /**
     * 最大致命缺陷率（%，0=不允许）
     */
    private BigDecimal criticalRate;
    /**
     * 最大严重缺陷率（%，0=不允许）
     */
    private BigDecimal majorRate;
    /**
     * 最大轻微缺陷率（%）
     */
    private BigDecimal minorRate;
    /**
     * 备注
     */
    private String remark;

}
