package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.line.MesWmProductionIssueLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.line.MesWmProductionIssueLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueLineDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 领料出库单行 Service 接口
 */
public interface MesWmProductionIssueLineService {

    /**
     * 创建领料出库单行
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProductionIssueLine(@Valid MesWmProductionIssueLineSaveReqVO createReqVO);

    /**
     * 更新领料出库单行
     *
     * @param updateReqVO 更新信息
     */
    void updateProductionIssueLine(@Valid MesWmProductionIssueLineSaveReqVO updateReqVO);

    /**
     * 删除领料出库单行
     *
     * @param id 编号
     */
    void deleteProductionIssueLine(Long id);

    /**
     * 获得领料出库单行
     *
     * @param id 编号
     * @return 领料出库单行
     */
    MesWmProductionIssueLineDO getProductionIssueLine(Long id);

    /**
     * 获得领料出库单行分页
     *
     * @param pageReqVO 分页查询
     * @return 领料出库单行分页
     */
    PageResult<MesWmProductionIssueLineDO> getProductionIssueLinePage(MesWmProductionIssueLinePageReqVO pageReqVO);

    /**
     * 根据领料单ID获取行列表
     *
     * @param issueId 领料单ID
     * @return 行列表
     */
    List<MesWmProductionIssueLineDO> getProductionIssueLineListByIssueId(Long issueId);

    /**
     * 根据领料单ID删除行
     *
     * @param issueId 领料单ID
     */
    void deleteProductionIssueLineByIssueId(Long issueId);

}
