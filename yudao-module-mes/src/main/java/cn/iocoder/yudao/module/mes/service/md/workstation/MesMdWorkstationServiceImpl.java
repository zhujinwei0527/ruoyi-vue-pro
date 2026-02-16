package cn.iocoder.yudao.module.mes.service.md.workstation;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.MesMdWorkstationSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkstationMachineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkstationMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkstationToolMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkstationWorkerMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 工位 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesMdWorkstationServiceImpl implements MesMdWorkstationService {

    @Resource
    private MesMdWorkstationMapper workstationMapper;

    @Resource
    private MesMdWorkstationMachineMapper workstationMachineMapper;

    @Resource
    private MesMdWorkstationToolMapper workstationToolMapper;

    @Resource
    private MesMdWorkstationWorkerMapper workstationWorkerMapper;

    @Resource
    private MesMdWorkshopService workshopService;

    @Override
    public Long createWorkstation(MesMdWorkstationSaveReqVO createReqVO) {
        // TODO @AI：注释
        validateWorkstationCodeUnique(null, createReqVO.getCode());
        // TODO @AI：注释
        validateWorkstationNameUnique(null, createReqVO.getName());
        // 校验车间存在
        workshopService.getWorkshop(createReqVO.getWorkshopId()); // validateWorkshopExists via getWorkshop

        // 插入
        MesMdWorkstationDO workstation = BeanUtils.toBean(createReqVO, MesMdWorkstationDO.class);
        workstationMapper.insert(workstation);
        return workstation.getId();
    }

    @Override
    public void updateWorkstation(MesMdWorkstationSaveReqVO updateReqVO) {
        // TODO @AI：注释
        validateWorkstationExists(updateReqVO.getId());
        // TODO @AI：注释
        validateWorkstationCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // TODO @AI：注释
        validateWorkstationNameUnique(updateReqVO.getId(), updateReqVO.getName());
        // 校验车间存在
        workshopService.getWorkshop(updateReqVO.getWorkshopId());

        // 更新
        MesMdWorkstationDO updateObj = BeanUtils.toBean(updateReqVO, MesMdWorkstationDO.class);
        workstationMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkstation(Long id) {
        // TODO @AI：注释
        validateWorkstationExists(id);

        // 级联删除子资源
        workstationMachineMapper.deleteByWorkstationId(id);
        workstationToolMapper.deleteByWorkstationId(id);
        workstationWorkerMapper.deleteByWorkstationId(id);
        // 删除工位
        workstationMapper.deleteById(id);
    }

    private void validateWorkstationExists(Long id) {
        if (workstationMapper.selectById(id) == null) {
            throw exception(MD_WORKSTATION_NOT_EXISTS);
        }
    }

    private void validateWorkstationCodeUnique(Long id, String code) {
        MesMdWorkstationDO workstation = workstationMapper.selectByCode(code);
        if (workstation == null) {
            return;
        }
        if (ObjUtil.notEqual(workstation.getId(), id)) {
            throw exception(MD_WORKSTATION_CODE_DUPLICATE);
        }
    }

    private void validateWorkstationNameUnique(Long id, String name) {
        MesMdWorkstationDO workstation = workstationMapper.selectByName(name);
        if (workstation == null) {
            return;
        }
        if (ObjUtil.notEqual(workstation.getId(), id)) {
            throw exception(MD_WORKSTATION_NAME_DUPLICATE);
        }
    }

    @Override
    public MesMdWorkstationDO getWorkstation(Long id) {
        return workstationMapper.selectById(id);
    }

    @Override
    public PageResult<MesMdWorkstationDO> getWorkstationPage(MesMdWorkstationPageReqVO pageReqVO) {
        return workstationMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesMdWorkstationDO> getWorkstationListByStatus(Integer status) {
        return workstationMapper.selectListByStatus(status);
    }

}
