package cn.iocoder.yudao.module.mes.service.wm.itemreceipt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.detail.MesWmItemReceiptDetailPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.detail.MesWmItemReceiptDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.itemreceipt.MesWmItemReceiptDetailMapper;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmItemReceiptStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 采购入库明细 Service 实现类
 */
@Service
@Validated
public class MesWmItemReceiptDetailServiceImpl implements MesWmItemReceiptDetailService {

    @Resource
    private MesWmItemReceiptDetailMapper itemReceiptDetailMapper;

    @Resource
    @Lazy
    private MesWmItemReceiptService itemReceiptService;

    @Override
    public Long createItemReceiptDetail(MesWmItemReceiptDetailSaveReqVO createReqVO) {
        // 校验父单据存在且为草稿状态
        validateReceiptStatusForDetailEdit(createReqVO.getReceiptId());

        MesWmItemReceiptDetailDO detail = BeanUtils.toBean(createReqVO, MesWmItemReceiptDetailDO.class);
        itemReceiptDetailMapper.insert(detail);
        return detail.getId();
    }

    @Override
    public void updateItemReceiptDetail(MesWmItemReceiptDetailSaveReqVO updateReqVO) {
        // 校验存在
        MesWmItemReceiptDetailDO detail = validateItemReceiptDetailExists(updateReqVO.getId());
        // 校验父单据存在且为草稿状态
        validateReceiptStatusForDetailEdit(detail.getReceiptId());

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
    public List<MesWmItemReceiptDetailDO> getItemReceiptDetailListByReceiptId(Long receiptId) {
        return itemReceiptDetailMapper.selectListByReceiptId(receiptId);
    }

    @Override
    public List<MesWmItemReceiptDetailDO> getItemReceiptDetailListByLineId(Long lineId) {
        return itemReceiptDetailMapper.selectListByLineId(lineId);
    }

    @Override
    public void deleteItemReceiptDetailByLineId(Long lineId) {
        itemReceiptDetailMapper.deleteByLineId(lineId);
    }

    @Override
    public void deleteItemReceiptDetailByReceiptId(Long receiptId) {
        itemReceiptDetailMapper.deleteByReceiptId(receiptId);
    }

    private MesWmItemReceiptDetailDO validateItemReceiptDetailExists(Long id) {
        MesWmItemReceiptDetailDO detail = itemReceiptDetailMapper.selectById(id);
        if (detail == null) {
            throw exception(WM_ITEM_RECEIPT_DETAIL_NOT_EXISTS);
        }
        return detail;
    }

    // TODO @AI：在 itemReceiptService 里，封装一个这个方法；最好不要是 fordetail，而不是 prepare + approving 状态；
    /**
     * 校验父采购入库单存在且允许编辑明细（草稿或待上架状态）
     */
    private void validateReceiptStatusForDetailEdit(Long receiptId) {
        MesWmItemReceiptDO receipt = itemReceiptService.getItemReceipt(receiptId);
        if (receipt == null) {
            throw exception(WM_ITEM_RECEIPT_NOT_EXISTS);
        }
        // TODO @AI：在 ObjectUtils 里，封装一个 notEquals 方法，简化代码；
        if (ObjUtil.notEqual(MesWmItemReceiptStatusEnum.PREPARE.getStatus(), receipt.getStatus())
                && ObjUtil.notEqual(MesWmItemReceiptStatusEnum.APPROVING.getStatus(), receipt.getStatus())) {
            throw exception(WM_ITEM_RECEIPT_STATUS_NOT_PREPARE);
        }
    }

}
