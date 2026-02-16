package cn.iocoder.yudao.module.mes.service.md.workstation;

import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationMachineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationMachineDO;
import jakarta.validation.Valid;

import java.util.List;

// TODO @AI：注释；
public interface MesMdWorkstationMachineService {

    Long createWorkstationMachine(@Valid MesMdWorkstationMachineSaveReqVO createReqVO);

    void deleteWorkstationMachine(Long id);

    List<MesMdWorkstationMachineDO> getWorkstationMachineListByWorkstationId(Long workstationId);

}
