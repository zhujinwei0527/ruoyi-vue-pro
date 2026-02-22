package cn.iocoder.yudao.module.mes.dal.mysql.wm.materialrequest;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.MesWmMaterialRequestPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialrequest.MesWmMaterialRequestDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 领料申请单 Mapper
 */
@Mapper
public interface MesWmMaterialRequestMapper extends BaseMapperX<MesWmMaterialRequestDO> {

    default PageResult<MesWmMaterialRequestDO> selectPage(MesWmMaterialRequestPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmMaterialRequestDO>()
                .eqIfPresent(MesWmMaterialRequestDO::getWorkstationId, reqVO.getWorkstationId())
                .eqIfPresent(MesWmMaterialRequestDO::getWorkOrderId, reqVO.getWorkOrderId())
                .eqIfPresent(MesWmMaterialRequestDO::getUserId, reqVO.getUserId())
                .eqIfPresent(MesWmMaterialRequestDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MesWmMaterialRequestDO::getRequestTime, reqVO.getRequestTime())
                .orderByDesc(MesWmMaterialRequestDO::getId));
    }

}
