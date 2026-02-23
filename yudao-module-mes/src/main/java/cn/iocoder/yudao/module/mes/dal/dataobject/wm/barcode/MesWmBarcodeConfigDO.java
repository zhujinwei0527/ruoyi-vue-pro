package cn.iocoder.yudao.module.mes.dal.dataobject.wm.barcode;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * MES 条码配置 DO
 */
@TableName("mes_wm_barcode_config")
@KeySequence("mes_wm_barcode_config_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmBarcodeConfigDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
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
     * 内容格式
     */
    private String contentFormat;
    /**
     * 内容样例
     */
    private String contentExample;
    /**
     * 是否自动生成
     */
    private Boolean autoGenerateFlag;
    // todo DONE @AI：printTemplate 改成这个字段；=> 已改名为 defaultTemplate
    /**
     * 默认打印模板
     */
    private String defaultTemplate;
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
