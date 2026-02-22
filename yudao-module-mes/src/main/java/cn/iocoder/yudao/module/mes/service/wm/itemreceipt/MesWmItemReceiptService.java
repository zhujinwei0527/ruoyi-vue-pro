package cn.iocoder.yudao.module.mes.service.wm.itemreceipt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.MesWmItemReceiptPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.MesWmItemReceiptSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptDO;
import jakarta.validation.Valid;

/**
 * MES 采购入库单 Service 接口
 */
public interface MesWmItemReceiptService {

    /**
     * 创建采购入库单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createItemReceipt(@Valid MesWmItemReceiptSaveReqVO createReqVO);

    /**
     * 修改采购入库单
     *
     * @param updateReqVO 修改信息
     */
    void updateItemReceipt(@Valid MesWmItemReceiptSaveReqVO updateReqVO);

    /**
     * 删除采购入库单（级联删除行+明细）
     *
     * @param id 编号
     */
    void deleteItemReceipt(Long id);

    /**
     * 获得采购入库单
     *
     * @param id 编号
     * @return 采购入库单
     */
    MesWmItemReceiptDO getItemReceipt(Long id);

    /**
     * 获得采购入库单分页
     *
     * @param pageReqVO 分页参数
     * @return 采购入库单分页
     */
    PageResult<MesWmItemReceiptDO> getItemReceiptPage(MesWmItemReceiptPageReqVO pageReqVO);

    /**
     * 提交采购入库单（草稿 → 已提交）
     *
     * @param id 编号
     */
    void submitItemReceipt(Long id);

    /**
     * 审批采购入库单（已提交 → 已审批）
     *
     * @param id 编号
     */
    void approveItemReceipt(Long id);

    /**
     * 执行入库（已审批 → 已完成），更新库存台账
     *
     * @param id 编号
     */
    void executeItemReceipt(Long id);

}
