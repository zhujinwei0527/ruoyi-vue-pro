package cn.iocoder.yudao.module.mes.service.wm.itemreceipt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.line.MesWmItemReceiptLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.itemreceipt.vo.line.MesWmItemReceiptLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.itemreceipt.MesWmItemReceiptDetailMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.itemreceipt.MesWmItemReceiptLineMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_ITEM_RECEIPT_LINE_NOT_EXISTS;

/**
 * MES 采购入库单行 Service 实现类
 */
@Service
@Validated
public class MesWmItemReceiptLineServiceImpl implements MesWmItemReceiptLineService {

    @Resource
    private MesWmItemReceiptLineMapper itemReceiptLineMapper;

    @Resource
    private MesWmItemReceiptDetailMapper itemReceiptDetailMapper;

    @Override
    public Long createItemReceiptLine(MesWmItemReceiptLineSaveReqVO createReqVO) {
        // TODO @AI：校验关联字段；

        // 新增
        MesWmItemReceiptLineDO line = BeanUtils.toBean(createReqVO, MesWmItemReceiptLineDO.class);
        itemReceiptLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateItemReceiptLine(MesWmItemReceiptLineSaveReqVO updateReqVO) {
        // 校验存在
        validateItemReceiptLineExists(updateReqVO.getId());
        // TODO @AI：校验关联字段；

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
        itemReceiptDetailMapper.deleteByLineId(id);
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

    private void validateItemReceiptLineExists(Long id) {
        if (itemReceiptLineMapper.selectById(id) == null) {
            throw exception(WM_ITEM_RECEIPT_LINE_NOT_EXISTS);
        }
    }

}
