package cn.iocoder.yudao.module.mes.service.qc.indicatorresult;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo.MesQcResultDetailRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo.MesQcResultDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo.MesQcResultPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo.MesQcResultSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicator.MesQcIndicatorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcLineDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicatorresult.MesQcResultDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicatorresult.MesQcResultDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.tm.tool.MesTmToolDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.lqc.MesQcIqcLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.indicatorresult.MesQcResultDetailMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.indicatorresult.MesQcResultMapper;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.qc.indicator.MesQcIndicatorService;
import cn.iocoder.yudao.module.mes.service.qc.lqc.MesQcIqcService;
import cn.iocoder.yudao.module.mes.service.tm.tool.MesTmToolService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.MapUtils.findAndThen;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 检验结果 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesQcResultServiceImpl implements MesQcResultService {

    @Resource
    private MesQcResultMapper resultMapper;
    @Resource
    private MesQcResultDetailMapper resultDetailMapper;

    @Resource
    private MesQcIqcService iqcService;
    @Resource
    private MesQcIqcLineMapper iqcLineMapper;
    @Resource
    private MesQcIndicatorService indicatorService;
    @Resource
    private MesMdUnitMeasureService unitMeasureService;
    @Resource
    private MesTmToolService toolService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createResult(MesQcResultSaveReqVO createReqVO) {
        // 1. 校验明细不为空
        List<MesQcResultDetailSaveReqVO> items = createReqVO.getItems();
        if (CollUtil.isEmpty(items)) {
            throw exception(QC_RESULT_ITEMS_EMPTY);
        }

        // 2. 根据 qcType 查询源质检单，获取 itemId
        Long itemId = getItemIdFromQcDoc(createReqVO.getQcId(), createReqVO.getQcType());

        // 3. 插入主表
        MesQcResultDO result = BeanUtils.toBean(createReqVO, MesQcResultDO.class);
        result.setItemId(itemId);
        resultMapper.insert(result);

        // 4. 批量插入明细
        List<MesQcResultDetailDO> details = BeanUtils.toBean(items, MesQcResultDetailDO.class);
        details.forEach(detail -> detail.setResultId(result.getId()));
        resultDetailMapper.insertBatch(details);
        return result.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateResult(MesQcResultSaveReqVO updateReqVO) {
        // 1. 校验存在
        validateResultExists(updateReqVO.getId());

        // 2. 校验明细不为空
        List<MesQcResultDetailSaveReqVO> items = updateReqVO.getItems();
        if (CollUtil.isEmpty(items)) {
            throw exception(QC_RESULT_ITEMS_EMPTY);
        }

        // 3. 更新主表
        MesQcResultDO updateObj = BeanUtils.toBean(updateReqVO, MesQcResultDO.class);
        resultMapper.updateById(updateObj);

        // 4. 更新明细：先删后插
        resultDetailMapper.deleteByResultId(updateReqVO.getId());
        List<MesQcResultDetailDO> details = BeanUtils.toBean(items, MesQcResultDetailDO.class);
        details.forEach(detail -> detail.setResultId(updateReqVO.getId()));
        resultDetailMapper.insertBatch(details);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteResult(Long id) {
        // 1. 校验存在
        validateResultExists(id);
        // 2. 级联删除明细
        resultDetailMapper.deleteByResultId(id);
        // 3. 删除主表
        resultMapper.deleteById(id);
    }

    @Override
    public MesQcResultDO getResult(Long id) {
        return resultMapper.selectById(id);
    }

    @Override
    public PageResult<MesQcResultDO> getResultPage(MesQcResultPageReqVO pageReqVO) {
        return resultMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesQcResultDetailRespVO> getResultDetailList(Long resultId, Long qcId, Integer qcType) {
        return buildDetailRespVOList(resultId, qcId, qcType);
    }

    @Override
    public List<MesQcResultDetailRespVO> getDetailTemplate(Long qcId, Integer qcType) {
        return buildDetailRespVOList(null, qcId, qcType);
    }

    // ==================== 私有方法 ====================

    private MesQcResultDO validateResultExists(Long id) {
        MesQcResultDO result = resultMapper.selectById(id);
        if (result == null) {
            throw exception(QC_RESULT_NOT_EXISTS);
        }
        return result;
    }

    /**
     * 根据 qcType 查询源质检单，获取 itemId
     */
    private Long getItemIdFromQcDoc(Long qcId, Integer qcType) {
        if (Objects.equals(qcType, MesQcTypeEnum.IQC.getType())) {
            MesQcIqcDO iqc = iqcService.getIqc(qcId);
            if (iqc == null) {
                throw exception(QC_RESULT_SOURCE_DOC_NOT_EXISTS);
            }
            return iqc.getItemId();
        }
        // TODO @芋艿：IPQC/OQC/RQC 模块迁移后实现
        throw exception(QC_RESULT_SOURCE_DOC_TYPE_INVALID);
    }

    /**
     * 组装明细 VO 列表：从 IQC line + indicator + tool + unitMeasure + 已有结果明细 三方组装
     *
     * @param resultId 检验结果 ID，为 null 时表示获取空值模板
     * @param qcId     质检单 ID
     * @param qcType   质检类型
     */
    private List<MesQcResultDetailRespVO> buildDetailRespVOList(Long resultId, Long qcId, Integer qcType) {
        // 1. 获取检验单行列表
        List<MesQcIqcLineDO> lines;
        if (Objects.equals(qcType, MesQcTypeEnum.IQC.getType())) {
            lines = iqcLineMapper.selectListByIqcId(qcId);
        } else {
            // TODO @芋艿：IPQC/OQC/RQC 模块迁移后实现
            return Collections.emptyList();
        }
        if (CollUtil.isEmpty(lines)) {
            return Collections.emptyList();
        }

        // 2. 批量查询关联信息
        Map<Long, MesQcIndicatorDO> indicatorMap = indicatorService.getIndicatorMap(
                convertSet(lines, MesQcIqcLineDO::getIndicatorId));
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(lines, MesQcIqcLineDO::getUnitMeasureId));

        // 3. 查询已有结果明细（如有 resultId）
        Map<Long, MesQcResultDetailDO> detailMap;
        if (resultId != null) {
            List<MesQcResultDetailDO> details = resultDetailMapper.selectListByResultId(resultId);
            detailMap = convertMap(details, MesQcResultDetailDO::getIndicatorId);
        } else {
            detailMap = Collections.emptyMap();
        }

        // 4. 遍历行，组装 VO
        List<MesQcResultDetailRespVO> voList = new ArrayList<>(lines.size());
        for (MesQcIqcLineDO line : lines) {
            MesQcResultDetailRespVO vo = new MesQcResultDetailRespVO();
            // 来自 IQC line
            vo.setIndicatorId(line.getIndicatorId());
            vo.setToolId(line.getToolId());
            vo.setUnitMeasureId(line.getUnitMeasureId());
            vo.setCheckMethod(line.getCheckMethod());
            vo.setStandardValue(line.getStandardValue());
            vo.setMaxThreshold(line.getMaxThreshold());
            vo.setMinThreshold(line.getMinThreshold());

            // 来自 indicator
            findAndThen(indicatorMap, line.getIndicatorId(), indicator -> {
                vo.setIndicatorCode(indicator.getCode());
                vo.setIndicatorName(indicator.getName());
                vo.setIndicatorType(indicator.getType());
                vo.setValueType(indicator.getResultType());
                vo.setValueSpecification(indicator.getResultSpecification());
            });

            // 来自 unitMeasure
            findAndThen(unitMeasureMap, line.getUnitMeasureId(),
                    unit -> vo.setUnitMeasureName(unit.getName()));

            // 来自 tool（逐个查询，数量通常较少）
            if (line.getToolId() != null) {
                MesTmToolDO tool = toolService.getTool(line.getToolId());
                if (tool != null) {
                    vo.setToolName(tool.getName());
                }
            }

            // 来自已有结果明细（如有）
            MesQcResultDetailDO detail = detailMap.get(line.getIndicatorId());
            if (detail != null) {
                vo.setId(detail.getId());
                vo.setResultId(detail.getResultId());
                vo.setValueFloat(detail.getValueFloat());
                vo.setValueInteger(detail.getValueInteger());
                vo.setValueText(detail.getValueText());
                vo.setValueDict(detail.getValueDict());
                vo.setValueFile(detail.getValueFile());
            }

            voList.add(vo);
        }
        return voList;
    }

}
