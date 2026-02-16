package cn.iocoder.yudao.module.mes.service.cal.holiday;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.cal.holiday.vo.MesCalHolidayPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.holiday.vo.MesCalHolidaySaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.holiday.MesCalHolidayDO;
import cn.iocoder.yudao.module.mes.dal.mysql.cal.holiday.MesCalHolidayMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.CAL_HOLIDAY_NOT_EXISTS;

/**
 * MES 假期设置 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesCalHolidayServiceImpl implements MesCalHolidayService {

    @Resource
    private MesCalHolidayMapper holidayMapper;

    @Override
    public Long createHoliday(MesCalHolidaySaveReqVO createReqVO) {
        // Upsert 逻辑：如果该日期已有记录，则更新
        MesCalHolidayDO existing = holidayMapper.selectByTheDay(createReqVO.getTheDay());
        if (existing != null) {
            MesCalHolidayDO updateObj = BeanUtils.toBean(createReqVO, MesCalHolidayDO.class);
            updateObj.setId(existing.getId());
            holidayMapper.updateById(updateObj);
            return existing.getId();
        }
        // 插入新记录
        MesCalHolidayDO holiday = BeanUtils.toBean(createReqVO, MesCalHolidayDO.class);
        holidayMapper.insert(holiday);
        return holiday.getId();
    }

    @Override
    public void updateHoliday(MesCalHolidaySaveReqVO updateReqVO) {
        // 校验存在
        validateHolidayExists(updateReqVO.getId());
        // 更新
        MesCalHolidayDO updateObj = BeanUtils.toBean(updateReqVO, MesCalHolidayDO.class);
        holidayMapper.updateById(updateObj);
    }

    @Override
    public void deleteHoliday(Long id) {
        // 校验存在
        validateHolidayExists(id);
        // 删除
        holidayMapper.deleteById(id);
    }

    private void validateHolidayExists(Long id) {
        if (holidayMapper.selectById(id) == null) {
            throw exception(CAL_HOLIDAY_NOT_EXISTS);
        }
    }

    @Override
    public MesCalHolidayDO getHoliday(Long id) {
        return holidayMapper.selectById(id);
    }

    @Override
    public PageResult<MesCalHolidayDO> getHolidayPage(MesCalHolidayPageReqVO pageReqVO) {
        return holidayMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesCalHolidayDO> getHolidayList() {
        return holidayMapper.selectList();
    }

}
