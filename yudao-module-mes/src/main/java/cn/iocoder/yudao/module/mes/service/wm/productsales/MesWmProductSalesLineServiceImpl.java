package cn.iocoder.yudao.module.mes.service.wm.productsales;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productsales.vo.line.MesWmProductSalesLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productsales.vo.line.MesWmProductSalesLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productsales.MesWmProductSalesLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productsales.MesWmProductSalesLineMapper;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCT_SALES_LINE_NOT_EXISTS;

/**
 * MES 销售出库单行 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesWmProductSalesLineServiceImpl implements MesWmProductSalesLineService {

    @Resource
    private MesWmProductSalesLineMapper productSalesLineMapper;

    @Resource
    @Lazy
    private MesWmProductSalesService productSalesService;

    @Resource
    private MesWmProductSalesDetailService productSalesDetailService;

    @Resource
    private MesMdItemService itemService;

    @Override
    public Long createProductSalesLine(MesWmProductSalesLineSaveReqVO createReqVO) {
        // 校验物料存在
        itemService.validateItemExists(createReqVO.getItemId());

        // 新增
        MesWmProductSalesLineDO line = BeanUtils.toBean(createReqVO, MesWmProductSalesLineDO.class);
        line.setPickedQuantity(BigDecimal.ZERO);
        productSalesLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateProductSalesLine(MesWmProductSalesLineSaveReqVO updateReqVO) {
        // 校验存在
        validateProductSalesLineExists(updateReqVO.getId());
        // 校验物料存在
        itemService.validateItemExists(updateReqVO.getItemId());

        // 更新
        MesWmProductSalesLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductSalesLineDO.class);
        productSalesLineMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductSalesLine(Long id) {
        // 校验存在
        validateProductSalesLineExists(id);

        // 级联删除明细
        productSalesDetailService.deleteProductSalesDetailByLineId(id);
        // 删除
        productSalesLineMapper.deleteById(id);
    }

    @Override
    public MesWmProductSalesLineDO getProductSalesLine(Long id) {
        return productSalesLineMapper.selectById(id);
    }

    @Override
    public List<MesWmProductSalesLineDO> getProductSalesLineListBySalesId(Long salesId) {
        return productSalesLineMapper.selectListBySalesId(salesId);
    }

    @Override
    public void deleteProductSalesLineBySalesId(Long salesId) {
        productSalesLineMapper.deleteBySalesId(salesId);
    }

    @Override
    public cn.iocoder.yudao.framework.common.pojo.PageResult<MesWmProductSalesLineDO> getProductSalesLinePage(
            MesWmProductSalesLinePageReqVO pageReqVO) {
        return productSalesLineMapper.selectPage(pageReqVO);
    }

    private MesWmProductSalesLineDO validateProductSalesLineExists(Long id) {
        MesWmProductSalesLineDO line = productSalesLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_PRODUCT_SALES_LINE_NOT_EXISTS);
        }
        return line;
    }

}
