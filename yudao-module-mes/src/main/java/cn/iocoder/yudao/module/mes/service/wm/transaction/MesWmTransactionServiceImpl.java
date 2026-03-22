package cn.iocoder.yudao.module.mes.service.wm.transaction;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.batch.MesWmBatchDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.transaction.MesWmTransactionDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.transaction.MesWmTransactionMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmTransactionTypeEnum;
import cn.iocoder.yudao.module.mes.service.wm.batch.MesWmBatchService;
import cn.iocoder.yudao.module.mes.service.wm.materialstock.MesWmMaterialStockService;
import cn.iocoder.yudao.module.mes.service.wm.transaction.dto.MesWmTransactionSaveReqDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MES 库存事务流水 Service 实现类
 */
@Service
@Validated
public class MesWmTransactionServiceImpl implements MesWmTransactionService {

    @Resource
    private MesWmTransactionMapper transactionMapper;

    @Resource
    private MesWmMaterialStockService materialStockService;
    @Resource
    private MesWmBatchService batchService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTransaction(MesWmTransactionSaveReqDTO reqDTO) {
        // 1.1 校验事务类型
        MesWmTransactionTypeEnum typeEnum = MesWmTransactionTypeEnum.valueOf(reqDTO.getType());
        Assert.notNull(typeEnum, "事务类型({}) 不存在", reqDTO.getType());
        // 1.2 校验 quantity 正负号与事务方向一致（入库必须正数，出库必须负数）
        boolean inbound = typeEnum.isInbound();
        if (inbound) {
            Assert.isTrue(reqDTO.getQuantity().compareTo(BigDecimal.ZERO) > 0,
                    "入库事务数量必须为正数，实际值: {}", reqDTO.getQuantity());
        } else {
            Assert.isTrue(reqDTO.getQuantity().compareTo(BigDecimal.ZERO) < 0,
                    "出库事务数量必须为负数，实际值: {}", reqDTO.getQuantity());
        }
        // 1.3 batchId / batchCode 互补
        completeBatchInfo(reqDTO);

        // 2.1 获取或创建库存记录
        Long materialStockId = materialStockService.getOrCreateMaterialStock(
                reqDTO.getItemId(), reqDTO.getWarehouseId(), reqDTO.getLocationId(), reqDTO.getAreaId(),
                reqDTO.getBatchId(), reqDTO.getVendorId(), reqDTO.getReceiptTime());
        // 2.2 更新库存数量
        boolean checkFlag = ObjUtil.defaultIfNull(reqDTO.getCheckFlag(), true);
        materialStockService.updateMaterialStockQuantity(materialStockId, reqDTO.getQuantity(), checkFlag);

        // 3. 插入事务流水
        MesWmTransactionDO transaction = MesWmTransactionDO.builder()
                .type(reqDTO.getType()).quantity(reqDTO.getQuantity()).transactionTime(LocalDateTime.now())
                .itemId(reqDTO.getItemId()).batchId(reqDTO.getBatchId()).batchCode(reqDTO.getBatchCode())
                .warehouseId(reqDTO.getWarehouseId()).locationId(reqDTO.getLocationId()).areaId(reqDTO.getAreaId())
                .bizType(reqDTO.getBizType()).bizId(reqDTO.getBizId()).bizCode(reqDTO.getBizCode()).bizLineId(reqDTO.getBizLineId())
                .materialStockId(materialStockId).relatedTransactionId(reqDTO.getRelatedTransactionId())
                .build();
        transactionMapper.insert(transaction);
        return transaction.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTransactionList(List<MesWmTransactionSaveReqDTO> reqDTOs) {
        reqDTOs.forEach(this::createTransaction);
    }

    /**
     * batchId / batchCode 互补：有 batchId 无 batchCode 时，查出 batchCode；反之亦然。
     *
     * @param reqDTO 事务请求 DTO
     */
    private void completeBatchInfo(MesWmTransactionSaveReqDTO reqDTO) {
        if (reqDTO.getBatchId() != null && StrUtil.isNotEmpty(reqDTO.getBatchCode())) {
            return;
        }
        // 情况一：有 batchId，补 batchCode
        if (reqDTO.getBatchId() != null) {
            MesWmBatchDO batch = batchService.getBatch(reqDTO.getBatchId());
            if (batch != null) {
                reqDTO.setBatchCode(batch.getCode());
            }
            return;
        }
        // 情况二：有 batchCode，补 batchId
        if (StrUtil.isNotEmpty(reqDTO.getBatchCode())) {
            MesWmBatchDO batch = batchService.getBatchByCode(reqDTO.getBatchCode());
            if (batch != null) {
                reqDTO.setBatchId(batch.getId());
            }
        }
    }

}
