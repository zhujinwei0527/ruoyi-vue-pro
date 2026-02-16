package cn.iocoder.yudao.module.mes.dal.mysql.cal.holiday;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.cal.holiday.vo.MesCalHolidayPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.holiday.MesCalHolidayDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MES 假期设置 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesCalHolidayMapper extends BaseMapperX<MesCalHolidayDO> {

    default PageResult<MesCalHolidayDO> selectPage(MesCalHolidayPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesCalHolidayDO>()
                .eqIfPresent(MesCalHolidayDO::getType, reqVO.getType())
                .geIfPresent(MesCalHolidayDO::getTheDay, reqVO.getTheDayStart())
                .leIfPresent(MesCalHolidayDO::getTheDay, reqVO.getTheDayEnd())
                .orderByAsc(MesCalHolidayDO::getTheDay));
    }

    default MesCalHolidayDO selectByTheDay(LocalDateTime theDay) {
        return selectOne(MesCalHolidayDO::getTheDay, theDay);
    }

    default List<MesCalHolidayDO> selectList() {
        return selectList(new LambdaQueryWrapperX<MesCalHolidayDO>()
                .orderByAsc(MesCalHolidayDO::getTheDay));
    }

}
