package cn.iocoder.yudao.module.mes.service.wm.miscreceipt;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.miscreceipt.vo.line.MesWmMiscReceiptLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.miscreceipt.MesWmMiscReceiptLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.miscreceipt.MesWmMiscReceiptLineMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_MISC_RECEIPT_LINE_NOT_EXISTS;

/**
 * MES 杂项入库单行 Service 实现类
 */
@Service
@Validated
public class MesWmMiscReceiptLineServiceImpl implements MesWmMiscReceiptLineService {

    @Resource
    private MesWmMiscReceiptLineMapper miscReceiptLineMapper;

    @Override
    public Long createMiscReceiptLine(MesWmMiscReceiptLineSaveReqVO createReqVO) {
        // 插入
        MesWmMiscReceiptLineDO line = BeanUtils.toBean(createReqVO, MesWmMiscReceiptLineDO.class);
        miscReceiptLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateMiscReceiptLine(MesWmMiscReceiptLineSaveReqVO updateReqVO) {
        // 校验存在
        validateMiscReceiptLineExists(updateReqVO.getId());

        // 更新
        MesWmMiscReceiptLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmMiscReceiptLineDO.class);
        miscReceiptLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteMiscReceiptLine(Long id) {
        // 校验存在
        validateMiscReceiptLineExists(id);

        // 删除
        miscReceiptLineMapper.deleteById(id);
    }

    @Override
    public MesWmMiscReceiptLineDO getMiscReceiptLine(Long id) {
        return miscReceiptLineMapper.selectById(id);
    }

    @Override
    public List<MesWmMiscReceiptLineDO> getMiscReceiptLineListByReceiptId(Long receiptId) {
        return miscReceiptLineMapper.selectListByReceiptId(receiptId);
    }

    @Override
    public MesWmMiscReceiptLineDO validateMiscReceiptLineExists(Long id) {
        MesWmMiscReceiptLineDO line = miscReceiptLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_MISC_RECEIPT_LINE_NOT_EXISTS);
        }
        return line;
    }

}
