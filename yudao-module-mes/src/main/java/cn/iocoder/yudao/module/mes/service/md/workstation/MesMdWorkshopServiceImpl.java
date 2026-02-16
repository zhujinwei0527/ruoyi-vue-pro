package cn.iocoder.yudao.module.mes.service.md.workstation;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.workshop.MesMdWorkshopPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.workstation.vo.workshop.MesMdWorkshopSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkshopDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.workstation.MesMdWorkstationDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkshopMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkstationMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 车间 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesMdWorkshopServiceImpl implements MesMdWorkshopService {

    @Resource
    private MesMdWorkshopMapper workshopMapper;

    @Resource
    @Lazy
    private MesMdWorkstationMapper workstationMapper;

    @Override
    public Long createWorkshop(MesMdWorkshopSaveReqVO createReqVO) {
        // TODO @AI：注释
        validateWorkshopCodeUnique(null, createReqVO.getCode());
        // TODO @AI：注释
        validateWorkshopNameUnique(null, createReqVO.getName());

        // 插入
        MesMdWorkshopDO workshop = BeanUtils.toBean(createReqVO, MesMdWorkshopDO.class);
        workshopMapper.insert(workshop);
        return workshop.getId();
    }

    @Override
    public void updateWorkshop(MesMdWorkshopSaveReqVO updateReqVO) {
        // TODO @AI：注释
        validateWorkshopExists(updateReqVO.getId());
        // TODO @AI：注释
        validateWorkshopCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // TODO @AI：注释
        validateWorkshopNameUnique(updateReqVO.getId(), updateReqVO.getName());

        // 更新
        MesMdWorkshopDO updateObj = BeanUtils.toBean(updateReqVO, MesMdWorkshopDO.class);
        workshopMapper.updateById(updateObj);
    }

    @Override
    public void deleteWorkshop(Long id) {
        // TODO @AI：注释
        validateWorkshopExists(id);
        // 校验车间下是否存在工位
        Long count = workstationMapper.selectCount(MesMdWorkstationDO::getWorkshopId, id);
        if (count > 0) {
            throw exception(MD_WORKSHOP_HAS_WORKSTATION);
        }

        // 删除
        workshopMapper.deleteById(id);
    }

    private void validateWorkshopExists(Long id) {
        if (workshopMapper.selectById(id) == null) {
            throw exception(MD_WORKSHOP_NOT_EXISTS);
        }
    }

    private void validateWorkshopCodeUnique(Long id, String code) {
        MesMdWorkshopDO workshop = workshopMapper.selectByCode(code);
        if (workshop == null) {
            return;
        }
        if (ObjUtil.notEqual(workshop.getId(), id)) {
            throw exception(MD_WORKSHOP_CODE_DUPLICATE);
        }
    }

    private void validateWorkshopNameUnique(Long id, String name) {
        MesMdWorkshopDO workshop = workshopMapper.selectByName(name);
        if (workshop == null) {
            return;
        }
        if (ObjUtil.notEqual(workshop.getId(), id)) {
            throw exception(MD_WORKSHOP_NAME_DUPLICATE);
        }
    }

    @Override
    public MesMdWorkshopDO getWorkshop(Long id) {
        return workshopMapper.selectById(id);
    }

    @Override
    public PageResult<MesMdWorkshopDO> getWorkshopPage(MesMdWorkshopPageReqVO pageReqVO) {
        return workshopMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesMdWorkshopDO> getWorkshopListByStatus(Integer status) {
        return workshopMapper.selectListByStatus(status);
    }

}
