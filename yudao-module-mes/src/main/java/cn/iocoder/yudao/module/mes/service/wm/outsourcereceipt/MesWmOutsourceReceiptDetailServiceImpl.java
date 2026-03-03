package cn.iocoder.yudao.module.mes.service.wm.outsourcereceipt;

import cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourcereceipt.MesWmOutsourceReceiptDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.outsourcereceipt.MesWmOutsourceReceiptDetailMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * MES 委外收货明细 Service 实现类
 */
@Service
@Validated
public class MesWmOutsourceReceiptDetailServiceImpl implements MesWmOutsourceReceiptDetailService {

    @Resource
    private MesWmOutsourceReceiptDetailMapper detailMapper;

    @Override
    public List<MesWmOutsourceReceiptDetailDO> getOutsourceReceiptDetailListByReceiptId(Long receiptId) {
        return detailMapper.selectListByReceiptId(receiptId);
    }

    @Override
    public List<MesWmOutsourceReceiptDetailDO> getOutsourceReceiptDetailListByLineId(Long lineId) {
        return detailMapper.selectListByLineId(lineId);
    }

    @Override
    public void deleteOutsourceReceiptDetailByReceiptId(Long receiptId) {
        detailMapper.deleteByReceiptId(receiptId);
    }

}
