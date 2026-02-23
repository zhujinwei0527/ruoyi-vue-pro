package cn.iocoder.yudao.module.mes.service.qc.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateIndicatorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateItemDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.indicator.MesQcIndicatorMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.template.MesQcTemplateIndicatorMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.template.MesQcTemplateItemMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.template.MesQcTemplateMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.findFirst;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 质检方案明细 Service 实现类
 *
 * 专注于质检方案的检测指标项和产品关联的 CRUD，以及复杂查询逻辑
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesQcTemplateDetailServiceImpl implements MesQcTemplateDetailService {

    @Resource
    private MesQcTemplateMapper templateMapper;
    @Resource
    private MesQcTemplateIndicatorMapper templateIndicatorMapper;
    @Resource
    private MesQcTemplateItemMapper templateItemMapper;
    @Resource
    private MesQcIndicatorMapper indicatorMapper;

    @Resource
    @Lazy
    private MesQcTemplateService templateService;

    // ========== 质检方案-检测指标项 ==========

    @Override
    public Long createTemplateIndicator(MesQcTemplateIndicatorSaveReqVO createReqVO) {
        // 校验方案存在
        templateService.validateTemplateExists(createReqVO.getTemplateId());
        // 插入
        MesQcTemplateIndicatorDO indicator = BeanUtils.toBean(createReqVO, MesQcTemplateIndicatorDO.class);
        templateIndicatorMapper.insert(indicator);
        return indicator.getId();
    }

    @Override
    public void updateTemplateIndicator(MesQcTemplateIndicatorSaveReqVO updateReqVO) {
        // 校验存在
        validateTemplateIndicatorExists(updateReqVO.getId());
        // 更新
        MesQcTemplateIndicatorDO updateObj = BeanUtils.toBean(updateReqVO, MesQcTemplateIndicatorDO.class);
        templateIndicatorMapper.updateById(updateObj);
    }

    @Override
    public void deleteTemplateIndicator(Long id) {
        // 校验存在
        validateTemplateIndicatorExists(id);
        // 删除
        templateIndicatorMapper.deleteById(id);
    }

    private void validateTemplateIndicatorExists(Long id) {
        if (templateIndicatorMapper.selectById(id) == null) {
            throw exception(QC_TEMPLATE_INDICATOR_NOT_EXISTS);
        }
    }

    @Override
    public MesQcTemplateIndicatorDO getTemplateIndicator(Long id) {
        return templateIndicatorMapper.selectById(id);
    }

    @Override
    public PageResult<MesQcTemplateIndicatorDO> getTemplateIndicatorPage(MesQcTemplateIndicatorPageReqVO pageReqVO) {
        return templateIndicatorMapper.selectPage(pageReqVO);
    }

    // ========== 质检方案-产品关联 ==========

    @Override
    public Long createTemplateItem(MesQcTemplateItemSaveReqVO createReqVO) {
        // 校验方案存在
        templateService.validateTemplateExists(createReqVO.getTemplateId());
        // 校验产品在此方案中唯一
        validateTemplateItemNotDuplicate(null, createReqVO.getTemplateId(), createReqVO.getItemId());
        // 插入
        MesQcTemplateItemDO item = BeanUtils.toBean(createReqVO, MesQcTemplateItemDO.class);
        templateItemMapper.insert(item);
        return item.getId();
    }

    @Override
    public void updateTemplateItem(MesQcTemplateItemSaveReqVO updateReqVO) {
        // 校验存在
        validateTemplateItemExists(updateReqVO.getId());
        // 校验产品在此方案中唯一
        validateTemplateItemNotDuplicate(updateReqVO.getId(), updateReqVO.getTemplateId(), updateReqVO.getItemId());
        // 更新
        MesQcTemplateItemDO updateObj = BeanUtils.toBean(updateReqVO, MesQcTemplateItemDO.class);
        templateItemMapper.updateById(updateObj);
    }

    @Override
    public void deleteTemplateItem(Long id) {
        // 校验存在
        validateTemplateItemExists(id);
        // 删除
        templateItemMapper.deleteById(id);
    }

    private void validateTemplateItemExists(Long id) {
        if (templateItemMapper.selectById(id) == null) {
            throw exception(QC_TEMPLATE_ITEM_NOT_EXISTS);
        }
    }

    private void validateTemplateItemNotDuplicate(Long id, Long templateId, Long itemId) {
        MesQcTemplateItemDO existing = templateItemMapper.selectByTemplateIdAndItemId(templateId, itemId);
        if (existing == null) {
            return;
        }
        if (ObjUtil.notEqual(existing.getId(), id)) {
            throw exception(QC_TEMPLATE_ITEM_DUPLICATE);
        }
    }

    @Override
    public MesQcTemplateItemDO getTemplateItem(Long id) {
        return templateItemMapper.selectById(id);
    }

    @Override
    public PageResult<MesQcTemplateItemDO> getTemplateItemPage(MesQcTemplateItemPageReqVO pageReqVO) {
        return templateItemMapper.selectPage(pageReqVO);
    }

    // ========== 复杂查询 ==========

    @Override
    public MesQcTemplateItemDO getTemplateItemByTemplateIdAndItemId(Long templateId, Long itemId) {
        return templateItemMapper.selectByTemplateIdAndItemId(templateId, itemId);
    }

    @Override
    public MesQcTemplateDO getTemplateByItemIdAndType(Long itemId, Integer qcType) {
        // 1.1 查出 itemId 关联的所有 templateId
        List<MesQcTemplateItemDO> templateItems = templateItemMapper.selectListByItemId(itemId);
        if (CollUtil.isEmpty(templateItems)) {
            return null;
        }
        // 1.2 批量查模板，筛选 types 包含给定 qcType 的第一个
        List<MesQcTemplateDO> templates = templateMapper.selectByIds(
                convertSet(templateItems, MesQcTemplateItemDO::getTemplateId));
        // 2. 筛选 types 包含给定 qcType 的第一个
        return findFirst(templates, template -> CollUtil.contains(template.getTypes(), qcType));
    }

    @Override
    public MesQcTemplateItemDO getRequiredTemplateByItemIdAndType(Long itemId, Integer qcType) {
        // 1. 查出 itemId 关联的所有 templateId
        List<MesQcTemplateItemDO> templateItems = templateItemMapper.selectListByItemId(itemId);
        if (CollUtil.isEmpty(templateItems)) {
            throw exception(QC_IQC_NO_TEMPLATE);
        }
        // 2. 筛选 types 包含 qcType 的模板
        List<MesQcTemplateDO> templates = templateMapper.selectByIds(
                convertSet(templateItems, MesQcTemplateItemDO::getTemplateId));
        MesQcTemplateDO matchedTemplate = findFirst(templates,
                t -> CollUtil.contains(t.getTypes(), qcType));
        if (matchedTemplate == null) {
            throw exception(QC_IQC_NO_TEMPLATE);
        }
        // 3. 返回对应的 templateItem
        return findFirst(templateItems,
                item -> Objects.equals(item.getTemplateId(), matchedTemplate.getId()));
    }

}
