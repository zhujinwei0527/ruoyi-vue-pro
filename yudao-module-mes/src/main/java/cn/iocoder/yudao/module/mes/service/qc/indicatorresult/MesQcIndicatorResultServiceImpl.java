package cn.iocoder.yudao.module.mes.service.qc.indicatorresult;

import cn.hutool.core.collection.CollUtil;
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
        // 1. 校验明细不为空
        // TODO @AI：是不是 MesQcIndicatorResultSaveReqVO validator 参数校验；
        List<MesQcIndicatorResultSaveReqVO.Item> items = createReqVO.getItems();
        if (CollUtil.isEmpty(items)) {
            throw exception(QC_RESULT_ITEMS_EMPTY);
        }

        // 2. 根据 qcType 查询源质检单，获取 itemId
        Long itemId = getItemIdFromQcDoc(createReqVO.getQcId(), createReqVO.getQcType());

        // 3.1 插入主表
        MesQcIndicatorResultDO result = BeanUtils.toBean(createReqVO, MesQcIndicatorResultDO.class);
        result.setItemId(itemId);
        resultMapper.insert(result);
        // 3.2 批量插入明细
        List<MesQcIndicatorResultDetailDO> details = BeanUtils.toBean(items, MesQcIndicatorResultDetailDO.class);
        details.forEach(detail -> detail.setResultId(result.getId()));
        resultDetailMapper.insertBatch(details);
        return result.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIndicatorResult(MesQcIndicatorResultSaveReqVO updateReqVO) {
        // 1. 校验存在
        validateIndicatorResultExists(updateReqVO.getId());

        // 2. 校验明细不为空
        // TODO @AI：是不是 MesQcIndicatorResultSaveReqVO validator 参数校验；
        List<MesQcIndicatorResultSaveReqVO.Item> items = updateReqVO.getItems();
        if (CollUtil.isEmpty(items)) {
            throw exception(QC_RESULT_ITEMS_EMPTY);
        }

        // 2.1 更新主表
        MesQcIndicatorResultDO updateObj = BeanUtils.toBean(updateReqVO, MesQcIndicatorResultDO.class);
        resultMapper.updateById(updateObj);

        // TODO @AI：直接 for 循环，resultMapper.insertOrUpdate() 简单粗暴点！
        // 4. 更新明细：diff 方式（有 id → 更新，无 id → 新增，不在列表中 → 删除）
        List<MesQcIndicatorResultDetailDO> existingDetails = resultDetailMapper.selectListByResultId(updateReqVO.getId());
        Map<Long, MesQcIndicatorResultDetailDO> existingMap = convertMap(existingDetails, MesQcIndicatorResultDetailDO::getId);

        // 4.1 收集前端传入的、已有 id 的集合
        Set<Long> newItemIds = convertSet(items, MesQcIndicatorResultSaveReqVO.Item::getId);
        newItemIds.remove(null);

        // 4.2 遍历传入的明细：有 id 则更新，无 id 则新增
        for (MesQcIndicatorResultSaveReqVO.Item item : items) {
            MesQcIndicatorResultDetailDO detail = BeanUtils.toBean(item, MesQcIndicatorResultDetailDO.class);
            detail.setResultId(updateReqVO.getId());
            if (item.getId() != null && existingMap.containsKey(item.getId())) {
                // 更新已有记录
                resultDetailMapper.updateById(detail);
            } else {
                // 新增记录
                detail.setId(null);
                resultDetailMapper.insert(detail);
            }
        }

        // 4.3 删除不在新列表中的旧记录
        for (MesQcIndicatorResultDetailDO existing : existingDetails) {
            if (!newItemIds.contains(existing.getId())) {
                resultDetailMapper.deleteById(existing.getId());
            }
        }
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
            // TODO @AI：使用 iqcService 的 validateIqcExists 方法，这样更聚焦！
            MesQcIqcDO iqc = iqcService.getIqc(qcId);
            if (iqc == null) {
                throw exception(QC_RESULT_SOURCE_DOC_NOT_EXISTS);
            }
            return iqc.getItemId();
        }
        // TODO @芋艿：IPQC/OQC/RQC 模块迁移后实现
        throw exception(QC_RESULT_SOURCE_DOC_TYPE_INVALID);
    }

}
