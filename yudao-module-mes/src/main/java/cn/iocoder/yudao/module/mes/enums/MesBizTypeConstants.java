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
    public static final int WM_WAREHOUSE = 102; // 仓库：MesWmWarehouseDO
    public static final int WM_AREA = 103; // 库位：MesWmStorageAreaDO
    public static final int WM_PACKAGE = 104; // 装箱单：MesWmPackageDO
    public static final int WM_STOCK = 105; // 库存：MesWmMaterialStockDO
    public static final int WM_BATCH = 106; // 批次：MesWmBatchDO

    // ========== CAL 排班模块 [200, 300) ==========

    // TODO @芋艿【暂时忽略】：MesProWorkOrderTypeEnum
    // TODO @芋艿【暂时忽略】：MesProWorkOrderSourceTypeEnum

    // ========== PRO 生产模块 [300, 400) ==========

    public static final int PRO_CARD = 300; // 流转卡：MesProCardDO
    public static final int PRO_WORKORDER = 301; // 工单：MesProWorkOrderDO
    public static final int PRO_TRANS_ORDER = 302; // 流转单：MesProTransOrderDO

    // ========== DV 设备模块 [400, 500) ==========

    public static final int DV_MACHINERY = 400; // 设备：MesDvMachineryDO

    // ========== TM 工装夹具模块 [500, 600) ==========

    public static final int TM_TOOL = 500; // 工装：MesToolDO

    // ========== MD 主数据模块 [600, 700) ==========

    public static final int MD_ITEM = 600; // 物料：MesMdItemDO
    public static final int MD_VENDOR = 601; // 供应商：MesMdVendorDO
    public static final int MD_WORKSTATION = 602; // 工作站：MesMdWorkstationDO
    public static final int MD_WORKSHOP = 603; // 车间：MesMdWorkshopDO
    public static final int MD_USER = 604; // 人员：系统用户

}
