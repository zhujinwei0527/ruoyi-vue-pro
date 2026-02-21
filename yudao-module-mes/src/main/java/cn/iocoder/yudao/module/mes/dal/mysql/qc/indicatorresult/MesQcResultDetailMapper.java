package cn.iocoder.yudao.module.mes.dal.mysql.qc.indicatorresult;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicatorresult.MesQcResultDetailDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 检验结果明细记录 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesQcResultDetailMapper extends BaseMapperX<MesQcResultDetailDO> {

    default List<MesQcResultDetailDO> selectListByResultId(Long resultId) {
        return selectList(MesQcResultDetailDO::getResultId, resultId);
    }

    default void deleteByResultId(Long resultId) {
        delete(new LambdaQueryWrapperX<MesQcResultDetailDO>()
                .eq(MesQcResultDetailDO::getResultId, resultId));
    }

}
