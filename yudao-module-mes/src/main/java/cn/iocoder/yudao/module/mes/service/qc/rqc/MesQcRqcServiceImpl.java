package cn.iocoder.yudao.module.mes.service.qc.rqc;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.rqc.vo.MesQcRqcPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.rqc.vo.MesQcRqcSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.defectrecord.MesQcDefectRecordDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.rqc.MesQcRqcDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateItemDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.rqc.MesQcRqcMapper;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusEnum;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcDefectLevelEnum;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.qc.defectrecord.MesQcDefectRecordService;
import cn.iocoder.yudao.module.mes.service.qc.template.MesQcTemplateDetailService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
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
 * MES 退货检验单（RQC） Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesQcRqcServiceImpl implements MesQcRqcService {

    @Resource
    private MesQcRqcMapper rqcMapper;
    @Resource
    private MesQcTemplateDetailService templateDetailService;
    @Resource
    private MesQcRqcLineService rqcLineService;
    @Resource
    @Lazy
    private MesMdItemService itemService;
    @Resource
    @Lazy
    private MesQcDefectRecordService defectRecordService;

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRqc(MesQcRqcSaveReqVO createReqVO) {
        // 1.1 校验编号唯一
        validateRqcCodeUnique(null, createReqVO.getCode());
        // 1.2 校验物料、检测人员存在
        itemService.validateItemExists(createReqVO.getItemId());
        adminUserApi.validateUser(createReqVO.getInspectorUserId());
        // 1.3 根据物料 + 检验类型自动匹配模板
        MesQcTemplateItemDO templateItem = templateDetailService.getRequiredTemplateByItemIdAndType(
                createReqVO.getItemId(), createReqVO.getType());
        Long templateId = templateItem.getTemplateId();

        // 2. 插入主表
        MesQcRqcDO rqc = BeanUtils.toBean(createReqVO, MesQcRqcDO.class)
                .setTemplateId(templateId)
                .setStatus(MesOrderStatusEnum.DRAFT.getType());
        rqcMapper.insert(rqc);

        // 3. 从模板指标自动生成检验行
        rqcLineService.createLinesFromTemplate(rqc.getId(), templateId);
        return rqc.getId();
    }

    @Override
    public void updateRqc(MesQcRqcSaveReqVO updateReqVO) {
        // 1.1 校验存在 + 草稿状态
        validateRqcStatusPrepare(updateReqVO.getId());
        // 1.2 校验编号唯一
        validateRqcCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 1.3 校验物料、检测人员存在
        itemService.validateItemExists(updateReqVO.getItemId());
        adminUserApi.validateUser(updateReqVO.getInspectorUserId());

        // 2. 更新
        MesQcRqcDO updateObj = BeanUtils.toBean(updateReqVO, MesQcRqcDO.class);
        rqcMapper.updateById(updateObj);
    }

    @Override
    public void completeRqc(Long id) {
        // 1.1 校验存在 + 草稿状态
        MesQcRqcDO rqc = validateRqcStatusPrepare(id);
        // 1.2 校验合格品 + 不合格品 = 检测数量
        if (rqc.getCheckQuantity() != null && rqc.getCheckQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal qualified = rqc.getQualifiedQuantity() != null ? rqc.getQualifiedQuantity() : BigDecimal.ZERO;
            BigDecimal unqualified = rqc.getUnqualifiedQuantity() != null ? rqc.getUnqualifiedQuantity() : BigDecimal.ZERO;
            if (qualified.add(unqualified).compareTo(rqc.getCheckQuantity()) != 0) {
                throw exception(QC_RQC_QUANTITY_MISMATCH);
            }
        }

        // 2. 更新状态为已完成
        MesQcRqcDO updateObj = new MesQcRqcDO().setId(id).setStatus(MesOrderStatusEnum.FINISHED.getType());
        rqcMapper.updateById(updateObj);

        // TODO @芋艿：WM 模块迁移后，更新来源退料/退货单行的质量状态
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRqc(Long id) {
        // 1. 校验存在 + 草稿状态
        validateRqcStatusPrepare(id);

        // 2.1 删除主表
        rqcMapper.deleteById(id);
        // 2.2 级联删除行
        rqcLineService.deleteByRqcId(id);
        // 2.3 级联删除缺陷记录
        defectRecordService.deleteListByQcTypeAndQcId(MesQcTypeEnum.RQC.getType(), id);
    }

    @Override
    public MesQcRqcDO validateRqcExists(Long id) {
        MesQcRqcDO rqc = rqcMapper.selectById(id);
        if (rqc == null) {
            throw exception(QC_RQC_NOT_EXISTS);
        }
        return rqc;
    }

    /**
     * 校验退货检验单存在且为草稿状态
     *
     * @param id 退货检验单 ID
     * @return 退货检验单
     */
    private MesQcRqcDO validateRqcStatusPrepare(Long id) {
        MesQcRqcDO rqc = validateRqcExists(id);
        if (ObjUtil.notEqual(rqc.getStatus(), MesOrderStatusEnum.DRAFT.getType())) {
            throw exception(QC_RQC_NOT_PREPARE);
        }
        return rqc;
    }

    private void validateRqcCodeUnique(Long id, String code) {
        MesQcRqcDO rqc = rqcMapper.selectByCode(code);
        if (rqc == null) {
            return;
        }
        if (ObjUtil.notEqual(rqc.getId(), id)) {
            throw exception(QC_RQC_CODE_DUPLICATE);
        }
    }

    @Override
    public MesQcRqcDO getRqc(Long id) {
        return rqcMapper.selectById(id);
    }

    @Override
    public PageResult<MesQcRqcDO> getRqcPage(MesQcRqcPageReqVO pageReqVO) {
        return rqcMapper.selectPage(pageReqVO);
    }

    @Override
    public void recalculateDefectStats(Long rqcId, List<MesQcDefectRecordDO> records) {
        MesQcRqcDO rqc = validateRqcExists(rqcId);
        // 1. 行级缺陷统计
        rqcLineService.recalculateLineDefectStats(rqcId, records);

        // 2.1 汇总主表的缺陷数量
        int totalCritical = 0;
        int totalMajor = 0;
        int totalMinor = 0;
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
        BigDecimal criticalRate = BigDecimal.ZERO;
        BigDecimal majorRate = BigDecimal.ZERO;
        BigDecimal minorRate = BigDecimal.ZERO;
        if (rqc.getCheckQuantity() != null && rqc.getCheckQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal checkQty = rqc.getCheckQuantity();
            criticalRate = BigDecimal.valueOf(totalCritical).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
            majorRate = BigDecimal.valueOf(totalMajor).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
            minorRate = BigDecimal.valueOf(totalMinor).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
        }
        // 2.3 更新主表
        MesQcRqcDO updateRqc = new MesQcRqcDO().setId(rqcId)
                .setCriticalQuantity(totalCritical).setMajorQuantity(totalMajor).setMinorQuantity(totalMinor)
                .setCriticalRate(criticalRate).setMajorRate(majorRate).setMinorRate(minorRate);
        rqcMapper.updateById(updateRqc);
    }

}
