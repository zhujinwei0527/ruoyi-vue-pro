package cn.iocoder.yudao.module.mes.service.pro.workorder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.MesProWorkorderSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.MesProWorkorderPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemBatchConfigDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdProductBomDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkorderBomDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkorderDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.workorder.MesProWorkorderBomMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.workorder.MesProWorkorderMapper;
import cn.iocoder.yudao.module.mes.enums.pro.MesProWorkorderStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemBatchConfigService;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdProductBomService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 生产工单 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesProWorkorderServiceImpl implements MesProWorkorderService {

    @Resource
    private MesProWorkorderMapper workorderMapper;

    @Resource
    private MesProWorkorderBomMapper workorderBomMapper;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesMdProductBomService productBomService;

    @Resource
    private MesMdItemBatchConfigService itemBatchConfigService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWorkorder(MesProWorkorderSaveReqVO createReqVO) {
        // 1.1 校验编码唯一
        validateWorkorderCodeUnique(null, createReqVO.getCode());
        // 1.2 校验产品存在
        // TODO @AI：getItem 里面要有个校验方法，不然每次都要写 if (item == null) throw exception(MD_ITEM_NOT_EXISTS);
        MesMdItemDO product = itemService.getItem(createReqVO.getProductId());
        if (product == null) {
            throw exception(MD_ITEM_NOT_EXISTS);
        }
        // 1.3 校验批次配置：如果产品有 clientFlag=true，则 clientId 必填
        validateBatchConfig(createReqVO.getProductId(), createReqVO.getClientId());

        // 2.1 设置默认值
        if (createReqVO.getParentId() == null) {
            createReqVO.setParentId(0L);
        }
        if (createReqVO.getAncestors() == null) {
            createReqVO.setAncestors("0");
        }
        // 2.2 插入工单
        MesProWorkorderDO workorder = BeanUtils.toBean(createReqVO, MesProWorkorderDO.class);
        if (workorder.getStatus() == null) {
            workorder.setStatus(MesProWorkorderStatusEnum.PREPARE.getStatus());
        }
        workorderMapper.insert(workorder);

        // 3. 自动生成 BOM：根据产品 BOM 生成工单 BOM
        generateWorkorderBom(workorder.getId(), createReqVO.getProductId(), createReqVO.getQuantity());
        return workorder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWorkorder(MesProWorkorderSaveReqVO updateReqVO) {
        // 1.1 校验存在
        MesProWorkorderDO oldWorkorder = validateWorkorderExists(updateReqVO.getId());
        // 1.2 校验编码唯一
        validateWorkorderCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 1.3 校验批次配置
        validateBatchConfig(updateReqVO.getProductId(), updateReqVO.getClientId());

        // 2. 判断产品或数量是否变更，如果变更则重新生成 BOM
        boolean productChanged = !Objects.equals(oldWorkorder.getProductId(), updateReqVO.getProductId());
        boolean quantityChanged = oldWorkorder.getQuantity().compareTo(updateReqVO.getQuantity()) != 0;
        if (productChanged || quantityChanged) {
            // 删除旧 BOM 并重新生成
            workorderBomMapper.deleteByWorkorderId(updateReqVO.getId());
            generateWorkorderBom(updateReqVO.getId(), updateReqVO.getProductId(), updateReqVO.getQuantity());
        }

        // 3. 更新
        MesProWorkorderDO updateObj = BeanUtils.toBean(updateReqVO, MesProWorkorderDO.class);
        workorderMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkorder(Long id) {
        // 1.1 校验存在
        MesProWorkorderDO workorder = validateWorkorderExists(id);
        // 1.2 只能删除草稿状态的工单
        // TODO @AI：notEquals；ObjUtil
        if (!Objects.equals(workorder.getStatus(), MesProWorkorderStatusEnum.PREPARE.getStatus())) {
            throw exception(PRO_WORKORDER_NOT_PREPARE);
        }

        // 2. 删除工单 + BOM
        workorderMapper.deleteById(id);
        workorderBomMapper.deleteByWorkorderId(id);
    }

    @Override
    public MesProWorkorderDO getWorkorder(Long id) {
        return workorderMapper.selectById(id);
    }

    @Override
    public PageResult<MesProWorkorderDO> getWorkorderPage(MesProWorkorderPageReqVO pageReqVO) {
        return workorderMapper.selectPage(pageReqVO);
    }

    @Override
    public void finishWorkorder(Long id) {
        // 1. 校验存在
        validateWorkorderExists(id);

        // 2. 更新状态为已完成
        // TODO @AI：链式调用，不用逐个设置；
        MesProWorkorderDO updateObj = new MesProWorkorderDO();
        updateObj.setId(id);
        updateObj.setStatus(MesProWorkorderStatusEnum.FINISHED.getStatus());
        updateObj.setFinishDate(LocalDateTime.now());
        workorderMapper.updateById(updateObj);
        // TODO @芋艿：pro_task 未迁移，暂不级联更新任务状态
    }

    @Override
    public void cancelWorkorder(Long id) {
        // 1. 校验存在
        validateWorkorderExists(id);

        // 2. 更新状态为已取消
        // TODO @AI：链式调用，不用逐个设置；
        MesProWorkorderDO updateObj = new MesProWorkorderDO();
        updateObj.setId(id);
        updateObj.setStatus(MesProWorkorderStatusEnum.CANCELED.getStatus());
        updateObj.setCancelDate(LocalDateTime.now());
        workorderMapper.updateById(updateObj);
        // TODO @芋艿：pro_task 未迁移，暂不级联更新任务状态
    }

    // ==================== 校验方法 ====================

    private MesProWorkorderDO validateWorkorderExists(Long id) {
        MesProWorkorderDO workorder = workorderMapper.selectById(id);
        if (workorder == null) {
            throw exception(PRO_WORKORDER_NOT_EXISTS);
        }
        return workorder;
    }

    private void validateWorkorderCodeUnique(Long id, String code) {
        if (code == null) {
            return;
        }
        MesProWorkorderDO workorder = workorderMapper.selectByCode(code);
        if (workorder == null) {
            return;
        }
        if (ObjUtil.notEqual(workorder.getId(), id)) {
            throw exception(PRO_WORKORDER_CODE_DUPLICATE);
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
     * @param workorderId 工单编号
     * @param productId   产品编号
     * @param quantity    工单生产数量
     */
    private void generateWorkorderBom(Long workorderId, Long productId, BigDecimal quantity) {
        List<MesMdProductBomDO> productBomList = productBomService.getProductBomListByItemId(productId);
        if (CollUtil.isEmpty(productBomList)) {
            return;
        }
        for (MesMdProductBomDO productBom : productBomList) {
            MesMdItemDO bomItem = itemService.getItem(productBom.getBomItemId());
            // TODO @AI：bomItem 校验必须非空吧？！
            // TODO @AI：联会调用；
            MesProWorkorderBomDO workorderBom = new MesProWorkorderBomDO();
            workorderBom.setWorkorderId(workorderId);
            workorderBom.setItemId(productBom.getBomItemId());
            workorderBom.setItemOrProduct("ITEM");
            // 预计使用量 = 工单数量 × BOM 用量
            workorderBom.setQuantity(quantity.multiply(productBom.getQuantity()));
            // 设置单位：使用物料自身的单位
            if (bomItem != null) {
                workorderBom.setUnitMeasureId(bomItem.getUnitMeasureId());
            }
            workorderBomMapper.insert(workorderBom);
        }
    }

}
