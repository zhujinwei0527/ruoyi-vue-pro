package cn.iocoder.yudao.module.mes.service.qc.lqc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.MesQcIqcPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.MesQcIqcSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.defectrecord.MesQcDefectRecordDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateItemDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.lqc.MesQcIqcMapper;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcDefectLevelEnum;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcIqcStatusEnum;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import cn.iocoder.yudao.module.mes.service.qc.defectrecord.MesQcDefectRecordService;
import cn.iocoder.yudao.module.mes.service.qc.template.MesQcTemplateService;
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

/**
 * MES 来料检验单（IQC） Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesQcIqcServiceImpl implements MesQcIqcService {

    @Resource
    private MesQcIqcMapper iqcMapper;
    @Resource
    private MesQcTemplateService templateService;

    @Resource
    private MesQcIqcLineService iqcLineService;
    @Resource
    private MesQcDefectRecordService defectRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createIqc(MesQcIqcSaveReqVO createReqVO) {
        // 1.1 校验编号唯一
        validateIqcCodeUnique(null, createReqVO.getCode());
        // 1.2 查找物料关联的 IQC 检测模板
        Long templateId = createReqVO.getTemplateId();
        MesQcTemplateDO template = templateService.validateTemplateExists(templateId);
        if (!CollUtil.contains(template.getTypes(), MesQcTypeEnum.IQC.getType())) {
            throw exception(QC_IQC_NO_TEMPLATE);
        }

        // 3.1 从模板的产品关联中获取检测参数（min_check_quantity、max_unqualified_quantity）
        MesQcTemplateItemDO templateItem = templateService.getTemplateItemByTemplateIdAndItemId(
                templateId, createReqVO.getItemId());
        if (templateItem != null) {
            if (createReqVO.getMinCheckQuantity() == null) {
                createReqVO.setMinCheckQuantity(templateItem.getQuantityCheck());
            }
            if (createReqVO.getMaxUnqualifiedQuantity() == null) {
                createReqVO.setMaxUnqualifiedQuantity(templateItem.getQuantityUnqualified());
            }
        }
        // 3.2 插入主表
        MesQcIqcDO iqc = BeanUtils.toBean(createReqVO, MesQcIqcDO.class);
        iqc.setStatus(MesQcIqcStatusEnum.PREPARE.getType());
        iqcMapper.insert(iqc);

        // 4. 从模板指标自动生成检验行
        iqcLineService.createLinesFromTemplate(iqc.getId(), templateId);
        return iqc.getId();
    }

    @Override
    public void updateIqc(MesQcIqcSaveReqVO updateReqVO) {
        // 1.1 校验存在 + 草稿状态
        validateIqcStatusPrepare(updateReqVO.getId());
        // 1.2 校验编号唯一
        validateIqcCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 2. 更新
        MesQcIqcDO updateObj = BeanUtils.toBean(updateReqVO, MesQcIqcDO.class);
        iqcMapper.updateById(updateObj);
    }

    @Override
    public void completeIqc(Long id) {
        // 1.1 校验存在 + 草稿状态
        MesQcIqcDO iqc = validateIqcStatusPrepare(id);
        // 1.2 校验合格品 + 不合格品 = 检测数量
        if (iqc.getCheckQuantity() != null && iqc.getCheckQuantity() > 0) {
            int total = (iqc.getQualifiedQuantity() != null ? iqc.getQualifiedQuantity() : 0)
                    + (iqc.getUnqualifiedQuantity() != null ? iqc.getUnqualifiedQuantity() : 0);
            if (total != iqc.getCheckQuantity()) {
                throw exception(QC_IQC_QUANTITY_MISMATCH);
            }
        }

        // 2. 更新状态为已完成
        MesQcIqcDO updateObj = new MesQcIqcDO()
                .setId(id).setStatus(MesQcIqcStatusEnum.FINISHED.getType());
        iqcMapper.updateById(updateObj);

        // TODO @芋艿：WM 模块迁移后，更新到货通知单行/外协入库单行的检验结果
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIqc(Long id) {
        // 1. 校验存在 + 草稿状态
        validateIqcStatusPrepare(id);

        // 2.1 删除主表
        iqcMapper.deleteById(id);
        // 2.2 级联删除行
        iqcLineService.deleteByIqcId(id);
        // 2.3 级联删除缺陷记录
        defectRecordService.deleteByQcTypeAndQcId(MesQcTypeEnum.IQC.getType(), id);
    }

    @Override
    public MesQcIqcDO validateIqcExists(Long id) {
        MesQcIqcDO iqc = iqcMapper.selectById(id);
        if (iqc == null) {
            throw exception(QC_IQC_NOT_EXISTS);
        }
        return iqc;
    }

    /**
     * 校验来料检验单存在且为草稿状态
     *
     * @param id 来料检验单 ID
     * @return 来料检验单
     */
    private MesQcIqcDO validateIqcStatusPrepare(Long id) {
        MesQcIqcDO iqc = validateIqcExists(id);
        if (ObjUtil.notEqual(iqc.getStatus(), MesQcIqcStatusEnum.PREPARE.getType())) {
            throw exception(QC_IQC_NOT_PREPARE);
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

    @Override
    public void recalculateDefectStats(Long iqcId, List<MesQcDefectRecordDO> records) {
        // 1. 行级缺陷统计
        iqcLineService.recalculateLineDefectStats(iqcId, records);

        // 2.1 汇总主表的缺陷数量
        int totalCritical = 0, totalMajor = 0, totalMinor = 0;
        for (MesQcDefectRecordDO record : records) {
            int quantity = ObjUtil.defaultIfNull(record.getQuantity(), 1);
            if (Objects.equals(record.getLevel(), MesQcDefectLevelEnum.CRITICAL.getType())) {
                totalCritical += quantity;
            } else if (Objects.equals(record.getLevel(), MesQcDefectLevelEnum.MAJOR.getType())) {
                totalMajor += quantity;
            } else if (Objects.equals(record.getLevel(), MesQcDefectLevelEnum.MINOR.getType())) {
                totalMinor += quantity;
            } else {
                throw exception(QC_DEFECT_RECORD_LEVEL_UNKNOWN);
            }
        }
        // 2.2 计算缺陷率
        MesQcIqcDO iqc = validateIqcExists(iqcId);
        BigDecimal criticalRate = BigDecimal.ZERO;
        BigDecimal majorRate = BigDecimal.ZERO;
        BigDecimal minorRate = BigDecimal.ZERO;
        if (iqc.getCheckQuantity() != null && iqc.getCheckQuantity() > 0) {
            BigDecimal checkQty = BigDecimal.valueOf(iqc.getCheckQuantity());
            criticalRate = BigDecimal.valueOf(totalCritical).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
            majorRate = BigDecimal.valueOf(totalMajor).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
            minorRate = BigDecimal.valueOf(totalMinor).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
        }

        // 3. 更新主表
        MesQcIqcDO updateIqc = new MesQcIqcDO().setId(iqcId)
                .setCriticalQuantity(totalCritical).setMajorQuantity(totalMajor).setMinorQuantity(totalMinor)
                .setCriticalRate(criticalRate).setMajorRate(majorRate).setMinorRate(minorRate);
        iqcMapper.updateById(updateIqc);
    }

}
