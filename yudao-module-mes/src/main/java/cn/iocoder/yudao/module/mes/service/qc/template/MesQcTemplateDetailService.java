package cn.iocoder.yudao.module.mes.service.qc.template;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateIndicatorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateItemDO;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import jakarta.validation.Valid;

// TODO @AI：还是按照表，拆分。而不是 detail；
/**
 * MES 质检方案明细 Service 接口
 *
 * 专注于质检方案的检测指标项和产品关联的 CRUD，以及复杂查询逻辑
 *
 * @author 芋道源码
 */
public interface MesQcTemplateDetailService {

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

    // ========== 复杂查询 ==========

    /**
     * 根据方案 ID + 产品 ID 获取产品关联
     *
     * @param templateId 方案 ID
     * @param itemId 产品 ID
     * @return 产品关联
     */
    MesQcTemplateItemDO getTemplateItemByTemplateIdAndItemId(Long templateId, Long itemId);

    // TODO @芋艿：【不要删除】后续这个方法，会慢慢被替代掉；
    /**
     * 根据产品物料 ID 和 QC 类型查找检验模板
     *
     * 查找逻辑：在 template_item 中找到匹配 itemId 的模板，再校验该模板的 types 包含给定的 qcType
     *
     * @param itemId 产品物料 ID
     * @param qcType 检测种类（枚举 {@link MesQcTypeEnum}）
     * @return 匹配的检验模板（可能为 null）
     */
    MesQcTemplateDO getTemplateByItemIdAndType(Long itemId, Integer qcType);

    /**
     * 根据产品物料 ID 和 QC 类型查找检验模板的产品关联（必须存在，否则抛异常）
     *
     * @param itemId 产品物料 ID
     * @param qcType 检测种类（枚举 {@link MesQcTypeEnum}）
     * @return 匹配的产品关联
     */
    MesQcTemplateItemDO getRequiredTemplateByItemIdAndType(Long itemId, Integer qcType);

}
