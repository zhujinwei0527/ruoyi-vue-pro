package cn.iocoder.yudao.module.mes.dal.mysql.md.autocode;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.md.autocode.vo.record.MesMdAutoCodeRecordPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.autocode.MesMdAutoCodeRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 编码生成记录 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesMdAutoCodeRecordMapper extends BaseMapperX<MesMdAutoCodeRecordDO> {

    default PageResult<MesMdAutoCodeRecordDO> selectPage(MesMdAutoCodeRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesMdAutoCodeRecordDO>()
                .eqIfPresent(MesMdAutoCodeRecordDO::getRuleId, reqVO.getRuleId())
                .likeIfPresent(MesMdAutoCodeRecordDO::getResult, reqVO.getResult())
                .betweenIfPresent(MesMdAutoCodeRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MesMdAutoCodeRecordDO::getId));
    }

    default MesMdAutoCodeRecordDO selectByResult(String result) {
        return selectOne(MesMdAutoCodeRecordDO::getResult, result);
    }

}
