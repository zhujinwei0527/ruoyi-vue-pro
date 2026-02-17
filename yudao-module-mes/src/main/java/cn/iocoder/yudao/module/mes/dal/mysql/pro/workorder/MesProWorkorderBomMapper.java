package cn.iocoder.yudao.module.mes.dal.mysql.pro.workorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.bom.MesProWorkorderBomPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkorderBomDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 生产工单 BOM Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesProWorkorderBomMapper extends BaseMapperX<MesProWorkorderBomDO> {

    default PageResult<MesProWorkorderBomDO> selectPage(MesProWorkorderBomPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesProWorkorderBomDO>()
                .eqIfPresent(MesProWorkorderBomDO::getWorkorderId, reqVO.getWorkorderId())
                .orderByDesc(MesProWorkorderBomDO::getId));
    }

    default List<MesProWorkorderBomDO> selectListByWorkorderId(Long workorderId) {
        return selectList(MesProWorkorderBomDO::getWorkorderId, workorderId);
    }

    default void deleteByWorkorderId(Long workorderId) {
        delete(MesProWorkorderBomDO::getWorkorderId, workorderId);
    }

}
