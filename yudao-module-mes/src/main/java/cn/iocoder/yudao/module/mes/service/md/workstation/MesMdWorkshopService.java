package cn.iocoder.yudao.module.mes.service.md.workstation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.workshop.MesMdWorkshopPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.workshop.MesMdWorkshopSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkshopDO;
import jakarta.validation.Valid;

import java.util.List;

// TODO @AI：方法注释？
/**
 * MES 车间 Service 接口
 *
 * @author 芋道源码
 */
public interface MesMdWorkshopService {

    Long createWorkshop(@Valid MesMdWorkshopSaveReqVO createReqVO);

    void updateWorkshop(@Valid MesMdWorkshopSaveReqVO updateReqVO);

    void deleteWorkshop(Long id);

    MesMdWorkshopDO getWorkshop(Long id);

    PageResult<MesMdWorkshopDO> getWorkshopPage(MesMdWorkshopPageReqVO pageReqVO);

    List<MesMdWorkshopDO> getWorkshopListByStatus(Integer status);

}
