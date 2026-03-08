package cn.iocoder.yudao.module.mes.service.wm.transfer;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transfer.vo.detail.MesWmTransferDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.transfer.MesWmTransferDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.transfer.MesWmTransferLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.transfer.MesWmTransferDetailMapper;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseAreaService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_TRANSFER_DETAIL_NOT_EXISTS;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_TRANSFER_DETAIL_QUANTITY_EXCEED;

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

    @Resource
    private MesWmWarehouseAreaService warehouseAreaService;

    @Override
    public Long createTransferDetail(MesWmTransferDetailSaveReqVO createReqVO) {
        // 校验父数据存在
        MesWmTransferLineDO line = transferLineService.validateTransferLineExists(createReqVO.getLineId());
        // 校验仓库、库区、库位的关联关系
        warehouseAreaService.validateWarehouseAreaExists(
                createReqVO.getToWarehouseId(), createReqVO.getToLocationId(), createReqVO.getToAreaId());
        // 校验明细总数量不超过行数量
        validateDetailQuantityNotExceed(createReqVO.getLineId(), createReqVO.getQuantity(), null, line);
        // TODO @芋艿：不能混货

        // 插入
        MesWmTransferDetailDO detail = BeanUtils.toBean(createReqVO, MesWmTransferDetailDO.class);
        transferDetailMapper.insert(detail);
        return detail.getId();
    }

    @Override
    public void updateTransferDetail(MesWmTransferDetailSaveReqVO updateReqVO) {
        // 校验存在
        validateTransferDetailExists(updateReqVO.getId());
        // 校验父数据存在
        MesWmTransferLineDO line = transferLineService.validateTransferLineExists(updateReqVO.getLineId());
        // 校验仓库、库区、库位的关联关系
        warehouseAreaService.validateWarehouseAreaExists(
                updateReqVO.getToWarehouseId(), updateReqVO.getToLocationId(), updateReqVO.getToAreaId());
        // 校验明细总数量不超过行数量（排除自身）
        validateDetailQuantityNotExceed(updateReqVO.getLineId(), updateReqVO.getQuantity(), updateReqVO.getId(), line);
        // TODO @芋艿：不能混货

        // 更新
        MesWmTransferDetailDO updateObj = BeanUtils.toBean(updateReqVO, MesWmTransferDetailDO.class);
        transferDetailMapper.updateById(updateObj);
    }

    @Override
    public void deleteTransferDetail(Long id) {
        // 校验存在
        validateTransferDetailExists(id);
        // 删除
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

    /**
     * 校验明细总数量不超过行数量
     *
     * @param lineId 行 ID
     * @param newQuantity 本次新增/修改的数量
     * @param excludeDetailId 排除的明细 ID（更新时排除自身，新增时传 null）
     * @param line 调拨单行
     */
    private void validateDetailQuantityNotExceed(Long lineId, BigDecimal newQuantity,
                                                 Long excludeDetailId, MesWmTransferLineDO line) {
        // 计算已有明细总数量（排除自身）
        List<MesWmTransferDetailDO> details = transferDetailMapper.selectListByLineId(lineId);
        BigDecimal existingTotal = CollectionUtils.getSumValue(details,
                detail -> excludeDetailId != null && excludeDetailId.equals(detail.getId())
                        ? BigDecimal.ZERO : detail.getQuantity(),
                BigDecimal::add, BigDecimal.ZERO);
        // 校验：已有 + 本次 <= 行数量
        if (existingTotal.add(newQuantity).compareTo(line.getQuantity()) > 0) {
            throw exception(WM_TRANSFER_DETAIL_QUANTITY_EXCEED);
        }
    }

}
