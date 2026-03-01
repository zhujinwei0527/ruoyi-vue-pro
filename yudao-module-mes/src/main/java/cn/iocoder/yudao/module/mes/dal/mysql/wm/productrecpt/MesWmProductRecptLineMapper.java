package cn.iocoder.yudao.module.mes.dal.mysql.wm.productrecpt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.line.MesWmProductRecptLinePageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt.MesWmProductRecptLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 产品收货单行 Mapper
 */
@Mapper
public interface MesWmProductRecptLineMapper extends BaseMapperX<MesWmProductRecptLineDO> {

    default PageResult<MesWmProductRecptLineDO> selectPage(MesWmProductRecptLinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmProductRecptLineDO>()
                .eqIfPresent(MesWmProductRecptLineDO::getRecptId, reqVO.getRecptId())
                .orderByDesc(MesWmProductRecptLineDO::getId));
    }

    default List<MesWmProductRecptLineDO> selectListByRecptId(Long recptId) {
        return selectList(MesWmProductRecptLineDO::getRecptId, recptId);
    }

    default void deleteByRecptId(Long recptId) {
        delete(MesWmProductRecptLineDO::getRecptId, recptId);
    }

}
