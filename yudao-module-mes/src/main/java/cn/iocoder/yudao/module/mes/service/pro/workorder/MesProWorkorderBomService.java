package cn.iocoder.yudao.module.mes.service.pro.workorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.bom.MesProWorkOrderBomPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.bom.MesProWorkOrderBomSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderBomDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 生产工单 BOM Service 接口
 *
 * @author 芋道源码
 */
public interface MesProWorkOrderBomService {

    /**
     * 创建工单 BOM
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createWorkOrderBom(@Valid MesProWorkOrderBomSaveReqVO createReqVO);

    /**
     * 更新工单 BOM
     *
     * @param updateReqVO 更新信息
     */
    void updateWorkOrderBom(@Valid MesProWorkOrderBomSaveReqVO updateReqVO);

    /**
     * 删除工单 BOM
     *
     * @param id 编号
     */
    void deleteWorkOrderBom(Long id);

    /**
     * 获得工单 BOM
     *
     * @param id 编号
     * @return 工单 BOM
     */
    MesProWorkOrderBomDO getWorkOrderBom(Long id);

    /**
     * 获得工单 BOM 分页
     *
     * @param pageReqVO 分页查询
     * @return 工单 BOM 分页
     */
    PageResult<MesProWorkOrderBomDO> getWorkOrderBomPage(MesProWorkOrderBomPageReqVO pageReqVO);

    /**
     * 根据工单编号获得 BOM 列表
     *
     * @param workOrderId 工单编号
     * @return BOM 列表
     */
    List<MesProWorkOrderBomDO> getWorkOrderBomListByWorkOrderId(Long workOrderId);

    /**
     * 根据工单编号删除 BOM
     *
     * @param workOrderId 工单编号
     */
    void deleteWorkOrderBomByWorkOrderId(Long workOrderId);

}
