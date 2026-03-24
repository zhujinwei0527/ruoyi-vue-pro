package cn.iocoder.yudao.module.mes.service.qc.ipqc;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.ipqc.vo.MesQcIpqcPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.ipqc.vo.MesQcIpqcSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.defectrecord.MesQcDefectRecordDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.ipqc.MesQcIpqcDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateItemDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.ipqc.MesQcIpqcMapper;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcDefectLevelEnum;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcStatusEnum;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import cn.iocoder.yudao.module.mes.enums.MesBizTypeConstants;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.feedback.MesProFeedbackService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
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
    private MesMdItemService itemService;
    @Resource
    @Lazy
    private MesQcDefectRecordService defectRecordService;

    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    @Lazy
    private MesProFeedbackService feedbackService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createIpqc(MesQcIpqcSaveReqVO createReqVO) {
        // 1.1 校验编号唯一
        validateIpqcCodeUnique(null, createReqVO.getCode());
        // 1.2 校验工单、工位、检测人员、物料存在
        MesProWorkOrderDO workOrder = workOrderService.validateWorkOrderExists(createReqVO.getWorkOrderId());
        workstationService.validateWorkstationExists(createReqVO.getWorkstationId());
        adminUserApi.validateUser(createReqVO.getInspectorUserId());
        itemService.validateItemExists(createReqVO.getItemId());
        // 1.3 根据产品 + 检验类型自动匹配模板
        MesQcTemplateItemDO templateItem = templateDetailService.getRequiredTemplateByItemIdAndType(
                workOrder.getProductId(), MesQcTypeEnum.IPQC.getType());
        Long templateId = templateItem.getTemplateId();
        // TODO @AI：processId 不应该是前端传递，而是通过 workstation + work order + route 表，联合查询出来的；

        // 2. 插入主表
        MesQcIpqcDO ipqc = BeanUtils.toBean(createReqVO, MesQcIpqcDO.class);
        ipqc.setItemId(workOrder.getProductId());
        ipqc.setTemplateId(templateId).setStatus(MesQcStatusEnum.DRAFT.getStatus());
        ipqcMapper.insert(ipqc);

        // 3. 从模板指标自动生成检验行
        ipqcLineService.createLinesFromTemplate(ipqc.getId(), templateId);
        return ipqc.getId();
    }

    @Override
    public void updateIpqc(MesQcIpqcSaveReqVO updateReqVO) {
        // 1.1 校验存在 + 草稿状态
        validateIpqcStatusPrepare(updateReqVO.getId());
        // 1.2 校验编号唯一
        validateIpqcCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 1.3 校验工单、工位、检测人员、物料存在
        workOrderService.validateWorkOrderExists(updateReqVO.getWorkOrderId());
        workstationService.validateWorkstationExists(updateReqVO.getWorkstationId());
        adminUserApi.validateUser(updateReqVO.getInspectorUserId());
        itemService.validateItemExists(updateReqVO.getItemId());

        // 2. 更新主表
        MesQcIpqcDO updateObj = BeanUtils.toBean(updateReqVO, MesQcIpqcDO.class);
        updateObj.setSourceDocType(null).setSourceDocId(null).setSourceLineId(null); // 不允许修改来源单据
        updateObj.setTemplateId(null); // 不允许修改模板
        updateObj.setItemId(null); // 不允许修改物料
        updateObj.setWorkOrderId(null); // 不允许修改工单
        ipqcMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishIpqc(Long id) {
        // 1.1 校验存在 + 草稿状态
        MesQcIpqcDO ipqc = validateIpqcStatusPrepare(id);
        // 1.2 校验检测结论必填
        if (ipqc.getCheckResult() == null) {
            throw exception(QC_IPQC_CHECK_RESULT_EMPTY);
        }

        // 2. 更新状态为已完成
        ipqcMapper.updateById(new MesQcIpqcDO()
                .setId(id).setStatus(MesQcStatusEnum.FINISHED.getStatus()));

        // 3. 回写来源单据
        writeBackSourceDoc(ipqc);
    }

    /**
     * 回写来源单据（IPQC 完成后）
     *
     * <p>当来源为报工时，拆分待检产出行、完成产出入库、回写报工状态和任务/工单进度
     *
     * @param ipqc 过程检验单
     */
    private void writeBackSourceDoc(MesQcIpqcDO ipqc) {
        if (ipqc.getSourceDocType() == null) {
            return;
        }
        if (ipqc.getSourceDocId() == null) {
            throw new IllegalArgumentException(
                    "IPQC 单[" + ipqc.getId() + "] sourceDocType 非空但 sourceDocId 为空");
        }

        if (Objects.equals(ipqc.getSourceDocType(), MesBizTypeConstants.PRO_FEEDBACK)) {
            feedbackService.updateProFeedbackWhenIpqcFinish(ipqc.getSourceDocId(),
                    ObjectUtil.defaultIfNull(ipqc.getQualifiedQuantity(), BigDecimal.ZERO),
                    ObjectUtil.defaultIfNull(ipqc.getUnqualifiedQuantity(), BigDecimal.ZERO),
                    ObjectUtil.defaultIfNull(ipqc.getLaborScrapQuantity(), BigDecimal.ZERO),
                    ObjectUtil.defaultIfNull(ipqc.getMaterialScrapQuantity(), BigDecimal.ZERO),
                    ObjectUtil.defaultIfNull(ipqc.getOtherScrapQuantity(), BigDecimal.ZERO));
        } else {
            throw new IllegalArgumentException(
                    "IPQC 单[" + ipqc.getId() + "] sourceDocType=" + ipqc.getSourceDocType() + " 无法识别，无法回写来源单据");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIpqc(Long id) {
        // 1. 校验存在 + 草稿状态
        validateIpqcStatusPrepare(id);

        // 2.1 删除主表
        ipqcMapper.deleteById(id);
        // 2.2 级联删除行
        ipqcLineService.deleteListByIpqcId(id);
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
        if (ObjUtil.notEqual(ipqc.getStatus(), MesQcStatusEnum.DRAFT.getStatus())) {
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
        MesQcIpqcDO ipqc = validateIpqcExists(ipqcId);
        // 1. 行级缺陷统计
        ipqcLineService.recalculateLineDefectStats(ipqcId, records);

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
        if (ipqc.getCheckQuantity() != null && ipqc.getCheckQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal checkQty = ipqc.getCheckQuantity();
            criticalRate = BigDecimal.valueOf(totalCritical).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
            majorRate = BigDecimal.valueOf(totalMajor).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
            minorRate = BigDecimal.valueOf(totalMinor).multiply(BigDecimal.valueOf(100))
                    .divide(checkQty, 2, RoundingMode.HALF_UP);
        }
        // 2.3 更新主表
        MesQcIpqcDO updateIpqc = new MesQcIpqcDO().setId(ipqcId)
                .setCriticalQuantity(totalCritical).setMajorQuantity(totalMajor).setMinorQuantity(totalMinor)
                .setCriticalRate(criticalRate).setMajorRate(majorRate).setMinorRate(minorRate);
        ipqcMapper.updateById(updateIpqc);
    }

}
