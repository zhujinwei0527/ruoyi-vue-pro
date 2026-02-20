package cn.iocoder.yudao.module.mes.service.qc.lqc;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.defect.MesQcIqcDefectPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.defect.MesQcIqcDefectSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDefectDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.lqc.MesQcIqcDefectMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.lqc.MesQcIqcLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.lqc.MesQcIqcMapper;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcDefectLevelEnum;
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
 * MES 来料检验缺陷记录 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesQcIqcDefectServiceImpl implements MesQcIqcDefectService {

    @Resource
    private MesQcIqcDefectMapper iqcDefectMapper;
    @Resource
    private MesQcIqcLineMapper iqcLineMapper;
    @Resource
    private MesQcIqcMapper iqcMapper;
    @Resource
    @Lazy
    private MesQcIqcLineService iqcLineService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createIqcDefect(MesQcIqcDefectSaveReqVO createReqVO) {
        // 1.1 校验 IQC 存在
        validateIqcExists(createReqVO.getIqcId());
        // 1.2 校验行存在
        validateIqcLineExists(createReqVO.getLineId());

        // 2. 插入
        MesQcIqcDefectDO defect = BeanUtils.toBean(createReqVO, MesQcIqcDefectDO.class);
        iqcDefectMapper.insert(defect);

        // 3. 重新计算缺陷统计
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

    @Override
    public PageResult<MesQcIqcDefectDO> getIqcDefectPage(MesQcIqcDefectPageReqVO pageReqVO) {
        return iqcDefectMapper.selectPage(pageReqVO);
    }

    @Override
    public void deleteByIqcId(Long iqcId) {
        iqcDefectMapper.deleteByIqcId(iqcId);
    }

    private void validateIqcExists(Long iqcId) {
        if (iqcMapper.selectById(iqcId) == null) {
            throw exception(QC_IQC_NOT_EXISTS);
        }
    }

    private void validateIqcLineExists(Long lineId) {
        MesQcIqcLineDO line = iqcLineService.getIqcLine(lineId);
        if (line == null) {
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

    /**
     * 重新计算缺陷统计（行级 + 主表级）
     *
     * @param iqcId 来料检验单 ID
     */
    private void recalculateDefectStats(Long iqcId) {
        // 1. 查询所有缺陷记录
        List<MesQcIqcDefectDO> defects = iqcDefectMapper.selectListByIqcId(iqcId);

        // 2. 更新每行的缺陷数量
        List<MesQcIqcLineDO> lines = iqcLineService.selectListByIqcId(iqcId);
        for (MesQcIqcLineDO line : lines) {
            int critical = 0;
            int major = 0;
            int minor = 0;
            for (MesQcIqcDefectDO defect : defects) {
                // TODO @AI：notEquals continue；减少括号层级；
                if (Objects.equals(defect.getLineId(), line.getId())) {
                    // TODO @AI：defaultIfNull；变量改成 quantity
                    int qty = defect.getDefectQuantity() != null ? defect.getDefectQuantity() : 1;
                    if (Objects.equals(defect.getDefectLevel(), MesQcDefectLevelEnum.CRITICAL.getType())) {
                        critical += qty;
                    } else if (Objects.equals(defect.getDefectLevel(), MesQcDefectLevelEnum.MAJOR.getType())) {
                        major += qty;
                    } else if (Objects.equals(defect.getDefectLevel(), MesQcDefectLevelEnum.MINOR.getType())) {
                        minor += qty;
                    }
                    // TODO @AI：抛出异常；未知的缺陷等级
                }
            }
            MesQcIqcLineDO updateLine = new MesQcIqcLineDO().setId(line.getId())
                    .setCriticalQuantity(critical).setMajorQuantity(major).setMinorQuantity(minor);
            // 直接通过 lineService 内部更新（避免循环依赖，使用 mapper）
            // TODO @AI：单独搞个 updateLineDefectStats 方法；减少 updateLine 变量的作用域；
            iqcLineMapper.updateById(updateLine);
        }

        // 3.1 汇总主表的缺陷数量
        int totalCritical = 0, totalMajor = 0, totalMinor = 0;
        for (MesQcIqcDefectDO defect : defects) {
            // TODO @AI：defaultIfNull；变量改成 quantity
            int qty = defect.getDefectQuantity() != null ? defect.getDefectQuantity() : 1;
            if (Objects.equals(defect.getDefectLevel(), MesQcDefectLevelEnum.CRITICAL.getType())) {
                totalCritical += qty;
            } else if (Objects.equals(defect.getDefectLevel(), MesQcDefectLevelEnum.MAJOR.getType())) {
                totalMajor += qty;
            } else if (Objects.equals(defect.getDefectLevel(), MesQcDefectLevelEnum.MINOR.getType())) {
                totalMinor += qty;
            }
            // TODO @AI：抛出异常；未知的缺陷等级
        }
        // 3.2 计算缺陷率
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
        // 3.3 更新主表
        MesQcIqcDO updateIqc = new MesQcIqcDO().setId(iqcId)
                .setCriticalQuantity(totalCritical).setMajorQuantity(totalMajor).setMinorQuantity(totalMinor)
                .setCriticalRate(criticalRate).setMajorRate(majorRate).setMinorRate(minorRate);
        iqcMapper.updateById(updateIqc);
    }

}
