package cn.iocoder.yudao.module.mes.dal.mysql.qc;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.defect.MesQcIqcDefectPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.MesQcIqcDefectDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 来料检验缺陷记录 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesQcIqcDefectMapper extends BaseMapperX<MesQcIqcDefectDO> {

    default PageResult<MesQcIqcDefectDO> selectPage(MesQcIqcDefectPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesQcIqcDefectDO>()
                .eqIfPresent(MesQcIqcDefectDO::getIqcId, reqVO.getIqcId())
                .eqIfPresent(MesQcIqcDefectDO::getLineId, reqVO.getLineId())
                .orderByDesc(MesQcIqcDefectDO::getId));
    }

    default List<MesQcIqcDefectDO> selectListByIqcId(Long iqcId) {
        return selectList(MesQcIqcDefectDO::getIqcId, iqcId);
    }

    default List<MesQcIqcDefectDO> selectListByIqcIdAndLineId(Long iqcId, Long lineId) {
        return selectList(new LambdaQueryWrapperX<MesQcIqcDefectDO>()
                .eq(MesQcIqcDefectDO::getIqcId, iqcId)
                .eq(MesQcIqcDefectDO::getLineId, lineId));
    }

    default void deleteByIqcId(Long iqcId) {
        delete(new LambdaQueryWrapperX<MesQcIqcDefectDO>()
                .eq(MesQcIqcDefectDO::getIqcId, iqcId));
    }

}
