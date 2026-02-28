package cn.iocoder.yudao.module.mes.service.wm.returnvendor;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnvendor.vo.detail.MesWmReturnVendorDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnvendor.MesWmReturnVendorDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.returnvendor.MesWmReturnVendorDetailMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
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

    @Override
    public Long createReturnVendorDetail(MesWmReturnVendorDetailSaveReqVO createReqVO) {
        // 校验父数据存在
        returnVendorLineService.validateReturnVendorLineExists(createReqVO.getLineId());
        // TODO @AI：item 存在

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
        returnVendorLineService.validateReturnVendorLineExists(updateReqVO.getLineId());
        // TODO @AI：item 存在

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
    public List<MesWmReturnVendorDetailDO> getReturnVendorDetailListByReturnVendorId(Long returnVendorId) {
        return returnVendorDetailMapper.selectListByReturnVendorId(returnVendorId);
    }

    @Override
    public void deleteReturnVendorDetailByReturnVendorId(Long returnVendorId) {
        returnVendorDetailMapper.deleteByReturnVendorId(returnVendorId);
    }

    private void validateReturnVendorDetailExists(Long id) {
        if (returnVendorDetailMapper.selectById(id) == null) {
            throw exception(WM_RETURN_VENDOR_DETAIL_NOT_EXISTS);
        }
    }

}
