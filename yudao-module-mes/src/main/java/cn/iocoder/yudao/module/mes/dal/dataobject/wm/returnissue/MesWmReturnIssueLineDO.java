package cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnissue;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 生产退料单行 DO
 */
@TableName("mes_wm_return_issue_line")
@KeySequence("mes_wm_return_issue_line_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmReturnIssueLineDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 退料单 ID
     *
     * 关联 {@link MesWmReturnIssueDO#getId()}
     */
    private Long issueId;
    /**
     * 库存记录 ID
     */
    private Long materialStockId;
    /**
     * 物料 ID
     *
     * 关联 {@link MesMdItemDO#getId()}
     */
    private Long itemId;
    /**
     * 退料数量
     */
    private BigDecimal quantity;
    /**
     * 批次 ID
     */
    private Long batchId;
    /**
     * 过程检验单 ID
     */
    private Long ipqcId;
    /**
     * 是否需要质检
     */
    private Boolean qcFlag;
    // TODO @AI：额外加个枚举类；
    // TODO @芋艿：是这些么？
    /**
     * 质量状态
     *
     * 字典 {@link cn.iocoder.yudao.module.mes.enums.DictTypeConstants#MES_WM_QUALITY_STATUS}
     */
    private Integer qualityStatus;
    /**
     * 备注
     */
    private String remark;

}
