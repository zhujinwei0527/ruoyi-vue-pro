package cn.iocoder.yudao.module.mes.service.wm.itemreceipt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.detail.MesWmItemReceiptDetailPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.detail.MesWmItemReceiptDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptDetailDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 采购入库明细 Service 接口
 */
public interface MesWmItemReceiptDetailService {

    /**
     * 创建采购入库明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createItemReceiptDetail(@Valid MesWmItemReceiptDetailSaveReqVO createReqVO);

    /**
     * 修改采购入库明细
     *
     * @param updateReqVO 修改信息
     */
    void updateItemReceiptDetail(@Valid MesWmItemReceiptDetailSaveReqVO updateReqVO);

    /**
     * 删除采购入库明细
     *
     * @param id 编号
     */
    void deleteItemReceiptDetail(Long id);

    /**
     * 获得采购入库明细
     *
     * @param id 编号
     * @return 采购入库明细
     */
    MesWmItemReceiptDetailDO getItemReceiptDetail(Long id);

    /**
     * 获得采购入库明细分页
     *
     * @param pageReqVO 分页参数
     * @return 采购入库明细分页
     */
    PageResult<MesWmItemReceiptDetailDO> getItemReceiptDetailPage(MesWmItemReceiptDetailPageReqVO pageReqVO);

    /**
     * 按入库单行编号获得明细列表
     *
     * @param lineId 行编号
     * @return 明细列表
     */
    List<MesWmItemReceiptDetailDO> getItemReceiptDetailListByLineId(Long lineId);

}
