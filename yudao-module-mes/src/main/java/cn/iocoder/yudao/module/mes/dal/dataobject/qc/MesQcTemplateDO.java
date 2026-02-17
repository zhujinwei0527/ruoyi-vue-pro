package cn.iocoder.yudao.module.mes.dal.dataobject.qc;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * MES 质检方案 DO
 *
 * @author 芋道源码
 */
@TableName("mes_qc_template")
@KeySequence("mes_qc_template_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesQcTemplateDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 方案编号
     */
    private String code;
    /**
     * 方案名称
     */
    private String name;
    // TODO @AI：List<Integer> types；以这个为主；然后 type int 1 - IQC、2 - IPQC、3 - OQC、4 - RQC；前端传过来逗号分隔的字符串，后端转换成 List<Integer> 存储到数据库中；查询时也是一样，数据库存储逗号分隔的字符串，后端转换成 List<Integer> 返回给前端。
    /**
     * 检测种类（逗号分隔：IQC,IPQC,OQC,RQC）
     *
     * 字典类型 mes_qc_type
     */
    private String types;
    // TODO @AI：Boolean
    /**
     * 是否启用（Y/N）
     *
     * 字典类型 sys_yes_no
     */
    private String enableFlag;
    /**
     * 备注
     */
    private String remark;

}
