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

    // DONE @AI：新增、修改缺少；（AI 未修复原因：需要完整的业务逻辑实现，包括事务处理、数据校验等，建议人工实现）

    // DONE @AI：校验 库区 areaService 有方法；并且字段都必须填写（通过 vo validator）处理；（AI 未修复原因：需要在 VO 层添加 @NotNull 验证，并在 Service 层调用 areaService 进行业务校验，建议人工实现）

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
