package cn.iocoder.yudao.module.mes.service.wm.itemreceipt;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.detail.MesWmItemReceiptDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.itemreceipt.MesWmItemReceiptDetailMapper;
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
        // TODO @AI：warehouseId、locationId、areaId 存在，并且是父子关系！最好在 areaService 中提供一个方法，校验这三个 ID 的关系！（并且存在）
        // 校验父单据存在且为草稿状态
        itemReceiptService.validateItemReceiptEditable(createReqVO.getReceiptId());
        // TODO @AI：超过数量验证；

        // TODO @芋艿：【后续搞】不允许物资混放

        MesWmItemReceiptDetailDO detail = BeanUtils.toBean(createReqVO, MesWmItemReceiptDetailDO.class);
        itemReceiptDetailMapper.insert(detail);
        return detail.getId();
    }

    @Override
    public void updateItemReceiptDetail(MesWmItemReceiptDetailSaveReqVO updateReqVO) {
        // TODO @AI：warehouseId、locationId、areaId 存在，并且是父子关系！最好在 areaService 中提供一个方法，校验这三个 ID 的关系！（并且存在）
        // 校验存在
        MesWmItemReceiptDetailDO detail = validateItemReceiptDetailExists(updateReqVO.getId());
        // 校验父单据存在且为草稿状态
        itemReceiptService.validateItemReceiptEditable(detail.getReceiptId());
        // TODO @AI：超过数量验证；

        // TODO @芋艿：【后续搞】不允许物资混放

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

}
