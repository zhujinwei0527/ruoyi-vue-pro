package cn.iocoder.yudao.module.mes.service.wm.itemreceipt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.line.MesWmItemReceiptLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.line.MesWmItemReceiptLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.itemreceipt.MesWmItemReceiptLineMapper;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmItemReceiptStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 采购入库单行 Service 实现类
 */
@Service
@Validated
public class MesWmItemReceiptLineServiceImpl implements MesWmItemReceiptLineService {

    @Resource
    private MesWmItemReceiptLineMapper itemReceiptLineMapper;

    @Resource
    @Lazy
    private MesWmItemReceiptService itemReceiptService;

    @Resource
    private MesWmItemReceiptDetailService itemReceiptDetailService;

    @Override
    public Long createItemReceiptLine(MesWmItemReceiptLineSaveReqVO createReqVO) {
        // 校验父单据存在且为草稿状态
        validateReceiptStatusDraft(createReqVO.getReceiptId());

        // 新增
        MesWmItemReceiptLineDO line = BeanUtils.toBean(createReqVO, MesWmItemReceiptLineDO.class);
        itemReceiptLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateItemReceiptLine(MesWmItemReceiptLineSaveReqVO updateReqVO) {
        // 校验存在
        MesWmItemReceiptLineDO line = validateItemReceiptLineExists(updateReqVO.getId());
        // 校验父单据存在且为草稿状态
        validateReceiptStatusDraft(line.getReceiptId());

        // 更新
        MesWmItemReceiptLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmItemReceiptLineDO.class);
        itemReceiptLineMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItemReceiptLine(Long id) {
        // 校验存在
        validateItemReceiptLineExists(id);

        // 级联删除明细
        itemReceiptDetailService.deleteItemReceiptDetailByLineId(id);
        // 删除
        itemReceiptLineMapper.deleteById(id);
    }

    @Override
    public MesWmItemReceiptLineDO getItemReceiptLine(Long id) {
        return itemReceiptLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmItemReceiptLineDO> getItemReceiptLinePage(MesWmItemReceiptLinePageReqVO pageReqVO) {
        return itemReceiptLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmItemReceiptLineDO> getItemReceiptLineListByReceiptId(Long receiptId) {
        return itemReceiptLineMapper.selectListByReceiptId(receiptId);
    }

    @Override
    public void deleteItemReceiptLineByReceiptId(Long receiptId) {
        itemReceiptLineMapper.deleteByReceiptId(receiptId);
    }

    private MesWmItemReceiptLineDO validateItemReceiptLineExists(Long id) {
        MesWmItemReceiptLineDO line = itemReceiptLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_ITEM_RECEIPT_LINE_NOT_EXISTS);
        }
        return line;
    }

    /**
     * 校验父采购入库单存在且为草稿状态
     */
    private void validateReceiptStatusDraft(Long receiptId) {
        MesWmItemReceiptDO receipt = itemReceiptService.getItemReceipt(receiptId);
        if (receipt == null) {
            throw exception(WM_ITEM_RECEIPT_NOT_EXISTS);
        }
        if (ObjUtil.notEqual(MesWmItemReceiptStatusEnum.PREPARE.getStatus(), receipt.getStatus())) {
            throw exception(WM_ITEM_RECEIPT_STATUS_NOT_PREPARE);
        }
    }

}
