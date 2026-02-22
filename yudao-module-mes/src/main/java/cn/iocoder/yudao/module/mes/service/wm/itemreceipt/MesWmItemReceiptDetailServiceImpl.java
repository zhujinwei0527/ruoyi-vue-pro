package cn.iocoder.yudao.module.mes.service.wm.itemreceipt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.detail.MesWmItemReceiptDetailPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.detail.MesWmItemReceiptDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.itemreceipt.MesWmItemReceiptDetailMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_ITEM_RECEIPT_DETAIL_NOT_EXISTS;

/**
 * MES 采购入库明细 Service 实现类
 */
@Service
@Validated
public class MesWmItemReceiptDetailServiceImpl implements MesWmItemReceiptDetailService {

    @Resource
    private MesWmItemReceiptDetailMapper itemReceiptDetailMapper;

    @Override
    public Long createItemReceiptDetail(MesWmItemReceiptDetailSaveReqVO createReqVO) {
        // TODO @AI：校验关联字段；
        MesWmItemReceiptDetailDO detail = BeanUtils.toBean(createReqVO, MesWmItemReceiptDetailDO.class);
        itemReceiptDetailMapper.insert(detail);
        return detail.getId();
    }

    @Override
    public void updateItemReceiptDetail(MesWmItemReceiptDetailSaveReqVO updateReqVO) {
        // 校验存在
        validateItemReceiptDetailExists(updateReqVO.getId());
        // TODO @AI：校验关联字段；

        // 更新
        MesWmItemReceiptDetailDO updateObj = BeanUtils.toBean(updateReqVO, MesWmItemReceiptDetailDO.class);
        itemReceiptDetailMapper.updateById(updateObj);
    }

    @Override
    public void deleteItemReceiptDetail(Long id) {
        // 校验存在
        validateItemReceiptDetailExists(id);
        // 删除
        itemReceiptDetailMapper.deleteById(id);
    }

    @Override
    public MesWmItemReceiptDetailDO getItemReceiptDetail(Long id) {
        return itemReceiptDetailMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmItemReceiptDetailDO> getItemReceiptDetailPage(MesWmItemReceiptDetailPageReqVO pageReqVO) {
        return itemReceiptDetailMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmItemReceiptDetailDO> getItemReceiptDetailListByLineId(Long lineId) {
        return itemReceiptDetailMapper.selectListByLineId(lineId);
    }

    private void validateItemReceiptDetailExists(Long id) {
        if (itemReceiptDetailMapper.selectById(id) == null) {
            throw exception(WM_ITEM_RECEIPT_DETAIL_NOT_EXISTS);
        }
    }

}
