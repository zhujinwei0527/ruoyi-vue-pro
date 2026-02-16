package cn.iocoder.yudao.module.mes.service.tm.tool;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.tm.tool.vo.MesTmToolPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.tm.tool.vo.MesTmToolSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.tm.tool.MesTmToolDO;
import cn.iocoder.yudao.module.mes.dal.mysql.tm.tool.MesTmToolMapper;
import cn.iocoder.yudao.module.mes.enums.tm.MesTmMaintenTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import cn.hutool.core.util.ObjUtil;

import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 工具台账 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesTmToolServiceImpl implements MesTmToolService {

    @Resource
    private MesTmToolMapper toolMapper;

    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private MesTmToolTypeService toolTypeService;

    @Override
    public Long createTool(MesTmToolSaveReqVO createReqVO) {
        // 校验工具类型存在
        toolTypeService.getToolType(createReqVO.getToolTypeId());
        // 校验编码唯一
        validateToolCodeUnique(null, createReqVO.getCode());

        // 插入
        MesTmToolDO tool = BeanUtils.toBean(createReqVO, MesTmToolDO.class);
        toolMapper.insert(tool);
        return tool.getId();
    }

    @Override
    public void updateTool(MesTmToolSaveReqVO updateReqVO) {
        // 校验存在
        validateToolExists(updateReqVO.getId());
        // 校验工具类型存在
        toolTypeService.getToolType(updateReqVO.getToolTypeId());
        // 校验编码唯一
        validateToolCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 更新
        if (Objects.equals(updateReqVO.getMaintenType(), MesTmMaintenTypeEnum.REGULAR.getType())) {
            updateReqVO.setNextMaintenPeriod(null);
        } else if (Objects.equals(updateReqVO.getMaintenType(), MesTmMaintenTypeEnum.USAGE.getType())) {
            updateReqVO.setNextMaintenDate(null);
        }
        MesTmToolDO updateObj = BeanUtils.toBean(updateReqVO, MesTmToolDO.class);
        toolMapper.updateById(updateObj);
    }

    @Override
    public void deleteTool(Long id) {
        // 校验存在
        validateToolExists(id);
        // 删除
        toolMapper.deleteById(id);
    }

    private void validateToolExists(Long id) {
        if (toolMapper.selectById(id) == null) {
            throw exception(TM_TOOL_NOT_EXISTS);
        }
    }

    private void validateToolCodeUnique(Long id, String code) {
        if (code == null) {
            return;
        }
        MesTmToolDO tool = toolMapper.selectByCode(code);
        if (tool == null) {
            return;
        }
        if (ObjUtil.notEqual(tool.getId(), id)) {
            throw exception(TM_TOOL_CODE_DUPLICATE);
        }
    }

    @Override
    public MesTmToolDO getTool(Long id) {
        return toolMapper.selectById(id);
    }

    @Override
    public PageResult<MesTmToolDO> getToolPage(MesTmToolPageReqVO pageReqVO) {
        return toolMapper.selectPage(pageReqVO);
    }

}
