package cn.iocoder.yudao.module.mes.dal.mysql.wm.packages;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.packages.vo.line.MesWmPackageLinePageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.packages.MesWmPackageLineDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 装箱明细 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesWmPackageLineMapper extends BaseMapperX<MesWmPackageLineDO> {

    default PageResult<MesWmPackageLineDO> selectPage(MesWmPackageLinePageReqVO reqVO) {
        // TODO @芋艿：需确认“父箱详情 -> 装箱清单”是否需要汇总当前箱及其所有子孙箱的明细；
        // ktg-mes 当前通过 SQL + ancestors 实现聚合查询，这里现在仅按 packageId 精确查询。
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmPackageLineDO>()
                .eqIfPresent(MesWmPackageLineDO::getPackageId, reqVO.getPackageId())
                .orderByDesc(MesWmPackageLineDO::getId));
    }

    default void deleteByPackageId(Long packageId) {
        delete(MesWmPackageLineDO::getPackageId, packageId);
    }

}
