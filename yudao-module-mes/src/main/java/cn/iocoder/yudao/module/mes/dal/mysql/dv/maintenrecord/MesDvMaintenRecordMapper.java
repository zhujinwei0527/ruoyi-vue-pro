package cn.iocoder.yudao.module.mes.dal.mysql.dv.maintenrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.dv.maintenrecord.vo.MesDvMaintenRecordPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.maintenrecord.MesDvMaintenRecordDO;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 设备保养记录 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesDvMaintenRecordMapper extends BaseMapperX<MesDvMaintenRecordDO> {

    default PageResult<MesDvMaintenRecordDO> selectPage(MesDvMaintenRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesDvMaintenRecordDO>()
//                .apply(StrUtil.isNotBlank(reqVO.getPlanName()),
//                        "plan_id IN (SELECT id FROM mes_dv_check_plan WHERE name LIKE CONCAT('%', {0}, '%') AND deleted = 0)",
//                        reqVO.getPlanName())
//                .apply(StrUtil.isNotBlank(reqVO.getMachineryCode()),
//                        "machinery_id IN (SELECT id FROM mes_dv_machinery WHERE code LIKE CONCAT('%', {0}, '%') AND deleted = 0)",
//                        reqVO.getMachineryCode())
//                .apply(StrUtil.isNotBlank(reqVO.getMachineryName()),
//                        "machinery_id IN (SELECT id FROM mes_dv_machinery WHERE name LIKE CONCAT('%', {0}, '%') AND deleted = 0)",
//                        reqVO.getMachineryName())
//                .apply(StrUtil.isNotBlank(reqVO.getNickname()),
//                        "user_id IN (SELECT id FROM system_users WHERE nickname LIKE CONCAT('%', {0}, '%') AND deleted = 0)",
//                        reqVO.getNickname())
                .betweenIfPresent(MesDvMaintenRecordDO::getMaintenTime, reqVO.getMaintenTime())
                .orderByDesc(MesDvMaintenRecordDO::getId));
    }

}
