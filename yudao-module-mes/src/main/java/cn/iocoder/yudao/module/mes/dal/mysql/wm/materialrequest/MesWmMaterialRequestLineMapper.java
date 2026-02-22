package cn.iocoder.yudao.module.mes.dal.mysql.wm.materialrequest;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.line.MesWmMaterialRequestLinePageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialrequest.MesWmMaterialRequestLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 领料申请单行 Mapper
 */
@Mapper
public interface MesWmMaterialRequestLineMapper extends BaseMapperX<MesWmMaterialRequestLineDO> {

    default PageResult<MesWmMaterialRequestLineDO> selectPage(MesWmMaterialRequestLinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmMaterialRequestLineDO>()
                .eqIfPresent(MesWmMaterialRequestLineDO::getMaterialRequestId, reqVO.getMaterialRequestId())
                .orderByDesc(MesWmMaterialRequestLineDO::getId));
    }

    default List<MesWmMaterialRequestLineDO> selectListByMaterialRequestId(Long materialRequestId) {
        return selectList(MesWmMaterialRequestLineDO::getMaterialRequestId, materialRequestId);
    }

    default void deleteByMaterialRequestId(Long materialRequestId) {
        delete(MesWmMaterialRequestLineDO::getMaterialRequestId, materialRequestId);
    }

}
