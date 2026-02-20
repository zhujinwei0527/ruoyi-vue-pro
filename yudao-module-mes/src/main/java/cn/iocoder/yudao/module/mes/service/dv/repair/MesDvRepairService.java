package cn.iocoder.yudao.module.mes.service.dv.repair;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.dv.repair.vo.MesDvRepairPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.dv.repair.vo.MesDvRepairSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.repair.MesDvRepairDO;

import jakarta.validation.Valid;

/**
 * MES 维修工单 Service 接口
 *
 * @author 芋道源码
 */
public interface MesDvRepairService {

    /**
     * 创建维修工单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createRepair(@Valid MesDvRepairSaveReqVO createReqVO);

    /**
     * 更新维修工单
     *
     * @param updateReqVO 更新信息
     */
    void updateRepair(@Valid MesDvRepairSaveReqVO updateReqVO);

    /**
     * 删除维修工单
     *
     * @param id 编号
     */
    void deleteRepair(Long id);

    /**
     * 校验维修工单是否存在
     *
     * @param id 编号
     */
    void validateRepairExists(Long id);

    /**
     * 获得维修工单
     *
     * @param id 编号
     * @return 维修工单
     */
    MesDvRepairDO getRepair(Long id);

    /**
     * 获得维修工单分页
     *
     * @param pageReqVO 分页查询
     * @return 维修工单分页
     */
    PageResult<MesDvRepairDO> getRepairPage(MesDvRepairPageReqVO pageReqVO);

    /**
     * 通过维修工单（草稿→已确认，结果=通过）
     *
     * @param id 编号
     */
    void confirmRepair(Long id);

    /**
     * 不通过维修工单（草稿→已确认，结果=不通过）
     *
     * @param id 编号
     */
    void rejectRepair(Long id);

}
