package cn.iocoder.yudao.module.mes.service.wm.materialrequest;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.MesWmMaterialRequestPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.MesWmMaterialRequestSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialrequest.MesWmMaterialRequestDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * MES 领料申请单 Service 接口
 */
public interface MesWmMaterialRequestService {

    /**
     * 创建领料申请单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMaterialRequest(@Valid MesWmMaterialRequestSaveReqVO createReqVO);

    /**
     * 修改领料申请单
     *
     * @param updateReqVO 修改信息
     */
    void updateMaterialRequest(@Valid MesWmMaterialRequestSaveReqVO updateReqVO);

    /**
     * 删除领料申请单（级联删除行，仅草稿状态允许）
     *
     * @param id 编号
     */
    void deleteMaterialRequest(Long id);

    /**
     * 获得领料申请单
     *
     * @param id 编号
     * @return 领料申请单
     */
    MesWmMaterialRequestDO getMaterialRequest(Long id);

    /**
     * 获得领料申请单分页
     *
     * @param pageReqVO 分页参数
     * @return 领料申请单分页
     */
    PageResult<MesWmMaterialRequestDO> getMaterialRequestPage(MesWmMaterialRequestPageReqVO pageReqVO);

    /**
     * 提交领料申请单（草稿 → 备料中）
     *
     * @param id 编号
     */
    void submitMaterialRequest(Long id);

    /**
     * 审批领料申请单（备料中 → 待领料）
     *
     * @param id 编号
     */
    void approveMaterialRequest(Long id);

    /**
     * 完成领料申请单（待领料 → 已完成），内部调用
     *
     * @param id 编号
     */
    void finishMaterialRequest(Long id);

    /**
     * 取消领料申请单（非已完成 → 已取消）
     *
     * @param id 编号
     */
    void cancelMaterialRequest(Long id);

    /**
     * 按编号集合获得领料申请单列表
     *
     * @param ids 编号集合
     * @return 领料申请单列表
     */
    List<MesWmMaterialRequestDO> getMaterialRequestList(Collection<Long> ids);

    default Map<Long, MesWmMaterialRequestDO> getMaterialRequestMap(Collection<Long> ids) {
        return convertMap(getMaterialRequestList(ids), MesWmMaterialRequestDO::getId);
    }

}
