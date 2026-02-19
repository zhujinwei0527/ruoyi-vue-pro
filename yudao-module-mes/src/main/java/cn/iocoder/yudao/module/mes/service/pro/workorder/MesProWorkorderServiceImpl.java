package cn.iocoder.yudao.module.mes.service.pro.workorder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.MesProWorkOrderPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.MesProWorkOrderSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemBatchConfigDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdProductBomDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderBomDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.workorder.MesProWorkOrderBomMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.workorder.MesProWorkOrderMapper;
import cn.iocoder.yudao.module.mes.enums.pro.MesProWorkOrderStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemBatchConfigService;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdProductBomService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 生产工单 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesProWorkOrderServiceImpl implements MesProWorkOrderService {

    @Resource
    private MesProWorkOrderMapper workOrderMapper;

    @Resource
    private MesProWorkOrderBomMapper workOrderBomMapper;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesMdProductBomService productBomService;

    @Resource
    private MesMdItemBatchConfigService itemBatchConfigService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWorkOrder(MesProWorkOrderSaveReqVO createReqVO) {
        // 1.1 校验编码唯一
        validateWorkOrderCodeUnique(null, createReqVO.getCode());
        // 1.2 校验产品存在
        itemService.validateItemExists(createReqVO.getProductId());
        // 1.3 校验批次配置：如果产品有 clientFlag=true，则 clientId 必填
        validateBatchConfig(createReqVO.getProductId(), createReqVO.getClientId());

        // 2.1 设置默认值
        if (createReqVO.getParentId() == null) {
            createReqVO.setParentId(0L);
        }
        // 2.2 插入工单
        MesProWorkOrderDO workOrder = BeanUtils.toBean(createReqVO, MesProWorkOrderDO.class);
        if (workOrder.getStatus() == null) {
            workOrder.setStatus(MesProWorkOrderStatusEnum.PREPARE.getStatus());
        }
        workOrderMapper.insert(workOrder);

        // 3. 自动生成 BOM：根据产品 BOM 生成工单 BOM
        generateWorkOrderBom(workOrder.getId(), createReqVO.getProductId(), createReqVO.getQuantity());
        return workOrder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWorkOrder(MesProWorkOrderSaveReqVO updateReqVO) {
        // 1.1 校验存在
        MesProWorkOrderDO oldWorkOrder = validateWorkOrderExists(updateReqVO.getId());
        // 1.2 校验编码唯一
        validateWorkOrderCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 1.3 校验批次配置
        validateBatchConfig(updateReqVO.getProductId(), updateReqVO.getClientId());

        // 2. 判断产品或数量是否变更，如果变更则重新生成 BOM
        boolean productChanged = ObjUtil.notEqual(oldWorkOrder.getProductId(), updateReqVO.getProductId());
        boolean quantityChanged = oldWorkOrder.getQuantity().compareTo(updateReqVO.getQuantity()) != 0;
        if (productChanged || quantityChanged) {
            // 删除旧 BOM 并重新生成
            workOrderBomMapper.deleteByWorkOrderId(updateReqVO.getId());
            generateWorkOrderBom(updateReqVO.getId(), updateReqVO.getProductId(), updateReqVO.getQuantity());
        }

        // 3. 更新
        MesProWorkOrderDO updateObj = BeanUtils.toBean(updateReqVO, MesProWorkOrderDO.class);
        workOrderMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkOrder(Long id) {
        // 1.1 校验存在
        MesProWorkOrderDO workOrder = validateWorkOrderExists(id);
        // 1.2 只能删除草稿状态的工单
        if (ObjUtil.notEqual(workOrder.getStatus(), MesProWorkOrderStatusEnum.PREPARE.getStatus())) {
            throw exception(PRO_WORK_ORDER_NOT_PREPARE);
        }

        // 2. 删除工单 + BOM
        workOrderMapper.deleteById(id);
        workOrderBomMapper.deleteByWorkOrderId(id);
    }

    @Override
    public MesProWorkOrderDO validateWorkOrderExists(Long id) {
        MesProWorkOrderDO workOrder = workOrderMapper.selectById(id);
        if (workOrder == null) {
            throw exception(PRO_WORK_ORDER_NOT_EXISTS);
        }
        return workOrder;
    }

    @Override
    public MesProWorkOrderDO getWorkOrder(Long id) {
        return workOrderMapper.selectById(id);
    }

    @Override
    public PageResult<MesProWorkOrderDO> getWorkOrderPage(MesProWorkOrderPageReqVO pageReqVO) {
        return workOrderMapper.selectPage(pageReqVO);
    }

    @Override
    public void finishWorkOrder(Long id) {
        // 1. 校验存在
        validateWorkOrderExists(id);

        // 2. 更新状态为已完成
        workOrderMapper.updateById(new MesProWorkOrderDO().setId(id)
                .setStatus(MesProWorkOrderStatusEnum.FINISHED.getStatus())
                .setFinishDate(LocalDateTime.now()));
        // TODO @芋艿：pro_task 未迁移，暂不级联更新任务状态
    }

    @Override
    public void cancelWorkOrder(Long id) {
        // 1. 校验存在
        validateWorkOrderExists(id);

        // 2. 更新状态为已取消
        workOrderMapper.updateById(new MesProWorkOrderDO().setId(id)
                .setStatus(MesProWorkOrderStatusEnum.CANCELED.getStatus())
                .setCancelDate(LocalDateTime.now()));
        // TODO @芋艿：pro_task 未迁移，暂不级联更新任务状态
    }

    @Override
    public List<MesProWorkOrderDO> getWorkOrderList(Collection<Long> ids) {
        return workOrderMapper.selectByIds(ids);
    }

    // ==================== 校验方法 ====================

    private void validateWorkOrderCodeUnique(Long id, String code) {
        if (code == null) {
            return;
        }
        MesProWorkOrderDO workOrder = workOrderMapper.selectByCode(code);
        if (workOrder == null) {
            return;
        }
        if (ObjUtil.notEqual(workOrder.getId(), id)) {
            throw exception(PRO_WORK_ORDER_CODE_DUPLICATE);
        }
    }

    private void validateBatchConfig(Long productId, Long clientId) {
        MesMdItemBatchConfigDO batchConfig = itemBatchConfigService.getItemBatchConfigByItemId(productId);
        if (batchConfig != null && Boolean.TRUE.equals(batchConfig.getClientFlag()) && clientId == null) {
            // 产品要求批次管理中必须填写客户
            throw exception(MD_CLIENT_NOT_EXISTS);
        }
    }

    // ==================== BOM 自动生成 ====================

    /**
     * 根据产品 BOM 自动生成工单 BOM 行
     *
     * @param workOrderId 工单编号
     * @param productId   产品编号
     * @param quantity    工单生产数量
     */
    private void generateWorkOrderBom(Long workOrderId, Long productId, BigDecimal quantity) {
        List<MesMdProductBomDO> productBomList = productBomService.getProductBomListByItemId(productId);
        if (CollUtil.isEmpty(productBomList)) {
            return;
        }
        for (MesMdProductBomDO productBom : productBomList) {
            MesMdItemDO bomItem = itemService.validateItemExists(productBom.getBomItemId());
            workOrderBomMapper.insert(new MesProWorkOrderBomDO()
                    .setWorkOrderId(workOrderId)
                    .setItemId(productBom.getBomItemId())
                    .setQuantity(quantity.multiply(productBom.getQuantity()))
                    .setUnitMeasureId(bomItem.getUnitMeasureId()));
        }
    }

}
