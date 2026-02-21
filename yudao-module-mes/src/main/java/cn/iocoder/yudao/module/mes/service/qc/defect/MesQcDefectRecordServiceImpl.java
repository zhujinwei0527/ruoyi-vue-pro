package cn.iocoder.yudao.module.mes.service.qc.defect;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.defect.vo.MesQcDefectRecordPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.defect.vo.MesQcDefectRecordSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.defect.MesQcDefectRecordDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.defect.MesQcDefectRecordMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.lqc.MesQcIqcMapper;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcDefectLevelEnum;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import cn.iocoder.yudao.module.mes.service.qc.lqc.MesQcIqcLineService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 质检缺陷记录 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesQcDefectRecordServiceImpl implements MesQcDefectRecordService {

    @Resource
    private MesQcDefectRecordMapper defectRecordMapper;

    // TODO @AI：不要调用 mapper，调用对方的 service；
    @Resource
    private MesQcIqcMapper iqcMapper;
    @Resource
    @Lazy
    private MesQcIqcLineService iqcLineService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDefectRecord(MesQcDefectRecordSaveReqVO createReqVO) {
        // 1. 校验主表和行表存在
        validateQcExists(createReqVO.getQcType(), createReqVO.getQcId());
        validateQcLineExists(createReqVO.getQcType(), createReqVO.getLineId());

        // 2. 插入
        MesQcDefectRecordDO record = BeanUtils.toBean(createReqVO, MesQcDefectRecordDO.class);
        defectRecordMapper.insert(record);

        // 3. 重新计算缺陷统计
        recalculateDefectStats(createReqVO.getQcType(), createReqVO.getQcId());
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDefectRecord(MesQcDefectRecordSaveReqVO updateReqVO) {
        // 1. 校验存在
        validateDefectRecordExists(updateReqVO.getId());

        // 2. 更新
        MesQcDefectRecordDO updateObj = BeanUtils.toBean(updateReqVO, MesQcDefectRecordDO.class);
        defectRecordMapper.updateById(updateObj);

        // 3. 重新计算缺陷统计
        recalculateDefectStats(updateReqVO.getQcType(), updateReqVO.getQcId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDefectRecord(Long id) {
        // 1. 校验存在
        MesQcDefectRecordDO record = validateDefectRecordExists(id);

        // 2. 删除
        defectRecordMapper.deleteById(id);

        // 3. 重新计算缺陷统计
        recalculateDefectStats(record.getQcType(), record.getQcId());
    }

    @Override
    public PageResult<MesQcDefectRecordDO> getDefectRecordPage(MesQcDefectRecordPageReqVO pageReqVO) {
        return defectRecordMapper.selectPage(pageReqVO);
    }

    @Override
    public void deleteByQcTypeAndQcId(Integer qcType, Long qcId) {
        defectRecordMapper.deleteByQcTypeAndQcId(qcType, qcId);
    }

    // ==================== 校验方法 ====================

    // TODO @AI：validateQcExists、validateQcLineExists 合并成一个方法；然后调用对应的 service 方法；
    private void validateQcExists(Integer qcType, Long qcId) {
        if (Objects.equals(qcType, MesQcTypeEnum.IQC.getType())) {
            if (iqcMapper.selectById(qcId) == null) {
                throw exception(QC_IQC_NOT_EXISTS);
            }
        } else {
            // 后续扩展：IPQC、OQC、RQC
            throw exception(QC_DEFECT_RECORD_QC_TYPE_UNSUPPORTED);
        }
    }

    // TODO @AI：去掉这个，相信调用方
    private void validateQcLineExists(Integer qcType, Long lineId) {
        if (Objects.equals(qcType, MesQcTypeEnum.IQC.getType())) {
            MesQcIqcLineDO line = iqcLineService.getIqcLine(lineId);
            if (line == null) {
                throw exception(QC_IQC_LINE_NOT_EXISTS);
            }
        } else {
            // 后续扩展：IPQC、OQC、RQC
            throw exception(QC_DEFECT_RECORD_QC_TYPE_UNSUPPORTED);
        }
    }

    private MesQcDefectRecordDO validateDefectRecordExists(Long id) {
        MesQcDefectRecordDO record = defectRecordMapper.selectById(id);
        if (record == null) {
            throw exception(QC_DEFECT_RECORD_NOT_EXISTS);
        }
        return record;
    }

    // ==================== 缺陷统计 ====================

    /**
     * 重新计算缺陷统计（行级 + 主表级）
     *
     * @param qcType 检验类型
     * @param qcId   检验单 ID
     */
    @SuppressWarnings("DuplicatedCode")
    private void recalculateDefectStats(Integer qcType, Long qcId) {
        // 1. 查询所有缺陷记录
        List<MesQcDefectRecordDO> records = defectRecordMapper.selectListByQcTypeAndQcId(qcType, qcId);

        if (Objects.equals(qcType, MesQcTypeEnum.IQC.getType())) {
            // TODO @AI：直接调用 iqcservice 方法；qcId、records
            recalculateIqcDefectStats(qcId, records);
        } else {
            // 后续扩展：IPQC、OQC、RQC
            throw exception(QC_DEFECT_RECORD_QC_TYPE_UNSUPPORTED);
        }
    }

    /**
     * IQC 缺陷统计重算
     */
    private void recalculateIqcDefectStats(Long qcId, List<MesQcDefectRecordDO> records) {
        // 1. 按行汇总缺陷数量，批量更新
        // TODO @AI：1 这块的逻辑，放到 iqcLineService 里，使用 qcId、records；
        List<MesQcIqcLineDO> lines = iqcLineService.selectListByIqcId(qcId);
        List<MesQcIqcLineDO> updateLines = new ArrayList<>();
        for (MesQcIqcLineDO line : lines) {
            int critical = 0, major = 0, minor = 0;
            for (MesQcDefectRecordDO record : records) {
                if (ObjUtil.notEqual(record.getLineId(), line.getId())) {
                    continue;
                }
                int quantity = ObjUtil.defaultIfNull(record.getQuantity(), 1);
                if (Objects.equals(record.getLevel(), MesQcDefectLevelEnum.CRITICAL.getType())) {
                    critical += quantity;
                } else if (Objects.equals(record.getLevel(), MesQcDefectLevelEnum.MAJOR.getType())) {
                    major += quantity;
                } else if (Objects.equals(record.getLevel(), MesQcDefectLevelEnum.MINOR.getType())) {
                    minor += quantity;
                } else {
                    throw exception(QC_DEFECT_RECORD_LEVEL_UNKNOWN);
                }
            }
            updateLines.add(new MesQcIqcLineDO().setId(line.getId())
                    .setCriticalQuantity(critical).setMajorQuantity(major).setMinorQuantity(minor));
        }
        iqcLineService.batchUpdateDefectStats(updateLines);

        // 2. 汇总主表的缺陷数量
        // TODO @AI：2-4 这块的逻辑，放到 iqcService 里，使用 qcId、records；
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

        // 3. 计算缺陷率
        MesQcIqcDO iqc = iqcMapper.selectById(qcId);
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

        // 4. 更新主表
        MesQcIqcDO updateIqc = new MesQcIqcDO().setId(qcId)
                .setCriticalQuantity(totalCritical).setMajorQuantity(totalMajor).setMinorQuantity(totalMinor)
                .setCriticalRate(criticalRate).setMajorRate(majorRate).setMinorRate(minorRate);
        iqcMapper.updateById(updateIqc);
    }

}
