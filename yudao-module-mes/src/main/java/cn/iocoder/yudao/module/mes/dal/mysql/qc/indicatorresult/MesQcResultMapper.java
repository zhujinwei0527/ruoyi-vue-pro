package cn.iocoder.yudao.module.mes.dal.mysql.qc.indicatorresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo.MesQcResultPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicatorresult.MesQcResultDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 检验结果记录 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesQcResultMapper extends BaseMapperX<MesQcResultDO> {

    default PageResult<MesQcResultDO> selectPage(MesQcResultPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesQcResultDO>()
                .eqIfPresent(MesQcResultDO::getQcId, reqVO.getQcId())
                .eqIfPresent(MesQcResultDO::getQcType, reqVO.getQcType())
                .likeIfPresent(MesQcResultDO::getCode, reqVO.getCode())
                .eqIfPresent(MesQcResultDO::getItemId, reqVO.getItemId())
                .orderByDesc(MesQcResultDO::getId));
    }

}
