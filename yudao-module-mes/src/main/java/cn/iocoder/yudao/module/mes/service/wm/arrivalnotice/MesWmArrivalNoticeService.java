package cn.iocoder.yudao.module.mes.service.wm.arrivalnotice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.arrivalnotice.vo.MesWmArrivalNoticePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.arrivalnotice.vo.MesWmArrivalNoticeSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.arrivalnotice.MesWmArrivalNoticeDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * MES 到货通知单 Service 接口
 */
public interface MesWmArrivalNoticeService {

    /**
     * 创建到货通知单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createArrivalNotice(@Valid MesWmArrivalNoticeSaveReqVO createReqVO);

    /**
     * 修改到货通知单
     *
     * @param updateReqVO 修改信息
     */
    void updateArrivalNotice(@Valid MesWmArrivalNoticeSaveReqVO updateReqVO);

    /**
     * 删除到货通知单（级联删除行）
     *
     * @param id 编号
     */
    void deleteArrivalNotice(Long id);

    /**
     * 获得到货通知单
     *
     * @param id 编号
     * @return 到货通知单
     */
    MesWmArrivalNoticeDO getArrivalNotice(Long id);

    /**
     * 获得到货通知单分页
     *
     * @param pageReqVO 分页参数
     * @return 到货通知单分页
     */
    PageResult<MesWmArrivalNoticeDO> getArrivalNoticePage(MesWmArrivalNoticePageReqVO pageReqVO);

    // TODO @AI：0-》1、1-》2 尽量写状态；不要写这种；
    /**
     * 提交到货通知单（0→1）
     *
     * @param id 编号
     */
    void submitArrivalNotice(Long id);

    /**
     * 审批到货通知单（1→2）
     *
     * @param id 编号
     */
    void approveArrivalNotice(Long id);

    /**
     * 完成到货通知单（→3），内部调用
     *
     * @param id 编号
     */
    void finishArrivalNotice(Long id);

    /**
     * 按编号集合获得到货通知单列表
     *
     * @param ids 编号集合
     * @return 到货通知单列表
     */
    List<MesWmArrivalNoticeDO> getArrivalNoticeList(Collection<Long> ids);

    default Map<Long, MesWmArrivalNoticeDO> getArrivalNoticeMap(Collection<Long> ids) {
        return convertMap(getArrivalNoticeList(ids), MesWmArrivalNoticeDO::getId);
    }

}
