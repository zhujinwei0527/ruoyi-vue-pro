package cn.iocoder.yudao.module.mes.service.md.workstation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 工位 Service 接口
 *
 * @author 芋道源码
 */
public interface MesMdWorkstationService {

    // TODO @AI：方法注释？

    Long createWorkstation(@Valid MesMdWorkstationSaveReqVO createReqVO);

    void updateWorkstation(@Valid MesMdWorkstationSaveReqVO updateReqVO);

    void deleteWorkstation(Long id);

    MesMdWorkstationDO getWorkstation(Long id);

    PageResult<MesMdWorkstationDO> getWorkstationPage(MesMdWorkstationPageReqVO pageReqVO);

    List<MesMdWorkstationDO> getWorkstationListByStatus(Integer status);

}
