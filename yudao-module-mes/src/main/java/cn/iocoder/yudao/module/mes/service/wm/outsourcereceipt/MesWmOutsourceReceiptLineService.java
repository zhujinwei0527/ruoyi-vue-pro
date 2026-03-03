package cn.iocoder.yudao.module.mes.service.wm.outsourcereceipt;

import cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourcereceipt.MesWmOutsourceReceiptLineDO;

import java.util.List;

/**
 * MES 委外收货单行 Service 接口
 */
public interface MesWmOutsourceReceiptLineService {

    /**
     * 获得委外收货单行列表
     *
     * @param receiptId 收货单编号
     * @return 委外收货单行列表
     */
    List<MesWmOutsourceReceiptLineDO> getOutsourceReceiptLineListByReceiptId(Long receiptId);

    /**
     * 删除委外收货单行（根据收货单编号）
     *
     * @param receiptId 收货单编号
     */
    void deleteOutsourceReceiptLineByReceiptId(Long receiptId);

}
