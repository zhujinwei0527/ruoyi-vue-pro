package cn.iocoder.yudao.module.mes.service.qc.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.MesQcTemplatePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.MesQcTemplateSaveReqVO;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 质检方案 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesQcTemplateServiceImpl implements MesQcTemplateService {

    @Resource
    private MesQcTemplateMapper templateMapper;
    @Resource
    private MesQcTemplateIndicatorMapper templateIndicatorMapper;
    @Resource
    private MesQcTemplateItemMapper templateItemMapper;
    @Resource
    private MesQcIndicatorMapper indicatorMapper;

    // ========== 质检方案主表 ==========

    @Override
    public Long createTemplate(MesQcTemplateSaveReqVO createReqVO) {
        // 校验编码唯一
        validateTemplateCodeUnique(null, createReqVO.getCode());

        // 插入
        MesQcTemplateDO template = BeanUtils.toBean(createReqVO, MesQcTemplateDO.class);
        templateMapper.insert(template);
        return template.getId();
    }

    @Override
    public void updateTemplate(MesQcTemplateSaveReqVO updateReqVO) {
        // 校验存在
        validateTemplateExists(updateReqVO.getId());
        // 校验编码唯一
        validateTemplateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 更新
        MesQcTemplateDO updateObj = BeanUtils.toBean(updateReqVO, MesQcTemplateDO.class);
        templateMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long id) {
        // 校验存在
        validateTemplateExists(id);
        // 删除主表
        templateMapper.deleteById(id);
        // 级联删除检测指标项
        templateIndicatorMapper.deleteByTemplateId(id);
        // 级联删除产品关联
        templateItemMapper.deleteByTemplateId(id);
    }

    @Override
    public MesQcTemplateDO validateTemplateExists(Long id) {
        MesQcTemplateDO template = templateMapper.selectById(id);
        if (template == null) {
            throw exception(QC_TEMPLATE_NOT_EXISTS);
        }
        return template;
    }

    private void validateTemplateCodeUnique(Long id, String code) {
        MesQcTemplateDO template = templateMapper.selectByCode(code);
        if (template == null) {
            return;
        }
        if (ObjUtil.notEqual(template.getId(), id)) {
            throw exception(QC_TEMPLATE_CODE_DUPLICATE);
        }
    }

    @Override
    public MesQcTemplateItemDO getTemplateItemByTemplateIdAndItemId(Long templateId, Long itemId) {
        return templateItemMapper.selectByTemplateIdAndItemId(templateId, itemId);
    }

    @Override
    public MesQcTemplateDO getTemplate(Long id) {
        return templateMapper.selectById(id);
    }

    @Override
    public PageResult<MesQcTemplateDO> getTemplatePage(MesQcTemplatePageReqVO pageReqVO) {
        return templateMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesQcTemplateDO> getTemplateList() {
        return templateMapper.selectList();
    }

    @Override
    public List<MesQcTemplateDO> getTemplateList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return templateMapper.selectByIds(ids);
    }

    // ========== 质检方案-检测指标项 ==========

    @Override
    public Long createTemplateIndicator(MesQcTemplateIndicatorSaveReqVO createReqVO) {
        // 校验方案存在
        validateTemplateExists(createReqVO.getTemplateId());
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
        validateTemplateExists(createReqVO.getTemplateId());
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

}
