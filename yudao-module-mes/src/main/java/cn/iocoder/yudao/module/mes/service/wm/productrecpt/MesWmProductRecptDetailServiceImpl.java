package cn.iocoder.yudao.module.mes.service.wm.productrecpt;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.detail.MesWmProductRecptDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt.MesWmProductRecptDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productrecpt.MesWmProductRecptDetailMapper;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseAreaService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 产品收货单明细 Service 实现类
 */
@Service
@Validated
public class MesWmProductRecptDetailServiceImpl implements MesWmProductRecptDetailService {

    @Resource
    private MesWmProductRecptDetailMapper productRecptDetailMapper;

    @Resource
    @Lazy
    private MesWmProductRecptService productRecptService;

    @Resource
    private MesWmWarehouseAreaService warehouseAreaService;

    @Override
    public Long createProductRecptDetail(MesWmProductRecptDetailSaveReqVO createReqVO) {
        // 校验父单据存在且为可编辑状态
        productRecptService.validateProductRecptEditable(createReqVO.getRecptId());
        // 校验库区关系
        warehouseAreaService.validateWarehouseAreaExists(
                createReqVO.getWarehouseId(), createReqVO.getLocationId(), createReqVO.getAreaId());

        MesWmProductRecptDetailDO detail = BeanUtils.toBean(createReqVO, MesWmProductRecptDetailDO.class);
        productRecptDetailMapper.insert(detail);
        return detail.getId();
    }

    @Override
    public void updateProductRecptDetail(MesWmProductRecptDetailSaveReqVO updateReqVO) {
        // 校验存在
        MesWmProductRecptDetailDO detail = validateProductRecptDetailExists(updateReqVO.getId());
        // 校验父单据存在且为可编辑状态
        productRecptService.validateProductRecptEditable(detail.getRecptId());
        // 校验库区关系
        warehouseAreaService.validateWarehouseAreaExists(
                updateReqVO.getWarehouseId(), updateReqVO.getLocationId(), updateReqVO.getAreaId());

        // 更新
        MesWmProductRecptDetailDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductRecptDetailDO.class);
        productRecptDetailMapper.updateById(updateObj);
    }

    @Override
    public void deleteProductRecptDetail(Long id) {
        // 校验存在
        validateProductRecptDetailExists(id);
        // 删除
        productRecptDetailMapper.deleteById(id);
    }

    @Override
    public MesWmProductRecptDetailDO getProductRecptDetail(Long id) {
        return productRecptDetailMapper.selectById(id);
    }

    @Override
    public List<MesWmProductRecptDetailDO> getProductRecptDetailListByRecptId(Long recptId) {
        return productRecptDetailMapper.selectListByRecptId(recptId);
    }

    @Override
    public List<MesWmProductRecptDetailDO> getProductRecptDetailListByLineId(Long lineId) {
        return productRecptDetailMapper.selectListByLineId(lineId);
    }

    @Override
    public void deleteProductRecptDetailByLineId(Long lineId) {
        productRecptDetailMapper.deleteByLineId(lineId);
    }

    @Override
    public void deleteProductRecptDetailByRecptId(Long recptId) {
        productRecptDetailMapper.deleteByRecptId(recptId);
    }

    private MesWmProductRecptDetailDO validateProductRecptDetailExists(Long id) {
        MesWmProductRecptDetailDO detail = productRecptDetailMapper.selectById(id);
        if (detail == null) {
            throw exception(WM_PRODUCT_RECPT_DETAIL_NOT_EXISTS);
        }
        return detail;
    }

}
