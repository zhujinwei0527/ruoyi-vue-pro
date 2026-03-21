package cn.iocoder.yudao.module.mes.service.wm.productproduce;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.batch.vo.MesWmBatchGenerateReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.MesWmProductProducePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.MesWmProductProduceSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.feedback.MesProFeedbackDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.batch.MesWmBatchDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productproduce.MesWmProductProduceDetailMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productproduce.MesWmProductProduceLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productproduce.MesWmProductProduceMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmProductProduceStatusEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmQualityStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import cn.iocoder.yudao.module.mes.service.wm.batch.MesWmBatchService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 生产入库单 Service 实现类
 */
@Service
@Validated
public class MesWmProductProduceServiceImpl implements MesWmProductProduceService {

    @Resource
    private MesWmProductProduceMapper productProduceMapper;
    // TODO @芋艿：需要优化，不要直接调用对方的 mapper
    @Resource
    private MesWmProductProduceLineMapper produceLineMapper;
    // TODO @芋艿：需要优化，不要直接调用对方的 mapper
    @Resource
    private MesWmProductProduceDetailMapper produceDetailMapper;

    @Resource
    private MesWmProductProduceLineService produceLineService;
    @Resource
    private MesWmProductProduceDetailService produceDetailService;
    @Resource
    private MesMdWorkstationService workstationService;
    @Resource
    private MesProWorkOrderService workOrderService;
    @Resource
    private MesWmBatchService batchService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProductProduce(MesWmProductProduceSaveReqVO createReqVO) {
        // 1. 校验关联数据
        if (createReqVO.getWorkOrderId() != null) {
            workOrderService.validateWorkOrderExists(createReqVO.getWorkOrderId());
        }
        if (createReqVO.getWorkstationId() != null) {
            workstationService.validateWorkstationExists(createReqVO.getWorkstationId());
        }

        // 2. 插入主表
        MesWmProductProduceDO produce = BeanUtils.toBean(createReqVO, MesWmProductProduceDO.class);
        produce.setStatus(MesWmProductProduceStatusEnum.PREPARE.getStatus());
        productProduceMapper.insert(produce);
        return produce.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductProduce(MesWmProductProduceSaveReqVO updateReqVO) {
        // 1.1 校验存在 + 准备中状态
        validateProductProduceExistsAndPrepare(updateReqVO.getId());
        // 1.2 校验关联数据
        if (updateReqVO.getWorkOrderId() != null) {
            workOrderService.validateWorkOrderExists(updateReqVO.getWorkOrderId());
        }
        if (updateReqVO.getWorkstationId() != null) {
            workstationService.validateWorkstationExists(updateReqVO.getWorkstationId());
        }

        // 2. 更新主表
        MesWmProductProduceDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductProduceDO.class);
        productProduceMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductProduce(Long id) {
        // 1. 校验存在 + 准备中状态
        validateProductProduceExistsAndPrepare(id);

        // 2.1 级联删除明细
        produceDetailService.deleteProductProduceDetailByProduceId(id);
        // 2.2 级联删除行
        produceLineService.deleteProductProduceLineByProduceId(id);
        // 2.3 删除主表
        productProduceMapper.deleteById(id);
    }

    @Override
    public MesWmProductProduceDO getProductProduce(Long id) {
        return productProduceMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmProductProduceDO> getProductProducePage(MesWmProductProducePageReqVO pageReqVO) {
        return productProduceMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishProductProduce(Long id) {
        // 1.1 校验存在 + 草稿状态
        validateProductProduceExistsAndPrepare(id);
        // 1.2 校验至少有一条行
        List<MesWmProductProduceLineDO> lines = produceLineService.getProductProduceLineListByProduceId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_PRODUCT_PRODUCE_NO_LINE);
        }

        // 2. 校验每行明细数量之和等于行数量
        for (MesWmProductProduceLineDO line : lines) {
            List<MesWmProductProduceDetailDO> details = produceDetailService.getProductProduceDetailListByLineId(line.getId());
            BigDecimal totalDetailQty = CollectionUtils.getSumValue(details,
                    MesWmProductProduceDetailDO::getQuantity, BigDecimal::add, BigDecimal.ZERO);
            if (line.getQuantity() != null && totalDetailQty.compareTo(line.getQuantity()) != 0) {
                throw exception(WM_PRODUCT_PRODUCE_DETAIL_QUANTITY_MISMATCH);
            }
        }

        // TODO @AI（from codex）：对齐，这里还需要按产出明细执行库存入账，而不是只校验明细并更新单据状态。

        // 3. 更新入库单状态
        productProduceMapper.updateById(new MesWmProductProduceDO()
                .setId(id).setStatus(MesWmProductProduceStatusEnum.FINISHED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelProductProduce(Long id) {
        // 1. 校验存在
        MesWmProductProduceDO produce = validateProductProduceExists(id);
        // 已完成和已取消不允许取消
        if (ObjectUtils.equalsAny(produce.getStatus(),
                MesWmProductProduceStatusEnum.FINISHED.getStatus(),
                MesWmProductProduceStatusEnum.CANCELED.getStatus())) {
            throw exception(WM_PRODUCT_PRODUCE_CANCEL_NOT_ALLOWED);
        }

        // 2. 取消
        productProduceMapper.updateById(new MesWmProductProduceDO()
                .setId(id).setStatus(MesWmProductProduceStatusEnum.CANCELED.getStatus()));
    }

    @Override
    public Boolean checkProductProduceQuantity(Long id) {
        List<MesWmProductProduceLineDO> lines = produceLineService.getProductProduceLineListByProduceId(id);
        for (MesWmProductProduceLineDO line : lines) {
            List<MesWmProductProduceDetailDO> details = produceDetailService.getProductProduceDetailListByLineId(line.getId());
            BigDecimal totalDetailQty = CollectionUtils.getSumValue(details,
                    MesWmProductProduceDetailDO::getQuantity, BigDecimal::add, BigDecimal.ZERO);
            if (line.getQuantity() != null && totalDetailQty.compareTo(line.getQuantity()) != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MesWmProductProduceDO generateProductProduce(MesProFeedbackDO feedback, boolean checkFlag) {
        // 0. 查询关联的工单（用于获取 clientId 等信息）
        MesProWorkOrderDO workOrder = workOrderService.getWorkOrder(feedback.getWorkOrderId());

        // 1. 创建产出单头
        MesWmProductProduceDO produce = MesWmProductProduceDO.builder()
                .workOrderId(feedback.getWorkOrderId()).feedbackId(feedback.getId()).taskId(feedback.getTaskId())
                .workstationId(feedback.getWorkstationId()).processId(feedback.getProcessId())
                .produceDate(LocalDateTime.now()).status(MesWmProductProduceStatusEnum.PREPARE.getStatus())
                .build();
        productProduceMapper.insert(produce);

        // 2. 获取或生成批次
        MesWmBatchGenerateReqVO batchReqVO = new MesWmBatchGenerateReqVO()
                .setItemId(feedback.getItemId())
                .setProduceDate(LocalDate.now().atStartOfDay()) // 截断到当天零点，确保同一天的报工生成相同批次号
                .setExpireDate(feedback.getExpireDate())
                .setWorkOrderId(feedback.getWorkOrderId())
                .setClientId(workOrder != null ? workOrder.getClientId() : null)
                .setWorkstationId(feedback.getWorkstationId())
                .setLotNumber(feedback.getLotNumber());
        // TODO @AI（from codex）：对齐 批次生成还需要带上工单来源单号（salesOrderCode / coCode 对应 orderSourceCode）；
        //  否则启用该维度的批次规则会生成错误批次或直接缺参失败。
        MesWmBatchDO batch = batchService.getOrGenerateBatchCode(batchReqVO);
        Long batchId = batch != null ? batch.getId() : null;
        String batchCode = batch != null ? batch.getCode() : null;

        // 3. 根据是否需要检验分支处理
        if (checkFlag) {
            // 3.1 需要检验：创建一条行（质量状态=待检验），不生成明细
            MesWmProductProduceLineDO line = buildProduceLine(produce, feedback, batchId, batchCode,
                    feedback.getFeedbackQuantity(), MesWmQualityStatusEnum.PENDING.getStatus());
            produceLineMapper.insert(line);
            // TODO @芋艿：先不生成明细行，等待检验完成时，再根据行的质量状态生成明细行
        } else {
            // 3.2 无需检验：按合格品/不合格品各生成一行 + 明细
            BigDecimal qualifiedQty = ObjectUtil.defaultIfNull(feedback.getQualifiedQuantity(), BigDecimal.ZERO);
            BigDecimal unqualifiedQty = ObjectUtil.defaultIfNull(feedback.getUnqualifiedQuantity(), BigDecimal.ZERO);
            // 3.2.1 不合格品行 + 明细
            if (unqualifiedQty.compareTo(BigDecimal.ZERO) > 0) {
                MesWmProductProduceLineDO unqualifiedLine = buildProduceLine(produce, feedback, batchId, batchCode,
                        unqualifiedQty, MesWmQualityStatusEnum.FAIL.getStatus());
                produceLineMapper.insert(unqualifiedLine);
                MesWmProductProduceDetailDO unqualifiedDetail = buildProduceDetail(produce, feedback, batchId, batchCode,
                        unqualifiedLine.getId(), unqualifiedQty);
                produceDetailMapper.insert(unqualifiedDetail);
            }
            // 3.2.2 合格品行 + 明细
            if (qualifiedQty.compareTo(BigDecimal.ZERO) > 0) {
                MesWmProductProduceLineDO qualifiedLine = buildProduceLine(produce, feedback, batchId, batchCode,
                        qualifiedQty, MesWmQualityStatusEnum.PASS.getStatus());
                produceLineMapper.insert(qualifiedLine);
                MesWmProductProduceDetailDO qualifiedDetail = buildProduceDetail(produce, feedback, batchId, batchCode,
                        qualifiedLine.getId(), qualifiedQty);
                produceDetailMapper.insert(qualifiedDetail);
            }
        }
        return produce;
    }

    private MesWmProductProduceLineDO buildProduceLine(MesWmProductProduceDO produce, MesProFeedbackDO feedback,
                                                       Long batchId, String batchCode,
                                                       BigDecimal quantity, Integer qualityStatus) {
        return MesWmProductProduceLineDO.builder()
                .produceId(produce.getId()).feedbackId(feedback.getId())
                .itemId(feedback.getItemId()).quantity(quantity)
                .batchId(batchId).batchCode(batchCode)
                .expireDate(feedback.getExpireDate()).lotNumber(feedback.getLotNumber())
                .qualityStatus(qualityStatus)
                .build();
    }

    private MesWmProductProduceDetailDO buildProduceDetail(MesWmProductProduceDO produce, MesProFeedbackDO feedback,
                                                           Long batchId, String batchCode,
                                                           Long lineId, BigDecimal quantity) {
        return MesWmProductProduceDetailDO.builder()
                .produceId(produce.getId()).lineId(lineId)
                .itemId(feedback.getItemId()).quantity(quantity)
                .batchId(batchId).batchCode(batchCode)
                // TODO @AI（from codex）：对齐 产出明细需要补齐虚拟仓/库区/库位（VIRTUAL_WH/VIRTUAL_WS/VIRTUAL_WA），否则后续库存入账定位不完整。
                .build();
    }

    @Override
    public MesWmProductProduceDO validateProductProduceExists(Long id) {
        MesWmProductProduceDO produce = productProduceMapper.selectById(id);
        if (produce == null) {
            throw exception(WM_PRODUCT_PRODUCE_NOT_EXISTS);
        }
        return produce;
    }

    /**
     * 校验生产入库单存在且为准备中状态
     */
    private MesWmProductProduceDO validateProductProduceExistsAndPrepare(Long id) {
        MesWmProductProduceDO produce = validateProductProduceExists(id);
        if (ObjUtil.notEqual(produce.getStatus(), MesWmProductProduceStatusEnum.PREPARE.getStatus())) {
            throw exception(WM_PRODUCT_PRODUCE_STATUS_INVALID);
        }
        return produce;
    }


}
