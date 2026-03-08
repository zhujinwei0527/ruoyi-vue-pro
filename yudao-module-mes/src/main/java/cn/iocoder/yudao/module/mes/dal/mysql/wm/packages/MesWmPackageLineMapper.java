package cn.iocoder.yudao.module.mes.dal.mysql.wm.packages;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.packages.vo.line.MesWmPackageLinePageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.packages.MesWmPackageLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MesWmPackageLineMapper extends BaseMapperX<MesWmPackageLineDO> {

    default PageResult<MesWmPackageLineDO> selectPage(MesWmPackageLinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmPackageLineDO>()
                .eqIfPresent(MesWmPackageLineDO::getPackageId, reqVO.getPackageId())
                .orderByDesc(MesWmPackageLineDO::getId));
    }

    default List<MesWmPackageLineDO> selectListByPackageId(Long packageId) {
        return selectList(MesWmPackageLineDO::getPackageId, packageId);
    }

    default void deleteByPackageId(Long packageId) {
        delete(MesWmPackageLineDO::getPackageId, packageId);
    }

}
