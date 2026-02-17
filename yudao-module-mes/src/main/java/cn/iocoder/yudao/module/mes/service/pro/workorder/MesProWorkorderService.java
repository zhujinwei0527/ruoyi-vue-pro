package cn.iocoder.yudao.module.mes.service.pro.workorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.MesProWorkorderPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.MesProWorkorderSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkorderDO;
import jakarta.validation.Valid;

/**
 * MES 生产工单 Service 接口
 *
 * @author 芋道源码
 */
public interface MesProWorkorderService {

    /**
     * 创建生产工单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createWorkorder(@Valid MesProWorkorderSaveReqVO createReqVO);

    /**
     * 更新生产工单
     *
     * @param updateReqVO 更新信息
     */
    void updateWorkorder(@Valid MesProWorkorderSaveReqVO updateReqVO);

    /**
     * 删除生产工单
     *
     * @param id 编号
     */
    void deleteWorkorder(Long id);

    /**
     * 获得生产工单
     *
     * @param id 编号
     * @return 生产工单
     */
    MesProWorkorderDO getWorkorder(Long id);

    /**
     * 获得生产工单分页
     *
     * @param pageReqVO 分页查询
     * @return 生产工单分页
     */
    PageResult<MesProWorkorderDO> getWorkorderPage(MesProWorkorderPageReqVO pageReqVO);

    /**
     * 完成工单
     *
     * @param id 编号
     */
    void finishWorkorder(Long id);

    /**
     * 取消工单
     *
     * @param id 编号
     */
    void cancelWorkorder(Long id);

}
