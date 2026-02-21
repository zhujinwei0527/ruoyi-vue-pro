package cn.iocoder.yudao.module.mes.dal.mysql.pro.task;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.pro.task.vo.MesProTaskPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.task.MesProTaskDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 生产任务 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesProTaskMapper extends BaseMapperX<MesProTaskDO> {

    default PageResult<MesProTaskDO> selectPage(MesProTaskPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesProTaskDO>()
                .likeIfPresent(MesProTaskDO::getCode, reqVO.getCode())
                .likeIfPresent(MesProTaskDO::getName, reqVO.getName())
                .eqIfPresent(MesProTaskDO::getWorkOrderId, reqVO.getWorkOrderId())
                .eqIfPresent(MesProTaskDO::getProcessId, reqVO.getProcessId())
                .eqIfPresent(MesProTaskDO::getWorkstationId, reqVO.getWorkstationId())
                .eqIfPresent(MesProTaskDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MesProTaskDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MesProTaskDO::getId));
    }

    default List<MesProTaskDO> selectListByWorkOrderId(Long workOrderId) {
        return selectList(new LambdaQueryWrapperX<MesProTaskDO>()
                .eqIfPresent(MesProTaskDO::getWorkOrderId, workOrderId)
                .orderByDesc(MesProTaskDO::getId));
    }

}
