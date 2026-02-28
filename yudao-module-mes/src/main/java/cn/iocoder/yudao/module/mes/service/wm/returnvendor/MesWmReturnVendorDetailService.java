package cn.iocoder.yudao.module.mes.service.wm.returnvendor;

import cn.iocoder.yudao.module.mes.controller.admin.wm.returnvendor.vo.detail.MesWmReturnVendorDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnvendor.MesWmReturnVendorDetailDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 供应商退货明细 Service 接口
 */
public interface MesWmReturnVendorDetailService {

    /**
     * 创建供应商退货明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createReturnVendorDetail(@Valid MesWmReturnVendorDetailSaveReqVO createReqVO);

    /**
     * 更新供应商退货明细
     *
     * @param updateReqVO 更新信息
     */
    void updateReturnVendorDetail(@Valid MesWmReturnVendorDetailSaveReqVO updateReqVO);

    /**
     * 删除供应商退货明细
     *
     * @param id 编号
     */
    void deleteReturnVendorDetail(Long id);

    /**
     * 获得供应商退货明细
     *
     * @param id 编号
     * @return 供应商退货明细
     */
    MesWmReturnVendorDetailDO getReturnVendorDetail(Long id);

    /**
     * 根据行ID获取明细列表
     *
     * @param lineId 行ID
     * @return 明细列表
     */
    List<MesWmReturnVendorDetailDO> getReturnVendorDetailListByLineId(Long lineId);

    /**
     * 根据退货单ID获取明细列表
     *
     * @param returnVendorId 退货单ID
     * @return 明细列表
     */
    List<MesWmReturnVendorDetailDO> getReturnVendorDetailListByReturnVendorId(Long returnVendorId);

    /**
     * 根据退货单ID删除明细
     *
     * @param returnVendorId 退货单ID
     */
    void deleteReturnVendorDetailByReturnVendorId(Long returnVendorId);

}
