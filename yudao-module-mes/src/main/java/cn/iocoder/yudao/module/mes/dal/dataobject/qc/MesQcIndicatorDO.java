package cn.iocoder.yudao.module.mes.dal.dataobject.qc;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * MES 质检指标 DO
 *
 * @author 芋道源码
 */
@TableName("mes_qc_indicator")
@KeySequence("mes_qc_indicator_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesQcIndicatorDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 检测项编码
     */
    private String code;
    /**
     * 检测项名称
     */
    private String name;
    // TODO @AI：字典枚举，链接过去；
    /**
     * 检测项类型
     *
     * 字典类型 mes_index_type
     */
    private String type;
    /**
     * 检测工具
     */
    private String tool;
    // TODO @AI：字典枚举，链接过去；
    /**
     * 结果值类型
     *
     * 字典类型 mes_qc_result_type
     */
    private String resultType;
    /**
     * 结果值属性
     *
     * FILE 时存 IMG/FILE；DICT 时存字典类型名
     */
    private String resultSpec;
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
