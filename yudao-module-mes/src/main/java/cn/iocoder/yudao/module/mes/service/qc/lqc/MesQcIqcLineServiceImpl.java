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
        // TODO @AI：convertList；
        for (MesQcTemplateIndicatorDO indicator : indicators) {
            // TODO @AI：链式设置；
            MesQcIqcLineDO line = new MesQcIqcLineDO();
            line.setIqcId(iqcId);
            line.setIndicatorId(indicator.getIndicatorId());
            line.setToolId(null); // TODO @芋艿：模板指标暂无 toolId，后续可扩展
            line.setCheckMethod(indicator.getCheckMethod());
            line.setStandardValue(indicator.getStandardValue());
            line.setUnitMeasureId(indicator.getUnitMeasureId());
            line.setMaxThreshold(indicator.getThresholdMax());
            line.setMinThreshold(indicator.getThresholdMin());
            line.setCriticalQuantity(0);
            line.setMajorQuantity(0);
            line.setMinorQuantity(0);
            // TODO @AI：insertBatch；
            iqcLineMapper.insert(line);
        }
    }

    @Override
    public void deleteByIqcId(Long iqcId) {
        iqcLineMapper.deleteByIqcId(iqcId);
    }

}
