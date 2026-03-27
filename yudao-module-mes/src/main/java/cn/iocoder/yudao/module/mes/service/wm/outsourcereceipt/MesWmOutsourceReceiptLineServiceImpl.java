package cn.iocoder.yudao.module.mes.service.wm.outsourcereceipt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.outsourcereceipt.vo.line.MesWmOutsourceReceiptLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.outsourcereceipt.vo.line.MesWmOutsourceReceiptLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.outsourcereceipt.MesWmOutsourceReceiptLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.outsourcereceipt.MesWmOutsourceReceiptLineMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmQualityStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_OUTSOURCE_RECEIPT_LINE_NOT_EXISTS;

/**
 * MES 委外收货单行 Service 实现类
 */
@Service
@Validated
public class MesWmOutsourceReceiptLineServiceImpl implements MesWmOutsourceReceiptLineService {

    @Resource
    private MesWmOutsourceReceiptLineMapper lineMapper;

    @Resource
    private MesMdItemService itemService;

    @Override
    public Long createOutsourceReceiptLine(MesWmOutsourceReceiptLineSaveReqVO createReqVO) {
        // 校验物料存在
        itemService.validateItemExists(createReqVO.getItemId());

        // 插入
        MesWmOutsourceReceiptLineDO line = BeanUtils.toBean(createReqVO, MesWmOutsourceReceiptLineDO.class);
        // 质量状态自动赋值
        line.setQualityStatus(calculateQualityStatus(line.getIqcCheckFlag()));
        lineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateOutsourceReceiptLine(MesWmOutsourceReceiptLineSaveReqVO updateReqVO) {
        // 校验存在
        validateOutsourceReceiptLineExists(updateReqVO.getId());
        // 校验物料存在
        itemService.validateItemExists(updateReqVO.getItemId());

        // 更新
        MesWmOutsourceReceiptLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmOutsourceReceiptLineDO.class);
        // 质量状态自动赋值
        updateObj.setQualityStatus(calculateQualityStatus(updateObj.getIqcCheckFlag()));
        lineMapper.updateById(updateObj);
    }

    @Override
    public void deleteOutsourceReceiptLine(Long id) {
        // 校验存在
        validateOutsourceReceiptLineExists(id);
        // 删除
        lineMapper.deleteById(id);
    }

    @Override
    public MesWmOutsourceReceiptLineDO getOutsourceReceiptLine(Long id) {
        return lineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmOutsourceReceiptLineDO> getOutsourceReceiptLinePage(MesWmOutsourceReceiptLinePageReqVO pageReqVO) {
        return lineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmOutsourceReceiptLineDO> getOutsourceReceiptLineListByReceiptId(Long receiptId) {
        return lineMapper.selectListByReceiptId(receiptId);
    }

    @Override
    public void deleteOutsourceReceiptLineByReceiptId(Long receiptId) {
        lineMapper.deleteByReceiptId(receiptId);
    }

    private void validateOutsourceReceiptLineExists(Long id) {
        if (lineMapper.selectById(id) == null) {
            throw exception(WM_OUTSOURCE_RECEIPT_LINE_NOT_EXISTS);
        }
    }

    @Override
    public void updateOutsourceReceiptLineDO(MesWmOutsourceReceiptLineDO line) {
        lineMapper.updateById(line);
    }

    @Override
    public void createOutsourceReceiptLineDO(MesWmOutsourceReceiptLineDO line) {
        lineMapper.insert(line);
    }

    /**
     * 根据 iqcCheckFlag，计算质量状态
     *
     * @param iqcCheckFlag 是否需要质检
     * @return 质量状态
     */
    private Integer calculateQualityStatus(Boolean iqcCheckFlag) {
        if (Boolean.TRUE.equals(iqcCheckFlag)) {
            return MesWmQualityStatusEnum.PENDING.getStatus(); // 待检
        }
        return MesWmQualityStatusEnum.PASS.getStatus(); // 不需检验，默认合格
    }

}
