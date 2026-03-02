package cn.iocoder.yudao.module.mes.service.wm.productsales;

import cn.iocoder.yudao.module.mes.controller.admin.wm.productsales.vo.line.MesWmProductSalesLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productsales.vo.line.MesWmProductSalesLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productsales.MesWmProductSalesLineDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 销售出库单行 Service 接口
 *
 * @author 芋道源码
 */
public interface MesWmProductSalesLineService {

    /**
     * 创建销售出库单行
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProductSalesLine(@Valid MesWmProductSalesLineSaveReqVO createReqVO);

    /**
     * 修改销售出库单行
     *
     * @param updateReqVO 修改信息
     */
    void updateProductSalesLine(@Valid MesWmProductSalesLineSaveReqVO updateReqVO);

    /**
     * 删除销售出库单行（级联删除明细）
     *
     * @param id 编号
     */
    void deleteProductSalesLine(Long id);

    /**
     * 获得销售出库单行
     *
     * @param id 编号
     * @return 销售出库单行
     */
    MesWmProductSalesLineDO getProductSalesLine(Long id);

    /**
     * 按出库单编号获得行列表
     *
     * @param salesId 出库单编号
     * @return 行列表
     */
    List<MesWmProductSalesLineDO> getProductSalesLineListBySalesId(Long salesId);

    /**
     * 按出库单编号批量删除行
     *
     * @param salesId 出库单编号
     */
    void deleteProductSalesLineBySalesId(Long salesId);

    /**
     * 获得销售出库单行分页
     *
     * @param pageReqVO 分页查询
     * @return 销售出库单行分页
     */
    cn.iocoder.yudao.framework.common.pojo.PageResult<MesWmProductSalesLineDO> getProductSalesLinePage(
            MesWmProductSalesLinePageReqVO pageReqVO);

}
