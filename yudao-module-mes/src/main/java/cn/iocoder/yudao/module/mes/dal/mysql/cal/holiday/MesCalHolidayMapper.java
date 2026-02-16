package cn.iocoder.yudao.module.mes.dal.mysql.cal.holiday;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
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

    default MesCalHolidayDO selectByDay(LocalDateTime day) {
        return selectOne(MesCalHolidayDO::getDay, day);
    }

    default List<MesCalHolidayDO> selectList() {
        return selectList(new LambdaQueryWrapperX<MesCalHolidayDO>()
                .orderByAsc(MesCalHolidayDO::getDay));
    }

}
