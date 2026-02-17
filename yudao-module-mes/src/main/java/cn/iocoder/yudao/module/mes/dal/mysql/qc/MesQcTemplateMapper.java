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
        // types: 取第一个值做 LIKE 匹配（简单实现，支持单值过滤）
        // TODO @AI：find in set；你看看 mybatisutils 有类似的；
        if (reqVO.getTypes() != null && !reqVO.getTypes().isEmpty()) {
            query.like(MesQcTemplateDO::getTypes, String.valueOf(reqVO.getTypes().get(0)));
        }
        // TODO @AI：是不是里面就是 boolean
        // enableFlag: 转为 Y/N 已经改成 boolean 了，数据库里也是 boolean
        if (reqVO.getEnableFlag() != null) {
            query.eq(MesQcTemplateDO::getEnableFlag, Boolean.TRUE.equals(reqVO.getEnableFlag()) ? "Y" : "N");
        }
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
