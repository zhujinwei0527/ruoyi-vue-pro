package cn.iocoder.yudao.module.mes.enums;

/**
 * MES 业务类型常量
 *
 * 集中管理业务类型枚举的编号，按业务域分段。
 * 各枚举类引用此处常量，避免硬编码数字。（也避免冲突！！！）
 *
 * @author 芋道源码
 */
public final class MesBizTypeConstants {

    private MesBizTypeConstants() {}

    // ========== QC 质检模块 [1, 100) ==========

    public static final int QC_IQC = 1; // 来料检验：MesQcIqcDO
    public static final int QC_IPQC = 2; // 过程检验：MesQcIpqcDO
    public static final int QC_OQC = 3; // 出货检验：MesQcOqcDO
    public static final int QC_RQC = 4; // 退货检验：MesQcRqcDO

    // ========== WM 仓库模块 [100, 200) ==========

    public static final int WM_ARRIVAL_NOTICE = 100; // 到货通知单：MesWmArrivalNoticeDO
    // public static final int WM_OUTSOURCE_RECPT = 101; // TODO 外协入库单：未实现，占位

    // ========== CAL 排班模块 [200, 300) ==========

    // TODO @芋艿【暂时忽略】：MesProWorkOrderTypeEnum
    // TODO @芋艿【暂时忽略】：MesProWorkOrderSourceTypeEnum

    // ========== PRO 生产模块 [300, 400) ==========

    // ========== DV 设备模块 [400, 500) ==========

    // ========== TM 工装夹具模块 [500, 600) ==========

    // ========== MD 主数据模块 [600, 700) ==========

}
