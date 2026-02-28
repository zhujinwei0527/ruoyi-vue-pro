package cn.iocoder.yudao.module.mes.service.wm.productproduce;

import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.detail.MesWmProductProduceDetailSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceDetailDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 生产入库明细 Service 接口
 */
public interface MesWmProductProduceDetailService {

    /**
     * 创建生产入库明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProductProduceDetail(@Valid MesWmProductProduceDetailSaveReqVO createReqVO);

    /**
     * 更新生产入库明细
     *
     * @param updateReqVO 更新信息
     */
    void updateProductProduceDetail(@Valid MesWmProductProduceDetailSaveReqVO updateReqVO);

    /**
     * 删除生产入库明细
     *
     * @param id 编号
     */
    void deleteProductProduceDetail(Long id);

    /**
     * 获得生产入库明细
     *
     * @param id 编号
     * @return 生产入库明细
     */
    MesWmProductProduceDetailDO getProductProduceDetail(Long id);

    /**
     * 根据行ID获取明细列表
     *
     * @param lineId 行ID
     * @return 明细列表
     */
    List<MesWmProductProduceDetailDO> getProductProduceDetailListByLineId(Long lineId);

    /**
     * 根据入库单ID获取明细列表
     *
     * @param produceId 入库单ID
     * @return 明细列表
     */
    List<MesWmProductProduceDetailDO> getProductProduceDetailListByProduceId(Long produceId);

    /**
     * 根据入库单ID删除明细
     *
     * @param produceId 入库单ID
     */
    void deleteProductProduceDetailByProduceId(Long produceId);

}
