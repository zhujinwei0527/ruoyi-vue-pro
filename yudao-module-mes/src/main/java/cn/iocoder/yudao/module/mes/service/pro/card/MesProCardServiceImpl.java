package cn.iocoder.yudao.module.mes.service.pro.card;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.card.vo.MesProCardPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.card.vo.MesProCardSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.card.MesProCardDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.card.MesProCardMapper;
import cn.iocoder.yudao.module.mes.enums.wm.BarcodeBizTypeEnum;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import cn.iocoder.yudao.module.mes.service.wm.barcode.MesWmBarcodeService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.PRO_CARD_CODE_DUPLICATE;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.PRO_CARD_NOT_EXISTS;

/**
 * MES 生产流转卡 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesProCardServiceImpl implements MesProCardService {

    @Resource
    private MesProCardMapper cardMapper;

    // DONE @AI：会用对应的 service
    @Resource
    @Lazy
    private MesProCardProcessService cardProcessService;

    @Resource
    private MesProWorkOrderService workOrderService;
    @Resource
    private MesWmBarcodeService barcodeService;

    @Override
    public Long createCard(MesProCardSaveReqVO createReqVO) {
        // 1.1 校验编码唯一
        validateCardCodeUnique(null, createReqVO.getCode());
        // 1.2 校验工单存在
        workOrderService.validateWorkOrderExists(createReqVO.getWorkOrderId());

        // 2. 插入
        MesProCardDO card = BeanUtils.toBean(createReqVO, MesProCardDO.class);
        cardMapper.insert(card);

        // 3. 自动生成条码
        barcodeService.autoGenerateBarcode(BarcodeBizTypeEnum.PROCARD.getValue(),
                card.getId(), card.getCode(), card.getCode());
        return card.getId();
    }

    @Override
    public void updateCard(MesProCardSaveReqVO updateReqVO) {
        // 1.1 校验存在
        validateCardExists(updateReqVO.getId());
        // 1.2 校验编码唯一
        validateCardCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 2. 更新
        MesProCardDO updateObj = BeanUtils.toBean(updateReqVO, MesProCardDO.class);
        cardMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCard(Long id) {
        // 1. 校验存在
        validateCardExists(id);

        // 2. 删除流转卡 + 级联删除工序记录
        cardMapper.deleteById(id);
        cardProcessService.deleteCardProcessByCardId(id);
    }

    @Override
    public MesProCardDO getCard(Long id) {
        return cardMapper.selectById(id);
    }

    @Override
    public PageResult<MesProCardDO> getCardPage(MesProCardPageReqVO pageReqVO) {
        return cardMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    @Override
    public void validateCardExists(Long id) {
        if (cardMapper.selectById(id) == null) {
            throw exception(PRO_CARD_NOT_EXISTS);
        }
    }

    private void validateCardCodeUnique(Long id, String code) {
        if (code == null) {
            return;
        }
        MesProCardDO card = cardMapper.selectByCode(code);
        if (card == null) {
            return;
        }
        if (ObjUtil.notEqual(card.getId(), id)) {
            throw exception(PRO_CARD_CODE_DUPLICATE);
        }
    }

}
