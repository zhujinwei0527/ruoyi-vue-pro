package cn.iocoder.yudao.module.mes.service.wm.productproduce;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.MesWmProductProducePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.MesWmProductProduceSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productproduce.MesWmProductProduceMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmProductProduceStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
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
    private MesWmProductProduceMapper produceMapper;

    @Resource
    private MesWmProductProduceLineService produceLineService;
    @Resource
    private MesWmProductProduceDetailService produceDetailService;
    @Resource
    private MesMdWorkstationService workstationService;
    @Resource
    private MesProWorkOrderService workOrderService;

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
        produceMapper.insert(produce);
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
        produceMapper.updateById(updateObj);
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
        produceMapper.deleteById(id);
    }

    @Override
    public MesWmProductProduceDO getProductProduce(Long id) {
        return produceMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmProductProduceDO> getProductProducePage(MesWmProductProducePageReqVO pageReqVO) {
        return produceMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishProductProduce(Long id) {
        // 1. 校验存在 + 草稿状态
        validateProductProduceExistsAndPrepare(id);

        // 2. 校验至少有一条行
        List<MesWmProductProduceLineDO> lines = produceLineService.getProductProduceLineListByProduceId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_PRODUCT_PRODUCE_NO_LINE);
        }

        // 3. 校验每行明细数量之和等于行数量
        for (MesWmProductProduceLineDO line : lines) {
            List<MesWmProductProduceDetailDO> details = produceDetailService.getProductProduceDetailListByLineId(line.getId());
            BigDecimal totalDetailQty = CollectionUtils.getSumValue(details,
                    MesWmProductProduceDetailDO::getQuantity, BigDecimal::add, BigDecimal.ZERO);
            if (line.getQuantity() != null && totalDetailQty.compareTo(line.getQuantity()) != 0) {
                throw exception(WM_PRODUCT_PRODUCE_DETAIL_QUANTITY_MISMATCH);
            }
        }

        // TODO @芋艿：完成入库时更新库存（对齐 ktg-mes 的 storageCoreService.processProductProduce），待库存事务模块就绪后实现

        // 4. 更新入库单状态
        produceMapper.updateById(new MesWmProductProduceDO()
                .setId(id).setStatus(MesWmProductProduceStatusEnum.FINISHED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelProductProduce(Long id) {
        // 校验存在
        MesWmProductProduceDO produce = validateProductProduceExists(id);
        // 已完成和已取消不允许取消
        if (ObjectUtils.equalsAny(produce.getStatus(),
                MesWmProductProduceStatusEnum.FINISHED.getStatus(),
                MesWmProductProduceStatusEnum.CANCELED.getStatus())) {
            throw exception(WM_PRODUCT_PRODUCE_CANCEL_NOT_ALLOWED);
        }

        // 取消
        produceMapper.updateById(new MesWmProductProduceDO()
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
    public MesWmProductProduceDO validateProductProduceExists(Long id) {
        MesWmProductProduceDO produce = produceMapper.selectById(id);
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
        if (ObjUtil.notEqual(MesWmProductProduceStatusEnum.PREPARE.getStatus(), produce.getStatus())) {
            throw exception(WM_PRODUCT_PRODUCE_STATUS_INVALID);
        }
        return produce;
    }

}
