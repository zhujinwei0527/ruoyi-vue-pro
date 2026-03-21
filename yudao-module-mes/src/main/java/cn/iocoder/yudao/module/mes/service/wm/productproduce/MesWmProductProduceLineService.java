package cn.iocoder.yudao.module.mes.service.wm.productproduce;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.line.MesWmProductProduceLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.line.MesWmProductProduceLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceLineDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 生产入库单行 Service 接口
 */
public interface MesWmProductProduceLineService {

    /**
     * 创建生产入库单行
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProductProduceLine(@Valid MesWmProductProduceLineSaveReqVO createReqVO);

    /**
     * 更新生产入库单行
     *
     * @param updateReqVO 更新信息
     */
    void updateProductProduceLine(@Valid MesWmProductProduceLineSaveReqVO updateReqVO);

    /**
     * 删除生产入库单行
     *
     * @param id 编号
     */
    void deleteProductProduceLine(Long id);

    /**
     * 获得生产入库单行
     *
     * @param id 编号
     * @return 生产入库单行
     */
    MesWmProductProduceLineDO getProductProduceLine(Long id);

    /**
     * 获得生产入库单行分页
     *
     * @param pageReqVO 分页查询
     * @return 生产入库单行分页
     */
    PageResult<MesWmProductProduceLineDO> getProductProduceLinePage(MesWmProductProduceLinePageReqVO pageReqVO);

    /**
     * 根据入库单ID获取行列表
     *
     * @param produceId 入库单ID
     * @return 行列表
     */
    List<MesWmProductProduceLineDO> getProductProduceLineListByProduceId(Long produceId);

    /**
     * 根据入库单ID删除行
     *
     * @param produceId 入库单ID
     */
    void deleteProductProduceLineByProduceId(Long produceId);

    /**
     * 校验生产入库单行是否存在
     *
     * @param id 编号
     * @return 生产入库单行
     */
    MesWmProductProduceLineDO validateProductProduceLineExists(Long id);

    /**
     * 根据报工记录ID获取行列表
     *
     * @param feedbackId 报工记录ID
     * @return 行列表
     */
    List<MesWmProductProduceLineDO> getProductProduceLineListByFeedbackId(Long feedbackId);

}
