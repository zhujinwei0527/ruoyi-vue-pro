package cn.iocoder.yudao.module.mes.service.pro.workorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.bom.MesProWorkorderBomPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.bom.MesProWorkorderBomSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkorderBomDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 生产工单 BOM Service 接口
 *
 * @author 芋道源码
 */
public interface MesProWorkorderBomService {

    /**
     * 创建工单 BOM
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createWorkorderBom(@Valid MesProWorkorderBomSaveReqVO createReqVO);

    /**
     * 更新工单 BOM
     *
     * @param updateReqVO 更新信息
     */
    void updateWorkorderBom(@Valid MesProWorkorderBomSaveReqVO updateReqVO);

    /**
     * 删除工单 BOM
     *
     * @param id 编号
     */
    void deleteWorkorderBom(Long id);

    /**
     * 获得工单 BOM
     *
     * @param id 编号
     * @return 工单 BOM
     */
    MesProWorkorderBomDO getWorkorderBom(Long id);

    /**
     * 获得工单 BOM 分页
     *
     * @param pageReqVO 分页查询
     * @return 工单 BOM 分页
     */
    PageResult<MesProWorkorderBomDO> getWorkorderBomPage(MesProWorkorderBomPageReqVO pageReqVO);

    /**
     * 根据工单编号获得 BOM 列表
     *
     * @param workorderId 工单编号
     * @return BOM 列表
     */
    List<MesProWorkorderBomDO> getWorkorderBomListByWorkorderId(Long workorderId);

    /**
     * 根据工单编号删除 BOM
     *
     * @param workorderId 工单编号
     */
    void deleteWorkorderBomByWorkorderId(Long workorderId);

}
