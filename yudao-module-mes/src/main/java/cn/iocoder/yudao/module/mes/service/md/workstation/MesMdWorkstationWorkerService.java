package cn.iocoder.yudao.module.mes.service.md.workstation;

import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.worker.MesMdWorkstationWorkerSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationWorkerDO;
import jakarta.validation.Valid;

import java.util.List;

// TODO @AI：注释；
public interface MesMdWorkstationWorkerService {

    Long createWorkstationWorker(@Valid MesMdWorkstationWorkerSaveReqVO createReqVO);

    void updateWorkstationWorker(@Valid MesMdWorkstationWorkerSaveReqVO updateReqVO);

    void deleteWorkstationWorker(Long id);

    List<MesMdWorkstationWorkerDO> getWorkstationWorkerListByWorkstationId(Long workstationId);

}
