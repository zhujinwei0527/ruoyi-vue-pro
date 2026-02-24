package cn.iocoder.yudao.module.mes.service.qc.oqc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.oqc.vo.MesQcOqcPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.oqc.vo.MesQcOqcSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.defectrecord.MesQcDefectRecordDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.oqc.MesQcOqcDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateItemDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.oqc.MesQcOqcMapper;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcDefectLevelEnum;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcOqcStatusEnum;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import cn.iocoder.yudao.module.mes.service.qc.defectrecord.MesQcDefectRecordService;
import cn.iocoder.yudao.module.mes.service.qc.template.MesQcTemplateDetailService;
import cn.iocoder.yudao.module.mes.service.qc.template.MesQcTemplateService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
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
 * MES 出货检验单（OQC） Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesQcOqcServiceImpl implements MesQcOqcService {

    @Resource
    private MesQcOqcMapper oqcMapper;
    @Resource
    private MesQcTemplateService templateService;
    @Resource
    private MesQcTemplateDetailService templateDetailService;
    @Resource
    private MesQcOqcLineService oqcLineService;
    @Resource
    @Lazy
    private MesQcDefectRecordService defectRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOqc(MesQcOqcSaveReqVO createReqVO) {
        // 1.1 校验编号唯一
        validateOqcCodeUnique(null, createReqVO.getCode());
        // TODO @AI：关联字段的校验！
        // 1.2 校验模板存在且包含 OQC 类型
        // TODO @AI：不用前端传递，而是类似 iqc 后端计算
        Long templateId = createReqVO.getTemplateId();
        MesQcTemplateDO template = templateService.validateTemplateExists(templateId);
        if (!CollUtil.contains(template.getTypes(), MesQcTypeEnum.OQC.getType())) {
            throw exception(QC_OQC_NO_TEMPLATE);
        }

        // 1.3 从模板的产品关联中获取检测参数
        // TODO @芋艿：【暂时不用删除】到底 miniCheckQuantity 和 maxUnqualifiedQuantity 是否有必要存储？
        // TODO @AI：去掉这 2 个字段的设置，包括数据的存储，也去掉！
        MesQcTemplateItemDO templateItem = templateDetailService.getTemplateItemByTemplateIdAndItemId(
                templateId, createReqVO.getItemId());
        if (templateItem != null) {
            if (createReqVO.getMinCheckQuantity() == null) {
                createReqVO.setMinCheckQuantity(templateItem.getQuantityCheck());
            }
            if (createReqVO.getMaxUnqualifiedQuantity() == null) {
                createReqVO.setMaxUnqualifiedQuantity(templateItem.getQuantityUnqualified());
            }
        }

        // 2. 插入主表
        MesQcOqcDO oqc = BeanUtils.toBean(createReqVO, MesQcOqcDO.class);
        oqc.setStatus(MesQcOqcStatusEnum.PREPARE.getType());
        oqcMapper.insert(oqc);

        // 3. 从模板指标自动生成检验行
        oqcLineService.createLinesFromTemplate(oqc.getId(), templateId);
        return oqc.getId();
    }

    @Override
    public void updateOqc(MesQcOqcSaveReqVO updateReqVO) {
        // 1.1 校验存在 + 草稿状态
        validateOqcStatusPrepare(updateReqVO.getId());
        // 1.2 校验编号唯一
        validateOqcCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 2. 更新
        MesQcOqcDO updateObj = BeanUtils.toBean(updateReqVO, MesQcOqcDO.class);
        oqcMapper.updateById(updateObj);
    }

    @Override
    public void completeOqc(Long id) {
        // 1.1 校验存在 + 草稿状态
        MesQcOqcDO oqc = validateOqcStatusPrepare(id);
        // 1.2 校验合格品 + 不合格品 = 检测数量
        if (oqc.getCheckQuantity() != null && oqc.getCheckQuantity() > 0) {
            int total = (oqc.getQualifiedQuantity() != null ? oqc.getQualifiedQuantity() : 0)
                    + (oqc.getUnqualifiedQuantity() != null ? oqc.getUnqualifiedQuantity() : 0);
            if (total != oqc.getCheckQuantity()) {
                throw exception(QC_OQC_QUANTITY_MISMATCH);
            }
        }

        // 2. 更新状态为已完成
        MesQcOqcDO updateObj = new MesQcOqcDO()
                .setId(id).setStatus(MesQcOqcStatusEnum.FINISHED.getType());
        oqcMapper.updateById(updateObj);

        // TODO @芋艿：WM 模块迁移后，更新销售出库单行的检验结果
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOqc(Long id) {
        // 1. 校验存在 + 草稿状态
        validateOqcStatusPrepare(id);

        // 2.1 删除主表
        oqcMapper.deleteById(id);
        // 2.2 级联删除行
        oqcLineService.deleteByOqcId(id);
        // 2.3 级联删除缺陷记录
        defectRecordService.deleteListByQcTypeAndQcId(MesQcTypeEnum.OQC.getType(), id);
    }

    @Override
    public MesQcOqcDO validateOqcExists(Long id) {
        MesQcOqcDO oqc = oqcMapper.selectById(id);
        if (oqc == null) {
            throw exception(QC_OQC_NOT_EXISTS);
        }
        return oqc;
    }

    /**
     * 校验出货检验单存在且为草稿状态
     */
    private MesQcOqcDO validateOqcStatusPrepare(Long id) {
        MesQcOqcDO oqc = validateOqcExists(id);
        if (ObjUtil.notEqual(oqc.getStatus(), MesQcOqcStatusEnum.PREPARE.getType())) {
            throw exception(QC_OQC_NOT_PREPARE);
        }
        return oqc;
    }

    private void validateOqcCodeUnique(Long id, String code) {
        MesQcOqcDO oqc = oqcMapper.selectByCode(code);
        if (oqc == null) {
            return;
        }
        if (ObjUtil.notEqual(oqc.getId(), id)) {
            throw exception(QC_OQC_CODE_DUPLICATE);
        }
    }

    @Override
    public MesQcOqcDO getOqc(Long id) {
        return oqcMapper.selectById(id);
    }

    @Override
    public PageResult<MesQcOqcDO> getOqcPage(MesQcOqcPageReqVO pageReqVO) {
        return oqcMapper.selectPage(pageReqVO);
    }

    @Override
    public void recalculateDefectStats(Long oqcId, List<MesQcDefectRecordDO> records) {
        // 1. 行级缺陷统计
        oqcLineService.recalculateLineDefectStats(oqcId, records);

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
        MesQcOqcDO oqc = validateOqcExists(oqcId);
        BigDecimal criticalRate = BigDecimal.ZERO;
        BigDecimal majorRate = BigDecimal.ZERO;
        BigDecimal minorRate = BigDecimal.ZERO;
        if (oqc.getCheckQuantity() != null && oqc.getCheckQuantity() > 0) {
            BigDecimal checkQty = BigDecimal.valueOf(oqc.getCheckQuantity());
            criticalRate = BigDecimal.valueOf(totalCritical).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
            majorRate = BigDecimal.valueOf(totalMajor).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
            minorRate = BigDecimal.valueOf(totalMinor).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
        }

        // 3. 更新主表
        MesQcOqcDO updateOqc = new MesQcOqcDO().setId(oqcId)
                .setCriticalQuantity(totalCritical).setMajorQuantity(totalMajor).setMinorQuantity(totalMinor)
                .setCriticalRate(criticalRate).setMajorRate(majorRate).setMinorRate(minorRate);
        oqcMapper.updateById(updateOqc);
    }

}
