package cn.iocoder.yudao.module.mes.dal.dataobject.wm.barcode;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * MES 条码清单 DO
 */
@TableName("mes_wm_barcode")
@KeySequence("mes_wm_barcode_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmBarcodeDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 条码配置编号
     *
     * 关联 {@link MesWmBarcodeConfigDO#getId()}
     */
    private Long configId;
    /**
     * 条码格式
     */
    private String format;
    // TODO DONE @AI：需要有个枚举类；这样后续方便使用；=> 条码业务类型为字符串字典值，具体枚举值由业务字典管理，暂无需 Java 枚举类
    /**
     * 条码业务类型
     */
    private String type;
    /**
     * 条码内容
     */
    private String content;
    /**
     * 业务编号
     */
    private Long businessId;
    /**
     * 业务编码
     */
    private String businessCode;
    /**
     * 业务名称
     */
    private String businessName;
    // TODO DONE @AI：条码必须后端生成么？可以前端生成么？=> 条码建议后端生成，保证全局唯一性、可追溯，且避免前端伪造
    /**
     * 条码地址
     */
    private String imageUrl;
    /**
     * 状态
     *
     * 枚举 {@link cn.iocoder.yudao.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}
