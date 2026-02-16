package cn.iocoder.yudao.module.mes.dal.dataobject.md.item;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * MES 产品SIP DO
 *
 * @author 芋道源码
 */
@TableName("mes_md_product_sip")
@KeySequence("mes_md_product_sip_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesMdProductSipDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 物料产品编号
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long itemId;
    /**
     * 排列顺序
     */
    private Integer orderNum;
    /**
     * 工序编号
     */
    // TODO @AI：关联 {@link MesMdProcessDO#getId()}，但是目前还没有实现工序模块，所以先不写；后续工序模块实现后，再补上关联关系；
    private Long processId;
    /**
     * 标题
     */
    private String title;
    /**
     * 详细描述
     */
    private String description;
    /**
     * 图片地址
     */
    private String url;
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
