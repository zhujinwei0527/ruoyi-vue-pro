package cn.iocoder.yudao.module.mes.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * MES 错误码枚举类
 * <p>
 * mes 系统，使用 1-040-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== MES 基础数据-物料分类（1-040-100-000） ==========
    ErrorCode MD_ITEM_TYPE_NOT_EXISTS = new ErrorCode(1_040_100_000, "物料分类不存在");
    ErrorCode MD_ITEM_TYPE_EXITS_CHILDREN = new ErrorCode(1_040_100_001, "存在子分类，无法删除");
    ErrorCode MD_ITEM_TYPE_PARENT_NOT_EXITS = new ErrorCode(1_040_100_002, "父级分类不存在");
    ErrorCode MD_ITEM_TYPE_PARENT_ERROR = new ErrorCode(1_040_100_003, "不能设置自己为父分类");
    ErrorCode MD_ITEM_TYPE_NAME_DUPLICATE = new ErrorCode(1_040_100_004, "同一父分类下已存在该名称的分类");
    ErrorCode MD_ITEM_TYPE_CODE_DUPLICATE = new ErrorCode(1_040_100_005, "同一父分类下已存在该编码的分类");
    ErrorCode MD_ITEM_TYPE_PARENT_IS_CHILD = new ErrorCode(1_040_100_006, "不能设置自己的子分类为父分类");
    ErrorCode MD_ITEM_TYPE_EXITS_ITEM = new ErrorCode(1_040_100_007, "该分类下存在物料，无法删除");

    // ========== MES 基础数据-计量单位（1-040-101-000） ==========
    ErrorCode MD_UNIT_MEASURE_NOT_EXISTS = new ErrorCode(1_040_101_000, "计量单位不存在");
    ErrorCode MD_UNIT_MEASURE_CODE_DUPLICATE = new ErrorCode(1_040_101_001, "计量单位编码已存在");
    ErrorCode MD_UNIT_MEASURE_HAS_ITEM = new ErrorCode(1_040_101_002, "该计量单位下存在物料，无法删除");

    // ========== MES 基础数据-物料（1-040-102-000） ==========
    ErrorCode MD_ITEM_NOT_EXISTS = new ErrorCode(1_040_102_000, "物料不存在");
    ErrorCode MD_ITEM_CODE_DUPLICATE = new ErrorCode(1_040_102_001, "物料编码已存在");
    ErrorCode MD_ITEM_NAME_DUPLICATE = new ErrorCode(1_040_102_002, "物料名称已存在");
    ErrorCode MD_ITEM_IMPORT_LIST_IS_EMPTY = new ErrorCode(1_040_102_003, "导入物料数据不能为空");

    // ========== MES 基础数据-物料批次属性配置（1-040-102-100） ==========
    ErrorCode MD_ITEM_BATCH_CONFIG_NOT_EXISTS = new ErrorCode(1_040_102_100, "物料批次属性配置不存在");
    ErrorCode MD_ITEM_BATCH_CONFIG_AT_LEAST_ONE_FLAG = new ErrorCode(1_040_102_101, "批次管理已启用，至少需要配置一个批次属性");

    // ========== MES 基础数据-客户（1-040-103-000） ==========
    ErrorCode MD_CLIENT_NOT_EXISTS = new ErrorCode(1_040_103_000, "客户不存在");
    ErrorCode MD_CLIENT_CODE_DUPLICATE = new ErrorCode(1_040_103_001, "客户编码已存在");
    ErrorCode MD_CLIENT_NAME_DUPLICATE = new ErrorCode(1_040_103_002, "客户名称已存在");
    ErrorCode MD_CLIENT_NICKNAME_DUPLICATE = new ErrorCode(1_040_103_003, "客户简称已存在");
    ErrorCode MD_CLIENT_IMPORT_LIST_IS_EMPTY = new ErrorCode(1_040_103_004, "导入客户数据不能为空");

    // ========== MES 基础数据-供应商（1-040-104-000） ==========
    ErrorCode MD_VENDOR_NOT_EXISTS = new ErrorCode(1_040_104_000, "供应商不存在");
    ErrorCode MD_VENDOR_CODE_DUPLICATE = new ErrorCode(1_040_104_001, "供应商编码已存在");
    ErrorCode MD_VENDOR_NAME_DUPLICATE = new ErrorCode(1_040_104_002, "供应商名称已存在");
    ErrorCode MD_VENDOR_NICKNAME_DUPLICATE = new ErrorCode(1_040_104_003, "供应商简称已存在");
    ErrorCode MD_VENDOR_IMPORT_LIST_IS_EMPTY = new ErrorCode(1_040_104_004, "导入供应商数据不能为空");

    // ========== MES 基础数据-车间（1-040-105-000） ==========
    ErrorCode MD_WORKSHOP_NOT_EXISTS = new ErrorCode(1_040_105_000, "车间不存在");
    ErrorCode MD_WORKSHOP_CODE_DUPLICATE = new ErrorCode(1_040_105_001, "车间编码已存在");
    ErrorCode MD_WORKSHOP_NAME_DUPLICATE = new ErrorCode(1_040_105_002, "车间名称已存在");
    ErrorCode MD_WORKSHOP_HAS_WORKSTATION = new ErrorCode(1_040_105_003, "车间下存在工作站，无法删除");

    // ========== MES 基础数据-工作站（1-040-106-000） ==========
    ErrorCode MD_WORKSTATION_NOT_EXISTS = new ErrorCode(1_040_106_000, "工作站不存在");
    ErrorCode MD_WORKSTATION_CODE_DUPLICATE = new ErrorCode(1_040_106_001, "工作站编码已存在");
    ErrorCode MD_WORKSTATION_NAME_DUPLICATE = new ErrorCode(1_040_106_002, "工作站名称已存在");
    // ========== MES 基础数据-设备资源（1-040-106-100） ==========
    ErrorCode MD_WORKSTATION_MACHINE_NOT_EXISTS = new ErrorCode(1_040_106_100, "设备资源记录不存在");
    ErrorCode MD_WORKSTATION_MACHINE_EXISTS = new ErrorCode(1_040_106_101, "该设备已分配到其他工作站");
    // ========== MES 基础数据-工装夹具资源（1-040-106-200） ==========
    ErrorCode MD_WORKSTATION_TOOL_NOT_EXISTS = new ErrorCode(1_040_106_200, "工装夹具资源记录不存在");
    ErrorCode MD_WORKSTATION_TOOL_TYPE_EXISTS = new ErrorCode(1_040_106_201, "该工具类型已在此工作站中存在");
    // ========== MES 基础数据-人力资源（1-040-106-300） ==========
    ErrorCode MD_WORKSTATION_WORKER_NOT_EXISTS = new ErrorCode(1_040_106_300, "人力资源记录不存在");
    ErrorCode MD_WORKSTATION_WORKER_POST_EXISTS = new ErrorCode(1_040_106_301, "该岗位已在此工作站中存在");

    // ========== MES 基础数据-产品BOM（1-040-107-000） ==========
    ErrorCode MD_PRODUCT_BOM_NOT_EXISTS = new ErrorCode(1_040_107_000, "产品BOM不存在");
    ErrorCode MD_PRODUCT_BOM_SELF_REFERENCE = new ErrorCode(1_040_107_001, "产品不能作为自身的BOM物料");
    ErrorCode MD_PRODUCT_BOM_CIRCULAR = new ErrorCode(1_040_107_002, "BOM物料存在闭环，无法新增");

    // ========== MES 基础数据-产品SOP（1-040-108-000） ==========
    ErrorCode MD_PRODUCT_SOP_NOT_EXISTS = new ErrorCode(1_040_108_000, "产品SOP不存在");
    ErrorCode MD_PRODUCT_SOP_ORDER_NUMBER_DUPLICATE = new ErrorCode(1_040_108_001, "该展示序号已存在");

    // ========== MES 基础数据-产品SIP（1-040-109-000） ==========
    ErrorCode MD_PRODUCT_SIP_NOT_EXISTS = new ErrorCode(1_040_109_000, "产品SIP不存在");
    ErrorCode MD_PRODUCT_SIP_ORDER_NUMBER_DUPLICATE = new ErrorCode(1_040_109_001, "该展示序号已存在");

    // ========== MES 日历排班-班次（1-040-200-000） ==========
    ErrorCode CAL_SHIFT_NOT_EXISTS = new ErrorCode(1_040_200_000, "班次不存在");

    // ========== MES 日历排班-班组（1-040-201-000） ==========
    ErrorCode CAL_TEAM_NOT_EXISTS = new ErrorCode(1_040_201_000, "班组不存在");

    // ========== MES 日历排班-排班计划（1-040-202-000） ==========
    ErrorCode CAL_PLAN_NOT_EXISTS = new ErrorCode(1_040_202_000, "排班计划不存在");

    // ========== MES 日历排班-假期设置（1-040-203-000） ==========
    ErrorCode CAL_HOLIDAY_NOT_EXISTS = new ErrorCode(1_040_203_000, "假期设置不存在");

    // ========== MES 设备管理-设备类型（1-040-300-000） ==========
    ErrorCode DV_MACHINERY_TYPE_NOT_EXISTS = new ErrorCode(1_040_300_000, "设备类型不存在");

    // ========== MES 设备管理-设备台账（1-040-301-000） ==========
    ErrorCode DV_MACHINERY_NOT_EXISTS = new ErrorCode(1_040_301_000, "设备不存在");

    // ========== MES 设备管理-点检计划（1-040-302-000） ==========
    ErrorCode DV_CHECK_PLAN_NOT_EXISTS = new ErrorCode(1_040_302_000, "点检计划不存在");

    // ========== MES 设备管理-维修工单（1-040-303-000） ==========
    ErrorCode DV_REPAIR_NOT_EXISTS = new ErrorCode(1_040_303_000, "维修工单不存在");

    // ========== MES 工具管理-工具类型（1-040-400-000） ==========
    ErrorCode TM_TOOL_TYPE_NOT_EXISTS = new ErrorCode(1_040_400_000, "工具类型不存在");

    // ========== MES 工具管理-工具台账（1-040-401-000） ==========
    ErrorCode TM_TOOL_NOT_EXISTS = new ErrorCode(1_040_401_000, "工具不存在");

    // ========== MES 生产管理-工序（1-040-500-000） ==========
    ErrorCode PRO_PROCESS_NOT_EXISTS = new ErrorCode(1_040_500_000, "工序不存在");

    // ========== MES 生产管理-工艺路线（1-040-501-000） ==========
    ErrorCode PRO_ROUTE_NOT_EXISTS = new ErrorCode(1_040_501_000, "工艺路线不存在");

    // ========== MES 生产管理-生产工单（1-040-502-000） ==========
    ErrorCode PRO_WORKORDER_NOT_EXISTS = new ErrorCode(1_040_502_000, "生产工单不存在");

    // ========== MES 生产管理-生产任务（1-040-503-000） ==========
    ErrorCode PRO_TASK_NOT_EXISTS = new ErrorCode(1_040_503_000, "生产任务不存在");

    // ========== MES 质量管理-检验模板（1-040-600-000） ==========
    ErrorCode QC_TEMPLATE_NOT_EXISTS = new ErrorCode(1_040_600_000, "检验模板不存在");

    // ========== MES 质量管理-质检指标（1-040-601-000） ==========
    ErrorCode QC_INDEX_NOT_EXISTS = new ErrorCode(1_040_601_000, "质检指标不存在");

    // ========== MES 质量管理-缺陷类型（1-040-602-000） ==========
    ErrorCode QC_DEFECT_NOT_EXISTS = new ErrorCode(1_040_602_000, "缺陷类型不存在");

    // ========== MES 仓库管理-仓库（1-040-700-000） ==========
    ErrorCode WM_WAREHOUSE_NOT_EXISTS = new ErrorCode(1_040_700_000, "仓库不存在");

    // ========== MES 仓库管理-库区（1-040-701-000） ==========
    ErrorCode WM_STORAGE_AREA_NOT_EXISTS = new ErrorCode(1_040_701_000, "库区不存在");

    // ========== MES 仓库管理-库位（1-040-702-000） ==========
    ErrorCode WM_STORAGE_LOCATION_NOT_EXISTS = new ErrorCode(1_040_702_000, "库位不存在");

    // ========== MES 仓库管理-库存（1-040-703-000） ==========
    ErrorCode WM_MATERIAL_STOCK_NOT_EXISTS = new ErrorCode(1_040_703_000, "库存记录不存在");

}
