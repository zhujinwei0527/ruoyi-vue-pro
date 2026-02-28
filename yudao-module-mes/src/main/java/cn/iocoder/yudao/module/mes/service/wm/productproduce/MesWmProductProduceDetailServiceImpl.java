package cn.iocoder.yudao.module.mes.service.wm.productproduce;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.detail.MesWmProductProduceDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productproduce.MesWmProductProduceDetailMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCT_PRODUCE_DETAIL_NOT_EXISTS;

/**
 * MES 生产入库明细 Service 实现类
 */
@Service
@Validated
public class MesWmProductProduceDetailServiceImpl implements MesWmProductProduceDetailService {

    @Resource
    private MesWmProductProduceDetailMapper produceDetailMapper;

    @Resource
    @Lazy
    private MesWmProductProduceLineService produceLineService;

    @Override
    public Long createProductProduceDetail(MesWmProductProduceDetailSaveReqVO createReqVO) {
        // 校验父数据存在
        produceLineService.validateProductProduceLineExists(createReqVO.getLineId());

        // 插入
        MesWmProductProduceDetailDO detail = BeanUtils.toBean(createReqVO, MesWmProductProduceDetailDO.class);
        produceDetailMapper.insert(detail);
        return detail.getId();
    }

    @Override
    public void updateProductProduceDetail(MesWmProductProduceDetailSaveReqVO updateReqVO) {
        // 校验存在
        validateProductProduceDetailExists(updateReqVO.getId());
        // 校验父数据存在
        produceLineService.validateProductProduceLineExists(updateReqVO.getLineId());

        // 更新
        MesWmProductProduceDetailDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductProduceDetailDO.class);
        produceDetailMapper.updateById(updateObj);
    }

    @Override
    public void deleteProductProduceDetail(Long id) {
        // 校验存在
        validateProductProduceDetailExists(id);
        // 删除
        produceDetailMapper.deleteById(id);
    }

    @Override
    public MesWmProductProduceDetailDO getProductProduceDetail(Long id) {
        return produceDetailMapper.selectById(id);
    }

    @Override
    public List<MesWmProductProduceDetailDO> getProductProduceDetailListByLineId(Long lineId) {
        return produceDetailMapper.selectListByLineId(lineId);
    }

    @Override
    public List<MesWmProductProduceDetailDO> getProductProduceDetailListByProduceId(Long produceId) {
        return produceDetailMapper.selectListByProduceId(produceId);
    }

    @Override
    public void deleteProductProduceDetailByProduceId(Long produceId) {
        produceDetailMapper.deleteByProduceId(produceId);
    }

    private void validateProductProduceDetailExists(Long id) {
        if (produceDetailMapper.selectById(id) == null) {
            throw exception(WM_PRODUCT_PRODUCE_DETAIL_NOT_EXISTS);
        }
    }

}
