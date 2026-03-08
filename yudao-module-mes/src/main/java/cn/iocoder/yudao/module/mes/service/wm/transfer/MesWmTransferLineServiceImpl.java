package cn.iocoder.yudao.module.mes.service.wm.transfer;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.line.MesWmTransferLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.transfer.MesWmTransferLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.transfer.MesWmTransferLineMapper;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_TRANSFER_LINE_NOT_EXISTS;

// TODO @AI：注释完善下
/**
 * MES 转移单行 Service 实现类
 */
@Service
@Validated
public class MesWmTransferLineServiceImpl implements MesWmTransferLineService {

    @Resource
    private MesWmTransferLineMapper transferLineMapper;

    @Resource
    @Lazy
    private MesWmTransferService transferService;
    @Resource
    private MesMdItemService itemService;

    @Override
    public Long createTransferLine(MesWmTransferLineSaveReqVO createReqVO) {
        transferService.validateTransferEditable(createReqVO.getTransferId());
        itemService.validateItemExists(createReqVO.getItemId());
        // TODO @AI：看看还有没关联的数据要校验的；包括状态的

        MesWmTransferLineDO line = BeanUtils.toBean(createReqVO, MesWmTransferLineDO.class);
        transferLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateTransferLine(MesWmTransferLineSaveReqVO updateReqVO) {
        validateTransferLineExists(updateReqVO.getId());
        transferService.validateTransferEditable(updateReqVO.getTransferId());
        itemService.validateItemExists(updateReqVO.getItemId());
        // TODO @AI：看看还有没关联的数据要校验的；包括状态的

        MesWmTransferLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmTransferLineDO.class);
        transferLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteTransferLine(Long id) {
        validateTransferLineExists(id);
        transferLineMapper.deleteById(id);
    }

    @Override
    public MesWmTransferLineDO getTransferLine(Long id) {
        return transferLineMapper.selectById(id);
    }

    @Override
    public List<MesWmTransferLineDO> getTransferLineListByTransferId(Long transferId) {
        return transferLineMapper.selectListByTransferId(transferId);
    }

    @Override
    public void deleteTransferLineByTransferId(Long transferId) {
        getTransferLineListByTransferId(transferId)
                .forEach(line -> transferLineMapper.deleteById(line.getId()));
    }

    @Override
    public MesWmTransferLineDO validateTransferLineExists(Long id) {
        MesWmTransferLineDO line = transferLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_TRANSFER_LINE_NOT_EXISTS);
        }
        return line;
    }

}
