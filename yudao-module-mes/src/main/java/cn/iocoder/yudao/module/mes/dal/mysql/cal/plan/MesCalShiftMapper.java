package cn.iocoder.yudao.module.mes.dal.mysql.cal.plan;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.cal.plan.vo.shift.MesCalShiftPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.MesCalShiftDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 计划班次 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesCalShiftMapper extends BaseMapperX<MesCalShiftDO> {

    default PageResult<MesCalShiftDO> selectPage(MesCalShiftPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesCalShiftDO>()
                .eqIfPresent(MesCalShiftDO::getPlanId, reqVO.getPlanId())
                .likeIfPresent(MesCalShiftDO::getName, reqVO.getName())
                .orderByAsc(MesCalShiftDO::getSort));
    }

    default List<MesCalShiftDO> selectListByPlanId(Long planId) {
        return selectList(new LambdaQueryWrapperX<MesCalShiftDO>()
                .eq(MesCalShiftDO::getPlanId, planId)
                .orderByAsc(MesCalShiftDO::getSort));
    }

    default Long selectCountByPlanId(Long planId) {
        return selectCount(MesCalShiftDO::getPlanId, planId);
    }

    default void deleteByPlanId(Long planId) {
        delete(new LambdaQueryWrapperX<MesCalShiftDO>()
                .eq(MesCalShiftDO::getPlanId, planId));
    }

}
