package cn.iocoder.yudao.module.mes.service.qc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.vo.MesQcIndicatorPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.vo.MesQcIndicatorSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.MesQcIndicatorDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.MesQcIndicatorMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 质检指标 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesQcIndicatorServiceImpl implements MesQcIndicatorService {

    @Resource
    private MesQcIndicatorMapper indicatorMapper;

    @Override
    public Long createIndicator(MesQcIndicatorSaveReqVO createReqVO) {
        // 校验编码唯一
        validateIndicatorCodeUnique(null, createReqVO.getCode());
        // 校验名称唯一
        validateIndicatorNameUnique(null, createReqVO.getName());

        // 插入
        MesQcIndicatorDO indicator = BeanUtils.toBean(createReqVO, MesQcIndicatorDO.class);
        indicatorMapper.insert(indicator);
        return indicator.getId();
    }

    @Override
    public void updateIndicator(MesQcIndicatorSaveReqVO updateReqVO) {
        // 校验存在
        validateIndicatorExists(updateReqVO.getId());
        // 校验编码唯一
        validateIndicatorCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 校验名称唯一
        validateIndicatorNameUnique(updateReqVO.getId(), updateReqVO.getName());

        // 更新
        MesQcIndicatorDO updateObj = BeanUtils.toBean(updateReqVO, MesQcIndicatorDO.class);
        indicatorMapper.updateById(updateObj);
    }

    @Override
    public void deleteIndicator(Long id) {
        // 校验存在
        validateIndicatorExists(id);
        // 删除
        indicatorMapper.deleteById(id);
    }

    private void validateIndicatorExists(Long id) {
        if (indicatorMapper.selectById(id) == null) {
            throw exception(QC_INDICATOR_NOT_EXISTS);
        }
    }

    private void validateIndicatorCodeUnique(Long id, String code) {
        MesQcIndicatorDO indicator = indicatorMapper.selectByCode(code);
        if (indicator == null) {
            return;
        }
        if (ObjUtil.notEqual(indicator.getId(), id)) {
            throw exception(QC_INDICATOR_CODE_DUPLICATE);
        }
    }

    private void validateIndicatorNameUnique(Long id, String name) {
        MesQcIndicatorDO indicator = indicatorMapper.selectByName(name);
        if (indicator == null) {
            return;
        }
        if (ObjUtil.notEqual(indicator.getId(), id)) {
            throw exception(QC_INDICATOR_NAME_DUPLICATE);
        }
    }

    @Override
    public MesQcIndicatorDO getIndicator(Long id) {
        return indicatorMapper.selectById(id);
    }

    @Override
    public PageResult<MesQcIndicatorDO> getIndicatorPage(MesQcIndicatorPageReqVO pageReqVO) {
        return indicatorMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesQcIndicatorDO> getIndicatorList() {
        return indicatorMapper.selectList();
    }

    @Override
    public List<MesQcIndicatorDO> getIndicatorList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return indicatorMapper.selectByIds(ids);
    }

}
