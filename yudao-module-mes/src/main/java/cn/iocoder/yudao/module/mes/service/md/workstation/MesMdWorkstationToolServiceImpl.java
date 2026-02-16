package cn.iocoder.yudao.module.mes.service.md.workstation;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.tool.MesMdWorkstationToolSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationToolDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkstationToolMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 工位工具 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesMdWorkstationToolServiceImpl implements MesMdWorkstationToolService {

    @Resource
    private MesMdWorkstationToolMapper workstationToolMapper;

    @Override
    public Long createWorkstationTool(MesMdWorkstationToolSaveReqVO createReqVO) {
        // 校验同一工位下工具类型不重复
        MesMdWorkstationToolDO existing = workstationToolMapper.selectByWorkstationIdAndToolTypeId(
                createReqVO.getWorkstationId(), createReqVO.getToolTypeId());
        if (existing != null) {
            throw exception(MD_WORKSTATION_TOOL_TYPE_EXISTS);
        }

        // 插入
        MesMdWorkstationToolDO tool = BeanUtils.toBean(createReqVO, MesMdWorkstationToolDO.class);
        workstationToolMapper.insert(tool);
        return tool.getId();
    }

    @Override
    public void updateWorkstationTool(MesMdWorkstationToolSaveReqVO updateReqVO) {
        // 校验存在
        // TODO @AI：要不要抽个方法，参考别的模块
        if (workstationToolMapper.selectById(updateReqVO.getId()) == null) {
            throw exception(MD_WORKSTATION_TOOL_NOT_EXISTS);
        }

        // 更新
        MesMdWorkstationToolDO updateObj = BeanUtils.toBean(updateReqVO, MesMdWorkstationToolDO.class);
        workstationToolMapper.updateById(updateObj);
    }

    @Override
    public void deleteWorkstationTool(Long id) {
        // 校验存在
        if (workstationToolMapper.selectById(id) == null) {
            throw exception(MD_WORKSTATION_TOOL_NOT_EXISTS);
        }

        // 删除
        workstationToolMapper.deleteById(id);
    }

    @Override
    public List<MesMdWorkstationToolDO> getWorkstationToolListByWorkstationId(Long workstationId) {
        return workstationToolMapper.selectListByWorkstationId(workstationId);
    }

}
