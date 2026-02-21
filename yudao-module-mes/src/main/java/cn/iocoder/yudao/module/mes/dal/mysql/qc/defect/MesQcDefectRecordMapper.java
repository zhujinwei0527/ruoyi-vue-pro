package cn.iocoder.yudao.module.mes.dal.mysql.qc.defect;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.qc.defect.vo.MesQcDefectRecordPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.defect.MesQcDefectRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 质检缺陷记录 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesQcDefectRecordMapper extends BaseMapperX<MesQcDefectRecordDO> {

    default PageResult<MesQcDefectRecordDO> selectPage(MesQcDefectRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesQcDefectRecordDO>()
                .eqIfPresent(MesQcDefectRecordDO::getQcType, reqVO.getQcType())
                .eqIfPresent(MesQcDefectRecordDO::getQcId, reqVO.getQcId())
                .eqIfPresent(MesQcDefectRecordDO::getLineId, reqVO.getLineId())
                .orderByDesc(MesQcDefectRecordDO::getId));
    }

    default List<MesQcDefectRecordDO> selectListByQcTypeAndQcId(Integer qcType, Long qcId) {
        return selectList(new LambdaQueryWrapperX<MesQcDefectRecordDO>()
                .eq(MesQcDefectRecordDO::getQcType, qcType)
                .eq(MesQcDefectRecordDO::getQcId, qcId));
    }

    default List<MesQcDefectRecordDO> selectListByQcTypeAndQcIdAndLineId(Integer qcType, Long qcId, Long lineId) {
        return selectList(new LambdaQueryWrapperX<MesQcDefectRecordDO>()
                .eq(MesQcDefectRecordDO::getQcType, qcType)
                .eq(MesQcDefectRecordDO::getQcId, qcId)
                .eq(MesQcDefectRecordDO::getLineId, lineId));
    }

    default void deleteByQcTypeAndQcId(Integer qcType, Long qcId) {
        delete(new LambdaQueryWrapperX<MesQcDefectRecordDO>()
                .eq(MesQcDefectRecordDO::getQcType, qcType)
                .eq(MesQcDefectRecordDO::getQcId, qcId));
    }

}
