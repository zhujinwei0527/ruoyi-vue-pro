package cn.iocoder.yudao.module.mes.dal.mysql.md.vendor;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.md.vendor.vo.MesMdVendorPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.vendor.MesMdVendorDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 供应商 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesMdVendorMapper extends BaseMapperX<MesMdVendorDO> {

    default PageResult<MesMdVendorDO> selectPage(MesMdVendorPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesMdVendorDO>()
                .eqIfPresent(MesMdVendorDO::getCode, reqVO.getCode())
                .likeIfPresent(MesMdVendorDO::getName, reqVO.getName())
                .likeIfPresent(MesMdVendorDO::getNickname, reqVO.getNickname())
                .eqIfPresent(MesMdVendorDO::getLevel, reqVO.getLevel())
                .eqIfPresent(MesMdVendorDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesMdVendorDO::getId));
    }

    default MesMdVendorDO selectByCode(String code) {
        return selectOne(MesMdVendorDO::getCode, code);
    }

    default MesMdVendorDO selectByName(String name) {
        return selectOne(MesMdVendorDO::getName, name);
    }

    default MesMdVendorDO selectByNickname(String nickname) {
        return selectOne(MesMdVendorDO::getNickname, nickname);
    }

    default List<MesMdVendorDO> selectListByStatus(Integer status) {
        return selectList(MesMdVendorDO::getStatus, status);
    }

}
