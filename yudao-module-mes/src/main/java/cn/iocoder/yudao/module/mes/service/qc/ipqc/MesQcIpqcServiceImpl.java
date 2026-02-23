package cn.iocoder.yudao.module.mes.service.qc.ipqc;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.ipqc.vo.MesQcIpqcPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.ipqc.vo.MesQcIpqcSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.defectrecord.MesQcDefectRecordDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.ipqc.MesQcIpqcDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.ipqc.MesQcIpqcMapper;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcDefectLevelEnum;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcIqcStatusEnum;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import cn.iocoder.yudao.module.mes.service.qc.defectrecord.MesQcDefectRecordService;
import cn.iocoder.yudao.module.mes.service.qc.template.MesQcTemplateDetailService;
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
 * MES 过程检验单（IPQC） Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesQcIpqcServiceImpl implements MesQcIpqcService {

    @Resource
    private MesQcIpqcMapper ipqcMapper;

    @Resource
    private MesQcTemplateDetailService templateDetailService;
    @Resource
    private MesQcIpqcLineService ipqcLineService;
    @Resource
    @Lazy
    private MesProWorkOrderService workOrderService;
    @Resource
    @Lazy
    private MesMdWorkstationService workstationService;
    @Resource
    @Lazy
    private MesQcDefectRecordService defectRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createIpqc(MesQcIpqcSaveReqVO createReqVO) {
        // 1.1 校验编号唯一
        validateIpqcCodeUnique(null, createReqVO.getCode());
        // 1.2 校验工单存在
        MesProWorkOrderDO workOrder = workOrderService.validateWorkOrderExists(createReqVO.getWorkOrderId());
        // 1.3 校验工位存在
        workstationService.validateWorkstationExists(createReqVO.getWorkstationId());
        // 1.4 根据产品 + 检验类型自动匹配模板
        MesQcTemplateDO template = templateDetailService.getTemplateByItemIdAndType(
                workOrder.getProductId(), MesQcTypeEnum.IPQC.getType());
        if (template == null) {
            throw exception(QC_IPQC_NO_TEMPLATE);
        }

        // 2. 插入主表
        MesQcIpqcDO ipqc = BeanUtils.toBean(createReqVO, MesQcIpqcDO.class);
        ipqc.setItemId(workOrder.getProductId());
        ipqc.setTemplateId(template.getId()).setStatus(MesQcIqcStatusEnum.PREPARE.getType());
        ipqcMapper.insert(ipqc);

        // 3. 从模板指标自动生成检验行
        ipqcLineService.createLinesFromTemplate(ipqc.getId(), template.getId());
        return ipqc.getId();
    }

    @Override
    public void updateIpqc(MesQcIpqcSaveReqVO updateReqVO) {
        // 1.1 校验存在 + 草稿状态
        validateIpqcStatusPrepare(updateReqVO.getId());
        // 1.2 校验编号唯一
        validateIpqcCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 2. 更新
        MesQcIpqcDO updateObj = BeanUtils.toBean(updateReqVO, MesQcIpqcDO.class);
        ipqcMapper.updateById(updateObj);
    }

    @Override
    public void completeIpqc(Long id) {
        // 1.1 校验存在 + 草稿状态
        MesQcIpqcDO ipqc = validateIpqcStatusPrepare(id);
        // 1.2 校验合格品 + 不合格品 = 检测数量
        if (ipqc.getCheckQuantity() != null && ipqc.getCheckQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal qualified = ipqc.getQualifiedQuantity() != null ? ipqc.getQualifiedQuantity() : BigDecimal.ZERO;
            BigDecimal unqualified = ipqc.getUnqualifiedQuantity() != null ? ipqc.getUnqualifiedQuantity() : BigDecimal.ZERO;
            if (qualified.add(unqualified).compareTo(ipqc.getCheckQuantity()) != 0) {
                throw exception(QC_IPQC_QUANTITY_MISMATCH);
            }
        }

        // 2. 更新状态为已完成
        MesQcIpqcDO updateObj = new MesQcIpqcDO();
        updateObj.setId(id);
        updateObj.setStatus(MesQcIqcStatusEnum.FINISHED.getType());
        ipqcMapper.updateById(updateObj);

        // TODO @芋艿：IPQC 完成时的 PRO/WM 联动（报工反馈更新、产品产出单拆分），待 WM 模块迁移后对接
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIpqc(Long id) {
        // 1. 校验存在 + 草稿状态
        validateIpqcStatusPrepare(id);

        // 2.1 删除主表
        ipqcMapper.deleteById(id);
        // 2.2 级联删除行
        ipqcLineService.deleteByIpqcId(id);
        // 2.3 级联删除缺陷记录
        defectRecordService.deleteListByQcTypeAndQcId(MesQcTypeEnum.IPQC.getType(), id);
    }

    @Override
    public MesQcIpqcDO validateIpqcExists(Long id) {
        MesQcIpqcDO ipqc = ipqcMapper.selectById(id);
        if (ipqc == null) {
            throw exception(QC_IPQC_NOT_EXISTS);
        }
        return ipqc;
    }

    /**
     * 校验过程检验单存在且为草稿状态
     *
     * @param id 过程检验单 ID
     * @return 过程检验单
     */
    private MesQcIpqcDO validateIpqcStatusPrepare(Long id) {
        MesQcIpqcDO ipqc = validateIpqcExists(id);
        if (ObjUtil.notEqual(ipqc.getStatus(), MesQcIqcStatusEnum.PREPARE.getType())) {
            throw exception(QC_IPQC_NOT_PREPARE);
        }
        return ipqc;
    }

    private void validateIpqcCodeUnique(Long id, String code) {
        MesQcIpqcDO ipqc = ipqcMapper.selectByCode(code);
        if (ipqc == null) {
            return;
        }
        if (ObjUtil.notEqual(ipqc.getId(), id)) {
            throw exception(QC_IPQC_CODE_DUPLICATE);
        }
    }

    @Override
    public MesQcIpqcDO getIpqc(Long id) {
        return ipqcMapper.selectById(id);
    }

    @Override
    public PageResult<MesQcIpqcDO> getIpqcPage(MesQcIpqcPageReqVO pageReqVO) {
        return ipqcMapper.selectPage(pageReqVO);
    }

    @Override
    public void recalculateDefectStats(Long ipqcId, List<MesQcDefectRecordDO> records) {
        // 1. 行级缺陷统计
        ipqcLineService.recalculateLineDefectStats(ipqcId, records);

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
        // 2.2 计算缺陷率（IPQC 的 checkQuantity 是 BigDecimal）
        MesQcIpqcDO ipqc = validateIpqcExists(ipqcId);
        BigDecimal criticalRate = BigDecimal.ZERO;
        BigDecimal majorRate = BigDecimal.ZERO;
        BigDecimal minorRate = BigDecimal.ZERO;
        if (ipqc.getCheckQuantity() != null && ipqc.getCheckQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal checkQty = ipqc.getCheckQuantity();
            criticalRate = BigDecimal.valueOf(totalCritical).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
            majorRate = BigDecimal.valueOf(totalMajor).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
            minorRate = BigDecimal.valueOf(totalMinor).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
        }

        // 3. 更新主表
        MesQcIpqcDO updateIpqc = new MesQcIpqcDO().setId(ipqcId)
                .setCriticalQuantity(totalCritical).setMajorQuantity(totalMajor).setMinorQuantity(totalMinor)
                .setCriticalRate(criticalRate).setMajorRate(majorRate).setMinorRate(minorRate);
        ipqcMapper.updateById(updateIpqc);
    }

}
