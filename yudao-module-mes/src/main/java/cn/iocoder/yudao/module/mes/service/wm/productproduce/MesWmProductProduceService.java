package cn.iocoder.yudao.module.mes.service.wm.productproduce;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.MesWmProductProducePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.MesWmProductProduceSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.feedback.MesProFeedbackDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceDO;
import jakarta.validation.Valid;

/**
 * MES 生产入库单 Service 接口
 */
public interface MesWmProductProduceService {

    /**
     * 创建生产入库单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProductProduce(@Valid MesWmProductProduceSaveReqVO createReqVO);

    /**
     * 修改生产入库单
     *
     * @param updateReqVO 修改信息
     */
    void updateProductProduce(@Valid MesWmProductProduceSaveReqVO updateReqVO);

    /**
     * 删除生产入库单
     *
     * @param id 编号
     */
    void deleteProductProduce(Long id);

    /**
     * 获得生产入库单
     *
     * @param id 编号
     * @return 生产入库单
     */
    MesWmProductProduceDO getProductProduce(Long id);

    /**
     * 获得生产入库单分页
     *
     * @param pageReqVO 分页参数
     * @return 生产入库单分页
     */
    PageResult<MesWmProductProduceDO> getProductProducePage(MesWmProductProducePageReqVO pageReqVO);

    /**
     * 校验生产入库单是否存在
     *
     * @param id 编号
     * @return 生产入库单
     */
    MesWmProductProduceDO validateProductProduceExists(Long id);

    /**
     * 完成生产入库单（草稿 → 已完成）
     *
     * @param id 编号
     */
    void finishProductProduce(Long id);

    /**
     * 取消生产入库单（非已完成/已取消状态 → 已取消）
     *
     * @param id 编号
     */
    void cancelProductProduce(Long id);

    /**
     * 校验生产入库单的数量：每行明细数量之和是否等于行入库数量
     *
     * @param id 编号
     * @return 是否全部一致
     */
    Boolean checkProductProduceQuantity(Long id);

    /**
     * 根据报工记录，自动生成产品产出单（头 + 行 + 明细）
     *
     * @param feedback  报工记录
     * @param checkFlag 是否需要检验（true=待检验，false=按合格/不合格拆分行）
     * @return 生成的产品产出单
     */
    MesWmProductProduceDO generateProductProduce(MesProFeedbackDO feedback, boolean checkFlag);

}
