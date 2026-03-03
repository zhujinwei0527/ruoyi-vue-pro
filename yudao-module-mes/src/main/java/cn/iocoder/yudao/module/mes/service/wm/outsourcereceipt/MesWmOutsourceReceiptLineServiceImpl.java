package cn.iocoder.yudao.module.mes.service.wm.outsourcereceipt;

import cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourcereceipt.MesWmOutsourceReceiptLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.outsourcereceipt.MesWmOutsourceReceiptLineMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * MES 委外收货单行 Service 实现类
 */
@Service
@Validated
public class MesWmOutsourceReceiptLineServiceImpl implements MesWmOutsourceReceiptLineService {

    @Resource
    private MesWmOutsourceReceiptLineMapper lineMapper;

    // TODO @AI：新增、修改缺少；

    @Override
    public List<MesWmOutsourceReceiptLineDO> getOutsourceReceiptLineListByReceiptId(Long receiptId) {
        return lineMapper.selectListByReceiptId(receiptId);
    }

    @Override
    public void deleteOutsourceReceiptLineByReceiptId(Long receiptId) {
        lineMapper.deleteByReceiptId(receiptId);
    }

}
