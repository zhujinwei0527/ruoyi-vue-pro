package cn.iocoder.yudao.module.mes.service.qc.lqc;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.*;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.defect.MesQcIqcDefectPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.defect.MesQcIqcDefectSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.line.MesQcIqcLinePageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDefectDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcLineDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateIndicatorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateItemDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.lqc.MesQcIqcDefectMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.lqc.MesQcIqcLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.lqc.MesQcIqcMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.template.MesQcTemplateIndicatorMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.template.MesQcTemplateItemMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.template.MesQcTemplateMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

// TODO @AI：和别的模块一样，拆分成多个 service
/**
 * MES 来料检验单（IQC） Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesQcIqcServiceImpl implements MesQcIqcService {

    // TODO @AI：搞个枚举类；
    /**
     * IQC 检测种类值（对应 mes_qc_type 字典 value=1）
     */
    private static final int QC_TYPE_IQC = 1;

    @Resource
    private MesQcIqcMapper iqcMapper;
    @Resource
    private MesQcIqcLineMapper iqcLineMapper;
    @Resource
    private MesQcIqcDefectMapper iqcDefectMapper;
    @Resource
    private MesQcTemplateMapper templateMapper;
    @Resource
    private MesQcTemplateIndicatorMapper templateIndicatorMapper;
    @Resource
    private MesQcTemplateItemMapper templateItemMapper;

    // ========== 来料检验主表 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createIqc(MesQcIqcSaveReqVO createReqVO) {
        // 1. 校验编号唯一
        validateIqcCodeUnique(null, createReqVO.getCode());

        // 2. 查找物料关联的 IQC 检测模板
        Long templateId = createReqVO.getTemplateId();
        MesQcTemplateDO template = templateMapper.selectById(templateId);
        if (template == null || template.getTypes() == null || !template.getTypes().contains(QC_TYPE_IQC)) {
            throw exception(QC_IQC_NO_TEMPLATE);
        }

        // 3. 从模板的产品关联中获取检测参数（min_check_quantity、max_unqualified_quantity）
        MesQcTemplateItemDO templateItem = templateItemMapper.selectByTemplateIdAndItemId(
                templateId, createReqVO.getItemId());
        if (templateItem != null) {
            if (createReqVO.getMinCheckQuantity() == null) {
                createReqVO.setMinCheckQuantity(templateItem.getQuantityCheck());
            }
            if (createReqVO.getMaxUnqualifiedQuantity() == null) {
                createReqVO.setMaxUnqualifiedQuantity(templateItem.getQuantityUnqualified());
            }
        }

        // 4. 插入主表
        MesQcIqcDO iqc = BeanUtils.toBean(createReqVO, MesQcIqcDO.class);
        iqc.setStatus(0); // 草稿
        iqcMapper.insert(iqc);

        // 5. 从模板指标自动生成检验行
        List<MesQcTemplateIndicatorDO> indicators = templateIndicatorMapper.selectListByTemplateId(templateId);
        for (MesQcTemplateIndicatorDO indicator : indicators) {
            MesQcIqcLineDO line = new MesQcIqcLineDO();
            line.setIqcId(iqc.getId());
            line.setIndicatorId(indicator.getIndicatorId());
            line.setToolId(null); // TODO @芋艿：模板指标暂无 toolId，后续可扩展
            line.setCheckMethod(indicator.getCheckMethod());
            line.setStandardValue(indicator.getStandardValue());
            line.setUnitMeasureId(indicator.getUnitMeasureId());
            line.setMaxThreshold(indicator.getThresholdMax());
            line.setMinThreshold(indicator.getThresholdMin());
            line.setCriticalQuantity(0);
            line.setMajorQuantity(0);
            line.setMinorQuantity(0);
            iqcLineMapper.insert(line);
        }

        return iqc.getId();
    }

    @Override
    public void updateIqc(MesQcIqcSaveReqVO updateReqVO) {
        // 1. 校验存在
        MesQcIqcDO iqc = validateIqcExists(updateReqVO.getId());
        // 2. 校验状态为草稿
        if (!Objects.equals(iqc.getStatus(), 0)) {
            throw exception(QC_IQC_ONLY_PREPARE_CAN_COMPLETE);
        }
        // 3. 校验编号唯一
        validateIqcCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 4. 更新
        MesQcIqcDO updateObj = BeanUtils.toBean(updateReqVO, MesQcIqcDO.class);
        iqcMapper.updateById(updateObj);
    }

    @Override
    public void completeIqc(Long id) {
        // 1. 校验存在
        MesQcIqcDO iqc = validateIqcExists(id);
        // 2. 校验状态为草稿
        if (!Objects.equals(iqc.getStatus(), 0)) {
            throw exception(QC_IQC_ONLY_PREPARE_CAN_COMPLETE);
        }
        // 3. 校验合格品 + 不合格品 = 检测数量
        if (iqc.getCheckQuantity() != null && iqc.getCheckQuantity() > 0) {
            int total = (iqc.getQualifiedQuantity() != null ? iqc.getQualifiedQuantity() : 0)
                    + (iqc.getUnqualifiedQuantity() != null ? iqc.getUnqualifiedQuantity() : 0);
            if (total != iqc.getCheckQuantity()) {
                throw exception(QC_IQC_QUANTITY_MISMATCH);
            }
        }
        // 4. 更新状态为已完成
        MesQcIqcDO updateObj = new MesQcIqcDO();
        updateObj.setId(id);
        updateObj.setStatus(1);
        iqcMapper.updateById(updateObj);
        // TODO @芋艿：WM 模块迁移后，更新到货通知单行/外协入库单行的检验结果
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIqc(Long id) {
        // 1. 校验存在
        MesQcIqcDO iqc = validateIqcExists(id);
        // 2. 仅草稿可删
        if (!Objects.equals(iqc.getStatus(), 0)) {
            throw exception(QC_IQC_ONLY_PREPARE_CAN_DELETE);
        }
        // 3. 删除主表
        iqcMapper.deleteById(id);
        // 4. 级联删除行
        iqcLineMapper.deleteByIqcId(id);
        // 5. 级联删除缺陷记录
        iqcDefectMapper.deleteByIqcId(id);
    }

    private MesQcIqcDO validateIqcExists(Long id) {
        MesQcIqcDO iqc = iqcMapper.selectById(id);
        if (iqc == null) {
            throw exception(QC_IQC_NOT_EXISTS);
        }
        return iqc;
    }

    private void validateIqcCodeUnique(Long id, String code) {
        MesQcIqcDO iqc = iqcMapper.selectByCode(code);
        if (iqc == null) {
            return;
        }
        if (ObjUtil.notEqual(iqc.getId(), id)) {
            throw exception(QC_IQC_CODE_DUPLICATE);
        }
    }

    @Override
    public MesQcIqcDO getIqc(Long id) {
        return iqcMapper.selectById(id);
    }

    @Override
    public PageResult<MesQcIqcDO> getIqcPage(MesQcIqcPageReqVO pageReqVO) {
        return iqcMapper.selectPage(pageReqVO);
    }

    // ========== 来料检验行（只读） ==========

    @Override
    public MesQcIqcLineDO getIqcLine(Long id) {
        return iqcLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesQcIqcLineDO> getIqcLinePage(MesQcIqcLinePageReqVO pageReqVO) {
        return iqcLineMapper.selectPage(pageReqVO);
    }

    // ========== 来料检验缺陷记录 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createIqcDefect(MesQcIqcDefectSaveReqVO createReqVO) {
        // 1. 校验 IQC 存在
        validateIqcExists(createReqVO.getIqcId());
        // 2. 校验行存在
        validateIqcLineExists(createReqVO.getLineId());
        // 3. 插入
        MesQcIqcDefectDO defect = BeanUtils.toBean(createReqVO, MesQcIqcDefectDO.class);
        iqcDefectMapper.insert(defect);
        // 4. 重新计算缺陷统计
        recalculateDefectStats(createReqVO.getIqcId());
        return defect.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIqcDefect(MesQcIqcDefectSaveReqVO updateReqVO) {
        // 1. 校验存在
        validateIqcDefectExists(updateReqVO.getId());
        // 2. 更新
        MesQcIqcDefectDO updateObj = BeanUtils.toBean(updateReqVO, MesQcIqcDefectDO.class);
        iqcDefectMapper.updateById(updateObj);
        // 3. 重新计算缺陷统计
        recalculateDefectStats(updateReqVO.getIqcId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIqcDefect(Long id) {
        // 1. 校验存在
        MesQcIqcDefectDO defect = validateIqcDefectExists(id);
        // 2. 删除
        iqcDefectMapper.deleteById(id);
        // 3. 重新计算缺陷统计
        recalculateDefectStats(defect.getIqcId());
    }

    private void validateIqcLineExists(Long id) {
        if (iqcLineMapper.selectById(id) == null) {
            throw exception(QC_IQC_LINE_NOT_EXISTS);
        }
    }

    private MesQcIqcDefectDO validateIqcDefectExists(Long id) {
        MesQcIqcDefectDO defect = iqcDefectMapper.selectById(id);
        if (defect == null) {
            throw exception(QC_IQC_DEFECT_NOT_EXISTS);
        }
        return defect;
    }

    @Override
    public PageResult<MesQcIqcDefectDO> getIqcDefectPage(MesQcIqcDefectPageReqVO pageReqVO) {
        return iqcDefectMapper.selectPage(pageReqVO);
    }

    // ========== 内部方法 ==========

    /**
     * 重新计算缺陷统计（行级 + 主表级）
     *
     * @param iqcId 来料检验单 ID
     */
    private void recalculateDefectStats(Long iqcId) {
        // 1. 查询所有缺陷记录
        List<MesQcIqcDefectDO> defects = iqcDefectMapper.selectListByIqcId(iqcId);

        // 2. 更新每行的缺陷数量
        List<MesQcIqcLineDO> lines = iqcLineMapper.selectListByIqcId(iqcId);
        for (MesQcIqcLineDO line : lines) {
            int critical = 0, major = 0, minor = 0;
            for (MesQcIqcDefectDO defect : defects) {
                if (Objects.equals(defect.getLineId(), line.getId())) {
                    int qty = defect.getDefectQuantity() != null ? defect.getDefectQuantity() : 1;
                    switch (defect.getDefectLevel()) {
                        case "CRITICAL": critical += qty; break;
                        case "MAJOR": major += qty; break;
                        case "MINOR": minor += qty; break;
                    }
                }
            }
            MesQcIqcLineDO updateLine = new MesQcIqcLineDO();
            updateLine.setId(line.getId());
            updateLine.setCriticalQuantity(critical);
            updateLine.setMajorQuantity(major);
            updateLine.setMinorQuantity(minor);
            iqcLineMapper.updateById(updateLine);
        }

        // 3. 汇总主表的缺陷数量
        int totalCritical = 0, totalMajor = 0, totalMinor = 0;
        for (MesQcIqcDefectDO defect : defects) {
            int qty = defect.getDefectQuantity() != null ? defect.getDefectQuantity() : 1;
            switch (defect.getDefectLevel()) {
                case "CRITICAL": totalCritical += qty; break;
                case "MAJOR": totalMajor += qty; break;
                case "MINOR": totalMinor += qty; break;
            }
        }

        // 4. 计算缺陷率
        MesQcIqcDO iqc = iqcMapper.selectById(iqcId);
        BigDecimal criticalRate = BigDecimal.ZERO;
        BigDecimal majorRate = BigDecimal.ZERO;
        BigDecimal minorRate = BigDecimal.ZERO;
        if (iqc != null && iqc.getCheckQuantity() != null && iqc.getCheckQuantity() > 0) {
            BigDecimal checkQty = BigDecimal.valueOf(iqc.getCheckQuantity());
            criticalRate = BigDecimal.valueOf(totalCritical).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
            majorRate = BigDecimal.valueOf(totalMajor).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
            minorRate = BigDecimal.valueOf(totalMinor).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
        }

        // 5. 更新主表
        MesQcIqcDO updateIqc = new MesQcIqcDO();
        updateIqc.setId(iqcId);
        updateIqc.setCriticalQuantity(totalCritical);
        updateIqc.setMajorQuantity(totalMajor);
        updateIqc.setMinorQuantity(totalMinor);
        updateIqc.setCriticalRate(criticalRate);
        updateIqc.setMajorRate(majorRate);
        updateIqc.setMinorRate(minorRate);
        iqcMapper.updateById(updateIqc);
    }

}
