package cn.iocoder.yudao.module.mes.service.wm.outsourcereceipt;

import cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourcereceipt.MesWmOutsourceReceiptDetailDO;

import java.util.List;

/**
 * MES 委外收货明细 Service 接口
 */
public interface MesWmOutsourceReceiptDetailService {

    /**
     * 获得委外收货明细列表（根据收货单编号）
     *
     * @param receiptId 收货单编号
     * @return 委外收货明细列表
     */
    List<MesWmOutsourceReceiptDetailDO> getOutsourceReceiptDetailListByReceiptId(Long receiptId);

    /**
     * 获得委外收货明细列表（根据行编号）
     *
     * @param lineId 行编号
     * @return 委外收货明细列表
     */
    List<MesWmOutsourceReceiptDetailDO> getOutsourceReceiptDetailListByLineId(Long lineId);

    /**
     * 删除委外收货明细（根据收货单编号）
     *
     * @param receiptId 收货单编号
     */
    void deleteOutsourceReceiptDetailByReceiptId(Long receiptId);

}
