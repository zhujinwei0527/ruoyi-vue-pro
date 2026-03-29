package cn.iocoder.yudao.module.mes.service.wm.returnvendor;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnvendor.vo.detail.MesWmReturnVendorDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnvendor.MesWmReturnVendorDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnvendor.MesWmReturnVendorLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.returnvendor.MesWmReturnVendorDetailMapper;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_RETURN_VENDOR_DETAIL_ITEM_MISMATCH;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_RETURN_VENDOR_DETAIL_NOT_EXISTS;

/**
 * MES 供应商退货明细 Service 实现类
 */
@Service
@Validated
public class MesWmReturnVendorDetailServiceImpl implements MesWmReturnVendorDetailService {

    @Resource
    private MesWmReturnVendorDetailMapper returnVendorDetailMapper;

    @Resource
    @Lazy
    private MesWmReturnVendorLineService returnVendorLineService;

    @Resource
    private MesMdItemService itemService;

    @Override
    public Long createReturnVendorDetail(MesWmReturnVendorDetailSaveReqVO createReqVO) {
        // 校验父数据存在
        MesWmReturnVendorLineDO line = returnVendorLineService.validateReturnVendorLineExists(createReqVO.getLineId());
        // 校验物料存在
        itemService.validateItemExists(createReqVO.getItemId());
        // 校验物料与行物料一致
        validateItemConsistency(createReqVO.getItemId(), line);

        // 插入
        MesWmReturnVendorDetailDO detail = BeanUtils.toBean(createReqVO, MesWmReturnVendorDetailDO.class);
        returnVendorDetailMapper.insert(detail);
        return detail.getId();
    }

    @Override
    public void updateReturnVendorDetail(MesWmReturnVendorDetailSaveReqVO updateReqVO) {
        // 校验存在
        validateReturnVendorDetailExists(updateReqVO.getId());
        // 校验父数据存在
        MesWmReturnVendorLineDO line = returnVendorLineService.validateReturnVendorLineExists(updateReqVO.getLineId());
        // 校验物料存在
        itemService.validateItemExists(updateReqVO.getItemId());
        // 校验物料与行物料一致
        validateItemConsistency(updateReqVO.getItemId(), line);

        // 更新
        MesWmReturnVendorDetailDO updateObj = BeanUtils.toBean(updateReqVO, MesWmReturnVendorDetailDO.class);
        returnVendorDetailMapper.updateById(updateObj);
    }

    @Override
    public void deleteReturnVendorDetail(Long id) {
        // 校验存在
        validateReturnVendorDetailExists(id);
        // 删除
        returnVendorDetailMapper.deleteById(id);
    }

    @Override
    public MesWmReturnVendorDetailDO getReturnVendorDetail(Long id) {
        return returnVendorDetailMapper.selectById(id);
    }

    @Override
    public List<MesWmReturnVendorDetailDO> getReturnVendorDetailListByLineId(Long lineId) {
        return returnVendorDetailMapper.selectListByLineId(lineId);
    }

    @Override
    public List<MesWmReturnVendorDetailDO> getReturnVendorDetailListByReturnId(Long returnId) {
        return returnVendorDetailMapper.selectListByReturnId(returnId);
    }

    @Override
    public void deleteReturnVendorDetailByReturnId(Long returnId) {
        returnVendorDetailMapper.deleteByReturnId(returnId);
    }

    @Override
    public void deleteReturnVendorDetailByLineId(Long lineId) {
        returnVendorDetailMapper.deleteByLineId(lineId);
    }

    private void validateReturnVendorDetailExists(Long id) {
        if (returnVendorDetailMapper.selectById(id) == null) {
            throw exception(WM_RETURN_VENDOR_DETAIL_NOT_EXISTS);
        }
    }

    /**
     * 校验明细物料与行物料一致
     */
    private void validateItemConsistency(Long detailItemId, MesWmReturnVendorLineDO line) {
        if (ObjUtil.notEqual(detailItemId, line.getItemId())) {
            throw exception(WM_RETURN_VENDOR_DETAIL_ITEM_MISMATCH);
        }
    }

}
