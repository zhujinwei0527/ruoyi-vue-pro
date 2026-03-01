package cn.iocoder.yudao.module.mes.service.wm.productrecpt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.MesWmProductRecptPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.MesWmProductRecptSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt.MesWmProductRecptDO;
import jakarta.validation.Valid;

/**
 * MES 产品收货单 Service 接口
 */
public interface MesWmProductRecptService {

    /**
     * 创建产品收货单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProductRecpt(@Valid MesWmProductRecptSaveReqVO createReqVO);

    /**
     * 修改产品收货单
     *
     * @param updateReqVO 修改信息
     */
    void updateProductRecpt(@Valid MesWmProductRecptSaveReqVO updateReqVO);

    /**
     * 删除产品收货单（级联删除行+明细）
     *
     * @param id 编号
     */
    void deleteProductRecpt(Long id);

    /**
     * 获得产品收货单
     *
     * @param id 编号
     * @return 产品收货单
     */
    MesWmProductRecptDO getProductRecpt(Long id);

    /**
     * 获得产品收货单分页
     *
     * @param pageReqVO 分页参数
     * @return 产品收货单分页
     */
    PageResult<MesWmProductRecptDO> getProductRecptPage(MesWmProductRecptPageReqVO pageReqVO);

    /**
     * 提交产品收货单（草稿 → 待上架）
     *
     * @param id 编号
     */
    void submitProductRecpt(Long id);

    /**
     * 执行上架（待上架 → 待入库）
     *
     * @param id 编号
     */
    void stockProductRecpt(Long id);

    /**
     * 执行入库（待入库 → 已完成），更新库存台账
     *
     * @param id 编号
     */
    void executeProductRecpt(Long id);

    /**
     * 取消产品收货单（任意非已完成/已取消状态 → 已取消）
     *
     * @param id 编号
     */
    void cancelProductRecpt(Long id);

    /**
     * 校验产品收货单存在且处于可编辑状态（草稿或待上架）
     *
     * @param id 编号
     * @return 产品收货单
     */
    MesWmProductRecptDO validateProductRecptEditable(Long id);

}
