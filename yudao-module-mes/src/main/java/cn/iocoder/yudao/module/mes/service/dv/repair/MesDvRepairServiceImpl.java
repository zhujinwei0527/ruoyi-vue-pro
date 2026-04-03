package cn.iocoder.yudao.module.mes.service.dv.repair;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.dv.repair.vo.MesDvRepairPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.dv.repair.vo.MesDvRepairSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.repair.MesDvRepairDO;
import cn.iocoder.yudao.module.mes.dal.mysql.dv.repair.MesDvRepairMapper;
import cn.iocoder.yudao.module.mes.enums.dv.MesDvRepairResultEnum;
import cn.iocoder.yudao.module.mes.enums.dv.MesDvRepairStatusEnum;
import cn.iocoder.yudao.module.mes.service.dv.machinery.MesDvMachineryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 维修工单 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesDvRepairServiceImpl implements MesDvRepairService {

    @Resource
    private MesDvRepairMapper repairMapper;

    @Resource
    private MesDvRepairLineService repairLineService;
    @Resource
    private MesDvMachineryService machineryService;

    @Override
    public Long createRepair(MesDvRepairSaveReqVO createReqVO) {
        // TODO @AI：抽成 validateXXSaveData（vo）；
        // 1.1 校验关联数据
        validateRepairRelation(createReqVO);
        // 1.2 校验编码唯一
        validateCodeUnique(null, createReqVO.getCode());

        // 2. 插入
        MesDvRepairDO repair = BeanUtils.toBean(createReqVO, MesDvRepairDO.class);
        repair.setStatus(MesDvRepairStatusEnum.DRAFT.getStatus());
        repairMapper.insert(repair);
        return repair.getId();
    }

    @Override
    public void updateRepair(MesDvRepairSaveReqVO updateReqVO) {
        // 1.1 校验存在，且状态为草稿
        validateRepairDraft(updateReqVO.getId());
        // TODO @AI：抽成 validateXXSaveData（vo）；
        // 1.2 校验关联数据
        validateRepairRelation(updateReqVO);
        // 1.3 校验编码唯一
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 2. 更新
        MesDvRepairDO updateObj = BeanUtils.toBean(updateReqVO, MesDvRepairDO.class);
        repairMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRepair(Long id) {
        // 校验存在，且状态为草稿
        validateRepairDraft(id);

        // 删除
        repairMapper.deleteById(id);
        // 级联删除子表
        repairLineService.deleteByRepairId(id);
    }

    private void validateRepairRelation(MesDvRepairSaveReqVO reqVO) {
        // 校验设备是否存在
        machineryService.validateMachineryExists(reqVO.getMachineryId());
    }

    private void validateCodeUnique(Long id, String code) {
        if (code == null) {
            return;
        }
        MesDvRepairDO repair = repairMapper.selectByCode(code);
        // TODO @AI：参考别的模块，notEquals
        if (repair == null) {
            return;
        }
        if (id == null || !id.equals(repair.getId())) {
            throw exception(DV_REPAIR_CODE_DUPLICATE);
        }
    }

    @Override
    public void validateRepairExists(Long id) {
        if (repairMapper.selectById(id) == null) {
            throw exception(DV_REPAIR_NOT_EXISTS);
        }
    }

    @Override
    public Long getRepairCountByMachineryId(Long machineryId) {
        return repairMapper.selectCountByMachineryId(machineryId);
    }

    /**
     * 校验维修工单是否为草稿状态
     *
     * @param id 编号
     * @return 维修工单
     */
    public MesDvRepairDO validateRepairDraft(Long id) {
        // TODO @AI：复用 validateRepairExists 方法
        MesDvRepairDO repair = repairMapper.selectById(id);
        if (repair == null) {
            throw exception(DV_REPAIR_NOT_EXISTS);
        }
        if (ObjUtil.notEqual(MesDvRepairStatusEnum.DRAFT.getStatus(), repair.getStatus())) {
            throw exception(DV_REPAIR_NOT_DRAFT);
        }
        return repair;
    }

    @Override
    public MesDvRepairDO getRepair(Long id) {
        return repairMapper.selectById(id);
    }

    @Override
    public PageResult<MesDvRepairDO> getRepairPage(MesDvRepairPageReqVO pageReqVO) {
        return repairMapper.selectPage(pageReqVO);
    }

    @Override
    public void confirmRepair(Long id) {
        // 1. 校验存在，且状态为草稿
        validateRepairDraft(id);

        // 2. 更新状态为已确认，结果为通过，自动设置验收日期
        repairMapper.updateById(new MesDvRepairDO().setId(id)
                .setStatus(MesDvRepairStatusEnum.CONFIRMED.getStatus())
                .setResult(MesDvRepairResultEnum.PASS.getResult())
                .setConfirmDate(LocalDateTime.now()));
    }

    @Override
    public void rejectRepair(Long id) {
        // 1. 校验存在，且状态为草稿
        validateRepairDraft(id);

        // 2. 更新状态为已确认，结果为不通过，自动设置验收日期
        repairMapper.updateById(new MesDvRepairDO().setId(id)
                .setStatus(MesDvRepairStatusEnum.CONFIRMED.getStatus())
                .setResult(MesDvRepairResultEnum.FAIL.getResult())
                .setConfirmDate(LocalDateTime.now()));
    }

}
