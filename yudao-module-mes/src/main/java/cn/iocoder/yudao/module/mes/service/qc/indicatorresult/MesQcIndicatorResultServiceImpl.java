package cn.iocoder.yudao.module.mes.service.qc.indicatorresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo.MesQcIndicatorResultPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo.MesQcIndicatorResultSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicatorresult.MesQcIndicatorResultDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicatorresult.MesQcIndicatorResultDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.indicatorresult.MesQcIndicatorResultDetailMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.indicatorresult.MesQcIndicatorResultMapper;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import cn.iocoder.yudao.module.mes.service.qc.lqc.MesQcIqcService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 检验结果 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesQcIndicatorResultServiceImpl implements MesQcIndicatorResultService {

    @Resource
    private MesQcIndicatorResultMapper resultMapper;
    @Resource
    private MesQcIndicatorResultDetailMapper resultDetailMapper;

    @Resource
    private MesQcIqcService iqcService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createIndicatorResult(MesQcIndicatorResultSaveReqVO createReqVO) {
        // 1. 根据 qcType 查询源质检单，获取 itemId
        Long itemId = getItemIdFromQcDoc(createReqVO.getQcId(), createReqVO.getQcType());

        // 2.1 插入主表
        MesQcIndicatorResultDO result = BeanUtils.toBean(createReqVO, MesQcIndicatorResultDO.class);
        result.setItemId(itemId);
        resultMapper.insert(result);
        // 2.2 批量插入明细
        List<MesQcIndicatorResultDetailDO> details = BeanUtils.toBean(createReqVO.getItems(),
                MesQcIndicatorResultDetailDO.class);
        details.forEach(detail -> detail.setResultId(result.getId()));
        resultDetailMapper.insertBatch(details);
        return result.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIndicatorResult(MesQcIndicatorResultSaveReqVO updateReqVO) {
        // 1. 校验存在
        validateIndicatorResultExists(updateReqVO.getId());

        // 2.1 更新主表
        MesQcIndicatorResultDO updateObj = BeanUtils.toBean(updateReqVO, MesQcIndicatorResultDO.class);
        resultMapper.updateById(updateObj);
        // 2.2 批量更新明细
        List<MesQcIndicatorResultDetailDO> details = BeanUtils.toBean(updateReqVO.getItems(),
                MesQcIndicatorResultDetailDO.class);
        resultDetailMapper.insertOrUpdate(details);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIndicatorResult(Long id) {
        // 1. 校验存在
        validateIndicatorResultExists(id);

        // 2.1 级联删除明细
        resultDetailMapper.deleteByResultId(id);
        // 2.2 删除主表
        resultMapper.deleteById(id);
    }

    @Override
    public MesQcIndicatorResultDO getIndicatorResult(Long id) {
        return resultMapper.selectById(id);
    }

    @Override
    public PageResult<MesQcIndicatorResultDO> getIndicatorResultPage(MesQcIndicatorResultPageReqVO pageReqVO) {
        return resultMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesQcIndicatorResultDetailDO> getIndicatorResultDetailListByResultId(Long resultId) {
        return resultDetailMapper.selectListByResultId(resultId);
    }

    // ==================== 私有方法 ====================

    private MesQcIndicatorResultDO validateIndicatorResultExists(Long id) {
        MesQcIndicatorResultDO result = resultMapper.selectById(id);
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
            MesQcIqcDO iqc = iqcService.validateIqcExists(qcId);
            return iqc.getItemId();
        }
        // TODO @芋艿：IPQC/OQC/RQC 模块迁移后实现
        throw exception(QC_RESULT_SOURCE_DOC_TYPE_INVALID);
    }

}
