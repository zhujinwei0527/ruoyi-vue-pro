package cn.iocoder.yudao.module.mes.enums.wm;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.mes.enums.MesBizTypeConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * MES 条码业务类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum BarcodeBizTypeEnum implements ArrayValuable<Integer> {

    // ========== WM 仓库模块 [100, 200) ==========
    WAREHOUSE(MesBizTypeConstants.WM_WAREHOUSE, "仓库"),
    AREA(MesBizTypeConstants.WM_AREA, "库位"),
    PACKAGE(MesBizTypeConstants.WM_PACKAGE, "装箱单"),
    STOCK(MesBizTypeConstants.WM_STOCK, "库存"),
    BATCH(MesBizTypeConstants.WM_BATCH, "批次"),

    // ========== PRO 生产模块 [300, 400) ==========
    PROCARD(MesBizTypeConstants.PRO_CARD, "流转卡"),
    WORKORDER(MesBizTypeConstants.PRO_WORKORDER, "工单"),
    TRANSORDER(MesBizTypeConstants.PRO_TRANS_ORDER, "流转单"),

    // ========== DV 设备模块 [400, 500) ==========
    MACHINERY(MesBizTypeConstants.DV_MACHINERY, "设备"),

    // ========== TM 工装夹具模块 [500, 600) ==========
    TOOL(MesBizTypeConstants.TM_TOOL, "工装"),

    // ========== MD 主数据模块 [600, 700) ==========
    ITEM(MesBizTypeConstants.MD_ITEM, "物料"),
    VENDOR(MesBizTypeConstants.MD_VENDOR, "供应商"),
    WORKSTATION(MesBizTypeConstants.MD_WORKSTATION, "工作站"),
    WORKSHOP(MesBizTypeConstants.MD_WORKSHOP, "车间"),
    USER(MesBizTypeConstants.MD_USER, "人员");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(BarcodeBizTypeEnum::getValue).toArray(Integer[]::new);

    /**
     * 业务类型值
     */
    private final Integer value;
    /**
     * 业务类型名称
     */
    private final String label;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
