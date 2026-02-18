package cn.iocoder.yudao.module.mes.service.cal.team;

import cn.iocoder.yudao.module.mes.controller.admin.cal.team.vo.shift.MesCalTeamShiftListReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.team.MesCalTeamShiftDO;

import java.util.List;

/**
 * MES 班组排班 Service 接口
 *
 * @author 芋道源码
 */
public interface MesCalTeamShiftService {

    // TODO @AI：方法名，优化下；
    /**
     * 根据排班计划生成班组排班记录
     *
     * @param planId 排班计划编号
     */
    void genRecords(Long planId);

    /**
     * 获得班组排班列表
     *
     * @param reqVO 查询条件
     * @return 班组排班列表
     */
    List<MesCalTeamShiftDO> getTeamShiftList(MesCalTeamShiftListReqVO reqVO);

    // TODO @AI：评审下，为什么不调用！
    /**
     * 根据排班计划编号删除所有排班记录
     *
     * @param planId 排班计划编号
     */
    void deleteByPlanId(Long planId);

}
