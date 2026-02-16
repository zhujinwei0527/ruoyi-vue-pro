package cn.iocoder.yudao.module.mes.dal.mysql.md.workstation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 工位 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesMdWorkstationMapper extends BaseMapperX<MesMdWorkstationDO> {

    default PageResult<MesMdWorkstationDO> selectPage(MesMdWorkstationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesMdWorkstationDO>()
                .eqIfPresent(MesMdWorkstationDO::getCode, reqVO.getCode())
                .likeIfPresent(MesMdWorkstationDO::getName, reqVO.getName())
                .eqIfPresent(MesMdWorkstationDO::getWorkshopId, reqVO.getWorkshopId())
                .eqIfPresent(MesMdWorkstationDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesMdWorkstationDO::getId));
    }

    default MesMdWorkstationDO selectByCode(String code) {
        return selectOne(MesMdWorkstationDO::getCode, code);
    }

    default MesMdWorkstationDO selectByName(String name) {
        return selectOne(MesMdWorkstationDO::getName, name);
    }

    default List<MesMdWorkstationDO> selectListByStatus(Integer status) {
        return selectList(MesMdWorkstationDO::getStatus, status);
    }

}
