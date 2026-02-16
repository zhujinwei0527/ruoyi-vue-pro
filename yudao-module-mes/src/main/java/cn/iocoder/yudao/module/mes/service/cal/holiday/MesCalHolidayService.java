package cn.iocoder.yudao.module.mes.service.cal.holiday;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.cal.holiday.vo.MesCalHolidayPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.holiday.vo.MesCalHolidaySaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.holiday.MesCalHolidayDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 假期设置 Service 接口
 *
 * @author 芋道源码
 */
public interface MesCalHolidayService {

    /**
     * 创建假期设置（含 upsert 逻辑：如果该日期已存在记录，则更新）
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createHoliday(@Valid MesCalHolidaySaveReqVO createReqVO);

    /**
     * 更新假期设置
     *
     * @param updateReqVO 更新信息
     */
    void updateHoliday(@Valid MesCalHolidaySaveReqVO updateReqVO);

    /**
     * 删除假期设置
     *
     * @param id 编号
     */
    void deleteHoliday(Long id);

    /**
     * 获得假期设置
     *
     * @param id 编号
     * @return 假期设置
     */
    MesCalHolidayDO getHoliday(Long id);

    /**
     * 获得假期设置分页
     *
     * @param pageReqVO 分页查询
     * @return 假期设置分页
     */
    PageResult<MesCalHolidayDO> getHolidayPage(MesCalHolidayPageReqVO pageReqVO);

    /**
     * 获得所有假期设置列表（用于日历显示，不分页）
     *
     * @return 假期设置列表
     */
    List<MesCalHolidayDO> getHolidayList();

}
