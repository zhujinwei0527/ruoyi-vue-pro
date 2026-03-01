package cn.iocoder.yudao.module.mes.service.wm.productrecpt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.line.MesWmProductRecptLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.line.MesWmProductRecptLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt.MesWmProductRecptLineDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 产品收货单行 Service 接口
 */
public interface MesWmProductRecptLineService {

    /**
     * 创建产品收货单行
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProductRecptLine(@Valid MesWmProductRecptLineSaveReqVO createReqVO);

    /**
     * 修改产品收货单行
     *
     * @param updateReqVO 修改信息
     */
    void updateProductRecptLine(@Valid MesWmProductRecptLineSaveReqVO updateReqVO);

    /**
     * 删除产品收货单行（级联删除明细）
     *
     * @param id 编号
     */
    void deleteProductRecptLine(Long id);

    /**
     * 获得产品收货单行
     *
     * @param id 编号
     * @return 产品收货单行
     */
    MesWmProductRecptLineDO getProductRecptLine(Long id);

    /**
     * 获得产品收货单行分页
     *
     * @param pageReqVO 分页参数
     * @return 产品收货单行分页
     */
    PageResult<MesWmProductRecptLineDO> getProductRecptLinePage(MesWmProductRecptLinePageReqVO pageReqVO);

    /**
     * 按收货单编号获得行列表
     *
     * @param recptId 收货单编号
     * @return 行列表
     */
    List<MesWmProductRecptLineDO> getProductRecptLineListByRecptId(Long recptId);

    /**
     * 按收货单编号批量删除行
     *
     * @param recptId 收货单编号
     */
    void deleteProductRecptLineByRecptId(Long recptId);

}
