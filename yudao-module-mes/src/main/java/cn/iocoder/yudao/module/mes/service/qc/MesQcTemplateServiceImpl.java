package cn.iocoder.yudao.module.mes.service.qc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.MesQcTemplatePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.MesQcTemplateRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.MesQcTemplateSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.MesQcIndicatorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.MesQcTemplateDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.MesQcTemplateIndicatorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.MesQcTemplateItemDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.item.MesMdItemMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.md.unitmeasure.MesMdUnitMeasureMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.MesQcIndicatorMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.MesQcTemplateIndicatorMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.MesQcTemplateItemMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.MesQcTemplateMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    @Resource
    private MesMdItemMapper mdItemMapper;
    @Resource
    private MesMdUnitMeasureMapper unitMeasureMapper;

    // ========== 质检方案主表 ==========

    @Override
    public Long createTemplate(MesQcTemplateSaveReqVO createReqVO) {
        // 校验编码唯一
        validateTemplateCodeUnique(null, createReqVO.getCode());

        // 插入
        MesQcTemplateDO template = BeanUtils.toBean(createReqVO, MesQcTemplateDO.class);
        template.setTypes(convertTypesToString(createReqVO.getTypes()));
        template.setEnableFlag(Boolean.TRUE.equals(createReqVO.getEnableFlag()) ? "Y" : "N");
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
        updateObj.setTypes(convertTypesToString(updateReqVO.getTypes()));
        updateObj.setEnableFlag(Boolean.TRUE.equals(updateReqVO.getEnableFlag()) ? "Y" : "N");
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

    private void validateTemplateExists(Long id) {
        if (templateMapper.selectById(id) == null) {
            throw exception(QC_TEMPLATE_NOT_EXISTS);
        }
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
    public MesQcTemplateRespVO getTemplate(Long id) {
        MesQcTemplateDO do0 = templateMapper.selectById(id);
        if (do0 == null) {
            return null;
        }
        return convertTemplateRespVO(do0);
    }

    @Override
    public PageResult<MesQcTemplateRespVO> getTemplatePage(MesQcTemplatePageReqVO pageReqVO) {
        PageResult<MesQcTemplateDO> pageResult = templateMapper.selectPage(pageReqVO);
        return new PageResult<>(
                pageResult.getList().stream().map(this::convertTemplateRespVO).collect(Collectors.toList()),
                pageResult.getTotal()
        );
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

    /**
     * DO → RespVO：转换 types（String → List<Integer>）和 enableFlag（Y/N → Boolean）
     */
    private MesQcTemplateRespVO convertTemplateRespVO(MesQcTemplateDO do0) {
        MesQcTemplateRespVO vo = BeanUtils.toBean(do0, MesQcTemplateRespVO.class);
        vo.setTypes(convertTypesToList(do0.getTypes()));
        vo.setEnableFlag("Y".equals(do0.getEnableFlag()));
        return vo;
    }

    /**
     * List<Integer> → 逗号分隔字符串（如 [1,3] → "1,3"）
     */
    private String convertTypesToString(List<Integer> types) {
        if (CollUtil.isEmpty(types)) {
            return null;
        }
        return types.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * 逗号分隔字符串 → List<Integer>（如 "1,3" → [1,3]）
     */
    private List<Integer> convertTypesToList(String types) {
        if (StrUtil.isBlank(types)) {
            return Collections.emptyList();
        }
        return Arrays.stream(types.split(","))
                .map(String::trim)
                .filter(StrUtil::isNotBlank)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
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
    public MesQcTemplateIndicatorRespVO getTemplateIndicator(Long id) {
        MesQcTemplateIndicatorDO do0 = templateIndicatorMapper.selectById(id);
        if (do0 == null) {
            return null;
        }
        return convertIndicatorRespVO(do0);
    }

    @Override
    public PageResult<MesQcTemplateIndicatorRespVO> getTemplateIndicatorPage(MesQcTemplateIndicatorPageReqVO pageReqVO) {
        PageResult<MesQcTemplateIndicatorDO> pageResult = templateIndicatorMapper.selectPage(pageReqVO);
        return new PageResult<>(
                pageResult.getList().stream().map(this::convertIndicatorRespVO).collect(Collectors.toList()),
                pageResult.getTotal()
        );
    }

    /**
     * DO → RespVO：JOIN mes_qc_indicator 补充检测项信息
     */
    private MesQcTemplateIndicatorRespVO convertIndicatorRespVO(MesQcTemplateIndicatorDO do0) {
        MesQcTemplateIndicatorRespVO vo = BeanUtils.toBean(do0, MesQcTemplateIndicatorRespVO.class);
        if (do0.getIndicatorId() != null) {
            MesQcIndicatorDO indicator = indicatorMapper.selectById(do0.getIndicatorId());
            if (indicator != null) {
                vo.setIndicatorCode(indicator.getCode());
                vo.setIndicatorName(indicator.getName());
                vo.setIndicatorType(indicator.getType());
                vo.setIndicatorTool(indicator.getTool());
            }
        }
        return vo;
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
    public MesQcTemplateItemRespVO getTemplateItem(Long id) {
        MesQcTemplateItemDO do0 = templateItemMapper.selectById(id);
        if (do0 == null) {
            return null;
        }
        return convertItemRespVO(do0);
    }

    @Override
    public PageResult<MesQcTemplateItemRespVO> getTemplateItemPage(MesQcTemplateItemPageReqVO pageReqVO) {
        PageResult<MesQcTemplateItemDO> pageResult = templateItemMapper.selectPage(pageReqVO);
        return new PageResult<>(
                pageResult.getList().stream().map(this::convertItemRespVO).collect(Collectors.toList()),
                pageResult.getTotal()
        );
    }

    /**
     * DO → RespVO：JOIN mes_md_item 补充物料信息，JOIN mes_md_unit_measure 补充单位名称
     */
    private MesQcTemplateItemRespVO convertItemRespVO(MesQcTemplateItemDO do0) {
        MesQcTemplateItemRespVO vo = BeanUtils.toBean(do0, MesQcTemplateItemRespVO.class);
        if (do0.getItemId() != null) {
            MesMdItemDO item = mdItemMapper.selectById(do0.getItemId());
            if (item != null) {
                vo.setItemCode(item.getCode());
                vo.setItemName(item.getName());
                vo.setSpecification(item.getSpecification());
                // 查询计量单位名称
                if (item.getUnitMeasureId() != null) {
                    MesMdUnitMeasureDO unit = unitMeasureMapper.selectById(item.getUnitMeasureId());
                    if (unit != null) {
                        vo.setUnitMeasureName(unit.getName());
                    }
                }
            }
        }
        return vo;
    }

}
