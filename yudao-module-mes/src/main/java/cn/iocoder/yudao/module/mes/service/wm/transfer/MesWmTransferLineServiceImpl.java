package cn.iocoder.yudao.module.mes.service.wm.transfer;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.line.MesWmTransferLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.transfer.MesWmTransferLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.transfer.MesWmTransferLineMapper;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseAreaService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_TRANSFER_LINE_NOT_EXISTS;

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
    @Resource
    private MesWmWarehouseAreaService warehouseAreaService;

    @Override
    public Long createTransferLine(MesWmTransferLineSaveReqVO createReqVO) {
        // 校验父数据可编辑
        transferService.validateTransferEditable(createReqVO.getTransferId());
        // 校验产品存在
        itemService.validateItemExists(createReqVO.getItemId());
        // 校验来源仓库、库区、库位的关联关系
        warehouseAreaService.validateWarehouseAreaExists(createReqVO.getFromWarehouseId(),
                createReqVO.getFromLocationId(), createReqVO.getFromAreaId());

        // 插入
        MesWmTransferLineDO line = BeanUtils.toBean(createReqVO, MesWmTransferLineDO.class);
        transferLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateTransferLine(MesWmTransferLineSaveReqVO updateReqVO) {
        // 校验存在
        validateTransferLineExists(updateReqVO.getId());
        // 校验父数据可编辑
        transferService.validateTransferEditable(updateReqVO.getTransferId());
        // 校验产品存在
        itemService.validateItemExists(updateReqVO.getItemId());
        // 校验来源仓库、库区、库位的关联关系
        warehouseAreaService.validateWarehouseAreaExists(updateReqVO.getFromWarehouseId(),
                updateReqVO.getFromLocationId(), updateReqVO.getFromAreaId());

        // 更新
        MesWmTransferLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmTransferLineDO.class);
        transferLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteTransferLine(Long id) {
        // 校验存在
        validateTransferLineExists(id);
        // 删除
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
