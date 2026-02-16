package cn.iocoder.yudao.module.mes.service.md.workstation;

import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.tool.MesMdWorkstationToolSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationToolDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 工位工具 Service 接口
 *
 * @author 芋道源码
 */
public interface MesMdWorkstationToolService {

    // TODO @AI：方法注释？
    Long createWorkstationTool(@Valid MesMdWorkstationToolSaveReqVO createReqVO);

    void updateWorkstationTool(@Valid MesMdWorkstationToolSaveReqVO updateReqVO);

    void deleteWorkstationTool(Long id);

    List<MesMdWorkstationToolDO> getWorkstationToolListByWorkstationId(Long workstationId);

}
