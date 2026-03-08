package cn.iocoder.yudao.module.mes.service.wm.transfer;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.detail.MesWmTransferDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.transfer.MesWmTransferDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.transfer.MesWmTransferDetailMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_TRANSFER_DETAIL_NOT_EXISTS;

// TODO @AI：注释完善下
/**
 * MES 调拨明细 Service 实现类
 */
@Service
@Validated
public class MesWmTransferDetailServiceImpl implements MesWmTransferDetailService {

    @Resource
    private MesWmTransferDetailMapper transferDetailMapper;

    @Resource
    @Lazy
    private MesWmTransferLineService transferLineService;

    @Override
    public Long createTransferDetail(MesWmTransferDetailSaveReqVO createReqVO) {
        transferLineService.validateTransferLineExists(createReqVO.getLineId());
        // TODO @AI：看看还有没关联的数据要校验的；包括状态的

        MesWmTransferDetailDO detail = BeanUtils.toBean(createReqVO, MesWmTransferDetailDO.class);
        transferDetailMapper.insert(detail);
        return detail.getId();
    }

    @Override
    public void updateTransferDetail(MesWmTransferDetailSaveReqVO updateReqVO) {
        validateTransferDetailExists(updateReqVO.getId());
        transferLineService.validateTransferLineExists(updateReqVO.getLineId());
        // TODO @AI：看看还有没关联的数据要校验的；包括状态的

        MesWmTransferDetailDO updateObj = BeanUtils.toBean(updateReqVO, MesWmTransferDetailDO.class);
        transferDetailMapper.updateById(updateObj);
    }

    @Override
    public void deleteTransferDetail(Long id) {
        // TODO @AI：看看还有没关联的数据要校验的；包括状态的
        validateTransferDetailExists(id);
        transferDetailMapper.deleteById(id);
    }

    @Override
    public MesWmTransferDetailDO getTransferDetail(Long id) {
        return transferDetailMapper.selectById(id);
    }

    @Override
    public List<MesWmTransferDetailDO> getTransferDetailListByLineId(Long lineId) {
        return transferDetailMapper.selectListByLineId(lineId);
    }

    @Override
    public List<MesWmTransferDetailDO> getTransferDetailListByTransferId(Long transferId) {
        return transferDetailMapper.selectListByTransferId(transferId);
    }

    @Override
    public void deleteTransferDetailByTransferId(Long transferId) {
        getTransferDetailListByTransferId(transferId)
                .forEach(detail -> transferDetailMapper.deleteById(detail.getId()));
    }

    private void validateTransferDetailExists(Long id) {
        if (transferDetailMapper.selectById(id) == null) {
            throw exception(WM_TRANSFER_DETAIL_NOT_EXISTS);
        }
    }

}
