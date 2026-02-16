package cn.iocoder.yudao.module.mes.service.md.workstation;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationMachineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationMachineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkstationMachineMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

// TODO @AI：注释；
@Service
@Validated
public class MesMdWorkstationMachineServiceImpl implements MesMdWorkstationMachineService {

    @Resource
    private MesMdWorkstationMachineMapper workstationMachineMapper;

    @Override
    public Long createWorkstationMachine(MesMdWorkstationMachineSaveReqVO createReqVO) {
        // 校验该设备是否已分配到其他工位
        MesMdWorkstationMachineDO existing = workstationMachineMapper.selectByMachineryId(createReqVO.getMachineryId());
        if (existing != null) {
            throw exception(MD_WORKSTATION_MACHINE_EXISTS);
        }

        MesMdWorkstationMachineDO machine = BeanUtils.toBean(createReqVO, MesMdWorkstationMachineDO.class);
        workstationMachineMapper.insert(machine);
        return machine.getId();
    }

    @Override
    public void deleteWorkstationMachine(Long id) {
        if (workstationMachineMapper.selectById(id) == null) {
            throw exception(MD_WORKSTATION_MACHINE_NOT_EXISTS);
        }
        workstationMachineMapper.deleteById(id);
    }

    @Override
    public List<MesMdWorkstationMachineDO> getWorkstationMachineListByWorkstationId(Long workstationId) {
        return workstationMachineMapper.selectListByWorkstationId(workstationId);
    }

}
