package cn.iocoder.yudao.module.mes.service.qc.lqc;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.line.MesQcIqcLinePageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcLineDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateIndicatorDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.lqc.MesQcIqcLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.template.MesQcTemplateIndicatorMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

/**
 * MES 来料检验单行 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesQcIqcLineServiceImpl implements MesQcIqcLineService {

    @Resource
    private MesQcIqcLineMapper iqcLineMapper;
    @Resource
    private MesQcTemplateIndicatorMapper templateIndicatorMapper;

    @Override
    public MesQcIqcLineDO getIqcLine(Long id) {
        return iqcLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesQcIqcLineDO> getIqcLinePage(MesQcIqcLinePageReqVO pageReqVO) {
        return iqcLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesQcIqcLineDO> selectListByIqcId(Long iqcId) {
        return iqcLineMapper.selectListByIqcId(iqcId);
    }

    @Override
    public void createLinesFromTemplate(Long iqcId, Long templateId) {
        List<MesQcTemplateIndicatorDO> indicators = templateIndicatorMapper.selectListByTemplateId(templateId);
        List<MesQcIqcLineDO> lines = convertList(indicators, indicator -> new MesQcIqcLineDO()
                .setIqcId(iqcId).setIndicatorId(indicator.getIndicatorId())
                .setToolId(null) // TODO @芋艿：模板指标暂无 toolId，后续可扩展
                .setCheckMethod(indicator.getCheckMethod())
                .setStandardValue(indicator.getStandardValue()).setUnitMeasureId(indicator.getUnitMeasureId())
                .setMaxThreshold(indicator.getThresholdMax()).setMinThreshold(indicator.getThresholdMin())
                .setCriticalQuantity(0).setMajorQuantity(0).setMinorQuantity(0));
        iqcLineMapper.insertBatch(lines);
    }

    @Override
    public void batchUpdateDefectStats(List<MesQcIqcLineDO> lines) {
        for (MesQcIqcLineDO line : lines) {
            iqcLineMapper.updateById(line);
        }
    }

    @Override
    public void deleteByIqcId(Long iqcId) {
        iqcLineMapper.deleteByIqcId(iqcId);
    }

}
