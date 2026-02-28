package cn.iocoder.yudao.module.mes.dal.mysql.wm.productproduce;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.MesWmProductProducePageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 生产入库单 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesWmProductProduceMapper extends BaseMapperX<MesWmProductProduceDO> {

    default PageResult<MesWmProductProduceDO> selectPage(MesWmProductProducePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmProductProduceDO>()
                .eqIfPresent(MesWmProductProduceDO::getWorkOrderId, reqVO.getWorkOrderId())
                .eqIfPresent(MesWmProductProduceDO::getWorkstationId, reqVO.getWorkstationId())
                .eqIfPresent(MesWmProductProduceDO::getProcessId, reqVO.getProcessId())
                .eqIfPresent(MesWmProductProduceDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MesWmProductProduceDO::getProduceDate, reqVO.getProduceDate())
                .orderByDesc(MesWmProductProduceDO::getId));
    }

}
