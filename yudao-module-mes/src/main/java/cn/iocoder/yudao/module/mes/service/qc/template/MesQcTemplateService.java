package cn.iocoder.yudao.module.mes.service.qc.template;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.MesQcTemplatePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.MesQcTemplateSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateIndicatorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateItemDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * MES 质检方案 Service 接口
 *
 * @author 芋道源码
 */
public interface MesQcTemplateService {

    // ========== 质检方案主表 ==========

    /**
     * 创建质检方案
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTemplate(@Valid MesQcTemplateSaveReqVO createReqVO);

    /**
     * 更新质检方案
     *
     * @param updateReqVO 更新信息
     */
    void updateTemplate(@Valid MesQcTemplateSaveReqVO updateReqVO);

    /**
     * 删除质检方案（级联删除检测指标项和产品关联）
     *
     * @param id 编号
     */
    void deleteTemplate(Long id);

    /**
     * 获得质检方案
     *
     * @param id 编号
     * @return 质检方案
     */
    MesQcTemplateDO getTemplate(Long id);

    /**
     * 获得质检方案分页
     *
     * @param pageReqVO 分页查询
     * @return 质检方案分页
     */
    PageResult<MesQcTemplateDO> getTemplatePage(MesQcTemplatePageReqVO pageReqVO);

    /**
     * 获得质检方案列表（用于下拉选择）
     *
     * @return 质检方案列表
     */
    List<MesQcTemplateDO> getTemplateList();

    /**
     * 获得质检方案列表
     *
     * @param ids 编号数组
     * @return 质检方案列表
     */
    List<MesQcTemplateDO> getTemplateList(Collection<Long> ids);

    /**
     * 获得质检方案 Map
     *
     * @param ids 编号数组
     * @return 质检方案 Map
     */
    default Map<Long, MesQcTemplateDO> getTemplateMap(Collection<Long> ids) {
        return convertMap(getTemplateList(ids), MesQcTemplateDO::getId);
    }

    // ========== 质检方案-检测指标项 ==========

    /**
     * 创建检测指标项
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTemplateIndicator(@Valid MesQcTemplateIndicatorSaveReqVO createReqVO);

    /**
     * 更新检测指标项
     *
     * @param updateReqVO 更新信息
     */
    void updateTemplateIndicator(@Valid MesQcTemplateIndicatorSaveReqVO updateReqVO);

    /**
     * 删除检测指标项
     *
     * @param id 编号
     */
    void deleteTemplateIndicator(Long id);

    /**
     * 获得检测指标项
     *
     * @param id 编号
     * @return 检测指标项
     */
    MesQcTemplateIndicatorDO getTemplateIndicator(Long id);

    /**
     * 获得检测指标项分页
     *
     * @param pageReqVO 分页查询
     * @return 检测指标项分页
     */
    PageResult<MesQcTemplateIndicatorDO> getTemplateIndicatorPage(MesQcTemplateIndicatorPageReqVO pageReqVO);

    // ========== 质检方案-产品关联 ==========

    /**
     * 创建产品关联
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTemplateItem(@Valid MesQcTemplateItemSaveReqVO createReqVO);

    /**
     * 更新产品关联
     *
     * @param updateReqVO 更新信息
     */
    void updateTemplateItem(@Valid MesQcTemplateItemSaveReqVO updateReqVO);

    /**
     * 删除产品关联
     *
     * @param id 编号
     */
    void deleteTemplateItem(Long id);

    /**
     * 获得产品关联
     *
     * @param id 编号
     * @return 产品关联
     */
    MesQcTemplateItemDO getTemplateItem(Long id);

    /**
     * 获得产品关联分页
     *
     * @param pageReqVO 分页查询
     * @return 产品关联分页
     */
    PageResult<MesQcTemplateItemDO> getTemplateItemPage(MesQcTemplateItemPageReqVO pageReqVO);

}
