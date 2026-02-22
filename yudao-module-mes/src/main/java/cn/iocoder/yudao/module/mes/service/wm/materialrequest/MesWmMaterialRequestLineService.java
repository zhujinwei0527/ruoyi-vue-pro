package cn.iocoder.yudao.module.mes.service.wm.materialrequest;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.line.MesWmMaterialRequestLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.line.MesWmMaterialRequestLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialrequest.MesWmMaterialRequestLineDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 领料申请单行 Service 接口
 */
public interface MesWmMaterialRequestLineService {

    /**
     * 创建领料申请单行
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMaterialRequestLine(@Valid MesWmMaterialRequestLineSaveReqVO createReqVO);

    /**
     * 修改领料申请单行
     *
     * @param updateReqVO 修改信息
     */
    void updateMaterialRequestLine(@Valid MesWmMaterialRequestLineSaveReqVO updateReqVO);

    /**
     * 删除领料申请单行
     *
     * @param id 编号
     */
    void deleteMaterialRequestLine(Long id);

    /**
     * 获得领料申请单行
     *
     * @param id 编号
     * @return 领料申请单行
     */
    MesWmMaterialRequestLineDO getMaterialRequestLine(Long id);

    /**
     * 获得领料申请单行分页
     *
     * @param pageReqVO 分页参数
     * @return 领料申请单行分页
     */
    PageResult<MesWmMaterialRequestLineDO> getMaterialRequestLinePage(MesWmMaterialRequestLinePageReqVO pageReqVO);

    /**
     * 获得指定领料申请单的所有行
     *
     * @param materialRequestId 领料申请单编号
     * @return 领料申请单行列表
     */
    List<MesWmMaterialRequestLineDO> getMaterialRequestLineListByMaterialRequestId(Long materialRequestId);

    /**
     * 根据领料申请单编号，删除所有行
     *
     * @param materialRequestId 领料申请单编号
     */
    void deleteMaterialRequestLineByMaterialRequestId(Long materialRequestId);

}
