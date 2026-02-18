package cn.iocoder.yudao.module.mes.dal.mysql.qc;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.qc.template.vo.MesQcTemplatePageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.MesQcTemplateDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 质检方案 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesQcTemplateMapper extends BaseMapperX<MesQcTemplateDO> {

    default PageResult<MesQcTemplateDO> selectPage(MesQcTemplatePageReqVO reqVO) {
        LambdaQueryWrapperX<MesQcTemplateDO> query = new LambdaQueryWrapperX<MesQcTemplateDO>()
                .likeIfPresent(MesQcTemplateDO::getCode, reqVO.getCode())
                .likeIfPresent(MesQcTemplateDO::getName, reqVO.getName())
                .orderByDesc(MesQcTemplateDO::getId);
        // types: 用 FIND_IN_SET 精确匹配逗号分隔字段（取第一个值过滤）
        // TODO @AI：MyBatisUtils 的 findInSet
        if (reqVO.getTypes() != null && !reqVO.getTypes().isEmpty()) {
            query.apply("FIND_IN_SET({0}, types) > 0", reqVO.getTypes().get(0));
        }
        query.eqIfPresent(MesQcTemplateDO::getEnableFlag, reqVO.getEnableFlag());
        return selectPage(reqVO, query);
    }

    default MesQcTemplateDO selectByCode(String code) {
        return selectOne(MesQcTemplateDO::getCode, code);
    }

    default List<MesQcTemplateDO> selectList() {
        return selectList(new LambdaQueryWrapperX<MesQcTemplateDO>()
                .orderByDesc(MesQcTemplateDO::getId));
    }

}
