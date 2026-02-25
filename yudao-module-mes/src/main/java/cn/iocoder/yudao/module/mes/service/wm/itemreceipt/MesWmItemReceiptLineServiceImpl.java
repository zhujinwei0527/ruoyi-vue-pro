package cn.iocoder.yudao.module.mes.service.wm.itemreceipt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.line.MesWmItemReceiptLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.line.MesWmItemReceiptLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.itemreceipt.MesWmItemReceiptLineMapper;
import cn.iocoder.yudao.module.mes.service.wm.arrivalnotice.MesWmArrivalNoticeLineService;
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

    @Resource
    @Lazy
    private MesWmArrivalNoticeLineService arrivalNoticeLineService;

    @Override
    public Long createItemReceiptLine(MesWmItemReceiptLineSaveReqVO createReqVO) {
        // 校验父单据存在且为可编辑状态
        MesWmItemReceiptDO receipt = itemReceiptService.validateItemReceiptEditable(createReqVO.getReceiptId());
        // 校验关联到货通知单行存在
        validateArrivalNoticeLine(receipt, createReqVO.getArrivalNoticeLineId());

        // 新增
        MesWmItemReceiptLineDO line = BeanUtils.toBean(createReqVO, MesWmItemReceiptLineDO.class);
        itemReceiptLineMapper.insert(line);

        // TODO @芋艿：【暂时不处理】wmBatchService 需要生成下；基于 batchCode
        return line.getId();
    }

    @Override
    public void updateItemReceiptLine(MesWmItemReceiptLineSaveReqVO updateReqVO) {
        // 校验存在
        MesWmItemReceiptLineDO line = validateItemReceiptLineExists(updateReqVO.getId());
        // 校验父单据存在且为可编辑状态
        MesWmItemReceiptDO receipt = itemReceiptService.validateItemReceiptEditable(line.getReceiptId());
        // 校验关联到货通知单行存在
        validateArrivalNoticeLine(receipt, updateReqVO.getArrivalNoticeLineId());

        // 更新
        MesWmItemReceiptLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmItemReceiptLineDO.class);
        itemReceiptLineMapper.updateById(updateObj);

        // TODO @芋艿：【暂时不处理】wmBatchService 需要生成下；基于 batchCode
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

    /**
     * 校验到货通知单行
     *
     * @param receipt 入库单
     * @param arrivalNoticeLineId 到货通知单行编号
     */
    private void validateArrivalNoticeLine(MesWmItemReceiptDO receipt, Long arrivalNoticeLineId) {
        // 情况一：如果入库单关联了到货通知单，则必须关联到货通知单行
        if (receipt.getNoticeId() != null) {
            if (arrivalNoticeLineId == null) {
                throw exception(WM_ITEM_RECEIPT_LINE_ARRIVAL_NOTICE_LINE_REQUIRED);
            }
            arrivalNoticeLineService.validateArrivalNoticeLineExists(
                    arrivalNoticeLineId, receipt.getNoticeId());
            return;
        }

        // 情况二：如果入库单没有关联到货通知单，则不允许关联到货通知单行
        if (arrivalNoticeLineId != null) {
            throw exception(WM_ITEM_RECEIPT_LINE_ARRIVAL_NOTICE_LINE_NOT_ALLOWED);
        }
    }

    private MesWmItemReceiptLineDO validateItemReceiptLineExists(Long id) {
        MesWmItemReceiptLineDO line = itemReceiptLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_ITEM_RECEIPT_LINE_NOT_EXISTS);
        }
        return line;
    }

}
