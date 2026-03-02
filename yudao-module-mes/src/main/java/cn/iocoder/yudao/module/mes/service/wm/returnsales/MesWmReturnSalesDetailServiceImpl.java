package cn.iocoder.yudao.module.mes.service.wm.returnsales;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnsales.vo.detail.MesWmReturnSalesDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnsales.MesWmReturnSalesDetailDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.returnsales.MesWmReturnSalesDetailMapper;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_RETURN_SALES_DETAIL_NOT_EXISTS;

/**
 * MES 销售退货明细 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesWmReturnSalesDetailServiceImpl implements MesWmReturnSalesDetailService {

    @Resource
    private MesWmReturnSalesDetailMapper returnSalesDetailMapper;

    @Resource
    @Lazy
    private MesWmReturnSalesLineService returnSalesLineService;

    @Resource
    private MesMdItemService itemService;

    @Override
    public Long createReturnSalesDetail(MesWmReturnSalesDetailSaveReqVO createReqVO) {
        // 校验父数据存在
        returnSalesLineService.validateReturnSalesLineExists(createReqVO.getLineId());
        // 校验物料存在
        itemService.validateItemExists(createReqVO.getItemId());

        // 插入
        MesWmReturnSalesDetailDO detail = BeanUtils.toBean(createReqVO, MesWmReturnSalesDetailDO.class);
        returnSalesDetailMapper.insert(detail);
        return detail.getId();
    }

    @Override
    public void updateReturnSalesDetail(MesWmReturnSalesDetailSaveReqVO updateReqVO) {
        // 校验存在
        validateReturnSalesDetailExists(updateReqVO.getId());
        // 校验父数据存在
        returnSalesLineService.validateReturnSalesLineExists(updateReqVO.getLineId());
        // 校验物料存在
        itemService.validateItemExists(updateReqVO.getItemId());

        // 更新
        MesWmReturnSalesDetailDO updateObj = BeanUtils.toBean(updateReqVO, MesWmReturnSalesDetailDO.class);
        returnSalesDetailMapper.updateById(updateObj);
    }

    @Override
    public void deleteReturnSalesDetail(Long id) {
        // 校验存在
        validateReturnSalesDetailExists(id);
        // 删除
        returnSalesDetailMapper.deleteById(id);
    }

    @Override
    public MesWmReturnSalesDetailDO getReturnSalesDetail(Long id) {
        return returnSalesDetailMapper.selectById(id);
    }

    @Override
    public List<MesWmReturnSalesDetailDO> getReturnSalesDetailListByLineId(Long lineId) {
        return returnSalesDetailMapper.selectListByLineId(lineId);
    }

    @Override
    public List<MesWmReturnSalesDetailDO> getReturnSalesDetailListByReturnId(Long returnId) {
        return returnSalesDetailMapper.selectListByReturnId(returnId);
    }

    @Override
    public void deleteReturnSalesDetailByReturnId(Long returnId) {
        returnSalesDetailMapper.deleteByReturnId(returnId);
    }

    private void validateReturnSalesDetailExists(Long id) {
        if (returnSalesDetailMapper.selectById(id) == null) {
            throw exception(WM_RETURN_SALES_DETAIL_NOT_EXISTS);
        }
    }

}
