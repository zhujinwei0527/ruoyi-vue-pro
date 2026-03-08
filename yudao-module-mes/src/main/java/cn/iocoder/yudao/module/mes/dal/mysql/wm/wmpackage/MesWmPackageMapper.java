package cn.iocoder.yudao.module.mes.dal.mysql.wm.wmpackage;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.wmpackage.vo.MesWmPackagePageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.wmpackage.MesWmPackageDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MesWmPackageMapper extends BaseMapperX<MesWmPackageDO> {

    default PageResult<MesWmPackageDO> selectPage(MesWmPackagePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmPackageDO>()
                .likeIfPresent(MesWmPackageDO::getCode, reqVO.getCode())
                .likeIfPresent(MesWmPackageDO::getSoCode, reqVO.getSoCode())
                .eqIfPresent(MesWmPackageDO::getClientId, reqVO.getClientId())
                .betweenIfPresent(MesWmPackageDO::getPackageDate, reqVO.getPackageDate())
                .eqIfPresent(MesWmPackageDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesWmPackageDO::getId));
    }

    default MesWmPackageDO selectByCode(String code) {
        return selectOne(MesWmPackageDO::getCode, code);
    }

    default List<MesWmPackageDO> selectListByParentId(Long parentId) {
        return selectList(MesWmPackageDO::getParentId, parentId);
    }

}
