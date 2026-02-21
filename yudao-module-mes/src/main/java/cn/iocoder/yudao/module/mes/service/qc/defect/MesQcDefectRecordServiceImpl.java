package cn.iocoder.yudao.module.mes.service.qc.defect;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.defect.vo.MesQcDefectRecordPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.defect.vo.MesQcDefectRecordSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.defect.MesQcDefectRecordDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.defect.MesQcDefectRecordMapper;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import cn.iocoder.yudao.module.mes.service.qc.lqc.MesQcIqcLineService;
import cn.iocoder.yudao.module.mes.service.qc.lqc.MesQcIqcService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

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

    @Resource
    @Lazy
    private MesQcIqcService iqcService;
    @Resource
    @Lazy
    private MesQcIqcLineService iqcLineService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDefectRecord(MesQcDefectRecordSaveReqVO createReqVO) {
        // 1. 校验检验单和检验行存在
        validateQcAndLineExists(createReqVO.getQcType(), createReqVO.getQcId(), createReqVO.getLineId());

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

    private void validateQcAndLineExists(Integer qcType, Long qcId, Long lineId) {
        if (Objects.equals(qcType, MesQcTypeEnum.IQC.getType())) {
            iqcService.validateIqcExists(qcId);
            iqcLineService.validateIqcLineExists(lineId);
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
     * 重新计算缺陷统计（行级 + 主表级），委托给对应的检验 Service
     *
     * @param qcType 检验类型
     * @param qcId   检验单 ID
     */
    private void recalculateDefectStats(Integer qcType, Long qcId) {
        List<MesQcDefectRecordDO> records = defectRecordMapper.selectListByQcTypeAndQcId(qcType, qcId);
        if (Objects.equals(qcType, MesQcTypeEnum.IQC.getType())) {
            iqcService.recalculateDefectStats(qcId, records);
        } else {
            // 后续扩展：IPQC、OQC、RQC
            throw exception(QC_DEFECT_RECORD_QC_TYPE_UNSUPPORTED);
        }
    }

}
