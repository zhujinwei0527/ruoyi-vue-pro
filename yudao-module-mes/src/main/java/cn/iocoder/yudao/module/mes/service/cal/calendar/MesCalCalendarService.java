package cn.iocoder.yudao.module.mes.service.cal.calendar;

import cn.iocoder.yudao.module.mes.controller.admin.cal.calendar.vo.MesCalCalendarListReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.calendar.vo.MesCalCalendarRespVO;

import java.util.List;

/**
 * MES 排班日历 Service 接口
 *
 * @author 芋道源码
 */
public interface MesCalCalendarService {

    /**
     * 查询排班日历列表
     *
     * @param reqVO 查询条件
     * @return 排班日历列表
     */
    List<MesCalCalendarRespVO> getCalendarList(MesCalCalendarListReqVO reqVO);

}
