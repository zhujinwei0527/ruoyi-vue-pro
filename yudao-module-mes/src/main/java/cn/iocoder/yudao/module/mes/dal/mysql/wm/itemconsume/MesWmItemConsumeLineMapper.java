package cn.iocoder.yudao.module.mes.dal.mysql.wm.itemconsume;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemconsume.MesWmItemConsumeLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 物料消耗记录行 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesWmItemConsumeLineMapper extends BaseMapperX<MesWmItemConsumeLineDO> {

    default List<MesWmItemConsumeLineDO> selectListByConsumeId(Long consumeId) {
        return selectList(MesWmItemConsumeLineDO::getConsumeId, consumeId);
    }

}
