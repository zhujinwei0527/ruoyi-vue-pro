package cn.iocoder.yudao.module.mes.service.md.workstation;

import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationMachineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationMachineDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 工位设备 Service 接口
 *
 * @author 芋道源码
 */
public interface MesMdWorkstationMachineService {

// TODO @AI：方法注释？

    Long createWorkstationMachine(@Valid MesMdWorkstationMachineSaveReqVO createReqVO);

    void deleteWorkstationMachine(Long id);

    List<MesMdWorkstationMachineDO> getWorkstationMachineListByWorkstationId(Long workstationId);

}
