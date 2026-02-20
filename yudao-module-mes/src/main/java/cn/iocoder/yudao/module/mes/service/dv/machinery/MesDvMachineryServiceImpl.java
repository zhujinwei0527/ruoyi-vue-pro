package cn.iocoder.yudao.module.mes.service.dv.machinery;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.dv.machinery.vo.MesDvMachineryPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.dv.machinery.vo.MesDvMachinerySaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.machinery.MesDvMachineryDO;
import cn.iocoder.yudao.module.mes.dal.mysql.dv.machinery.MesDvMachineryMapper;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkshopService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 设备台账 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesDvMachineryServiceImpl implements MesDvMachineryService {

    @Resource
    private MesDvMachineryMapper machineryMapper;

    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private MesDvMachineryTypeService machineryTypeService;

    @Resource
    @Lazy
    private MesMdWorkshopService workshopService;

    @Override
    public Long createMachinery(MesDvMachinerySaveReqVO createReqVO) {
        // 校验设备类型存在
        machineryTypeService.getMachineryType(createReqVO.getMachineryTypeId());
        // 校验车间存在
        workshopService.getWorkshop(createReqVO.getWorkshopId());
        // 校验编码唯一
        validateMachineryCodeUnique(null, createReqVO.getCode());

        // 插入
        MesDvMachineryDO machinery = BeanUtils.toBean(createReqVO, MesDvMachineryDO.class);
        machineryMapper.insert(machinery);
        return machinery.getId();
    }

    @Override
    public void updateMachinery(MesDvMachinerySaveReqVO updateReqVO) {
        // 校验存在
        validateMachineryExists(updateReqVO.getId());
        // 校验设备类型存在
        machineryTypeService.getMachineryType(updateReqVO.getMachineryTypeId());
        // 校验车间存在
        workshopService.getWorkshop(updateReqVO.getWorkshopId());
        // 校验编码唯一
        validateMachineryCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 更新
        MesDvMachineryDO updateObj = BeanUtils.toBean(updateReqVO, MesDvMachineryDO.class);
        machineryMapper.updateById(updateObj);
    }

    @Override
    public void deleteMachinery(Long id) {
        // 校验存在
        validateMachineryExists(id);
        // TODO @芋艿：后续补充点检计划、保养记录、维修工单等引用校验

        // 删除
        machineryMapper.deleteById(id);
    }

    @Override
    public void validateMachineryExists(Long id) {
        if (machineryMapper.selectById(id) == null) {
            throw exception(DV_MACHINERY_NOT_EXISTS);
        }
    }

    private void validateMachineryCodeUnique(Long id, String code) {
        if (code == null) {
            return;
        }
        MesDvMachineryDO machinery = machineryMapper.selectByCode(code);
        if (machinery == null) {
            return;
        }
        if (ObjUtil.notEqual(machinery.getId(), id)) {
            throw exception(DV_MACHINERY_CODE_DUPLICATE);
        }
    }

    @Override
    public MesDvMachineryDO getMachinery(Long id) {
        return machineryMapper.selectById(id);
    }

    @Override
    public PageResult<MesDvMachineryDO> getMachineryPage(MesDvMachineryPageReqVO pageReqVO) {
        return machineryMapper.selectPage(pageReqVO);
    }

    @Override
    public Long getMachineryCountByMachineryTypeId(Long machineryTypeId) {
        return machineryMapper.selectCountByMachineryTypeId(machineryTypeId);
    }

    @Override
    public List<MesDvMachineryDO> getMachinerySimpleList() {
        return machineryMapper.selectList();
    }

    @Override
    public List<MesDvMachineryDO> getMachineryList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return machineryMapper.selectByIds(ids);
    }

}
