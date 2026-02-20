package cn.iocoder.yudao.module.mes.service.dv.checkplan;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.dv.checkplan.vo.machinery.MesDvCheckPlanMachinerySaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.checkplan.MesDvCheckPlanMachineryDO;
import cn.iocoder.yudao.module.mes.dal.mysql.dv.checkplan.MesDvCheckPlanMachineryMapper;
import cn.iocoder.yudao.module.mes.service.dv.machinery.MesDvMachineryService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 点检保养方案设备 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesDvCheckPlanMachineryServiceImpl implements MesDvCheckPlanMachineryService {

    @Resource
    private MesDvCheckPlanMachineryMapper checkPlanMachineryMapper;
    @Resource
    @Lazy
    private MesDvCheckPlanService checkPlanService;
    @Resource
    private MesDvMachineryService machineryService;

    @Override
    public Long createCheckPlanMachinery(MesDvCheckPlanMachinerySaveReqVO createReqVO) {
        // 1.1 校验方案草稿状态
        checkPlanService.validateCheckPlanPrepare(createReqVO.getPlanId());
        // 1.2 校验设备存在
        machineryService.validateMachineryExists(createReqVO.getMachineryId());

        // 2. 插入
        MesDvCheckPlanMachineryDO planMachinery = BeanUtils.toBean(createReqVO, MesDvCheckPlanMachineryDO.class);
        checkPlanMachineryMapper.insert(planMachinery);
        return planMachinery.getId();
    }

    @Override
    public void deleteCheckPlanMachinery(Long id) {
        // 1.1 校验存在
        MesDvCheckPlanMachineryDO existRecord = checkPlanMachineryMapper.selectById(id);
        if (existRecord == null) {
            throw exception(DV_CHECK_PLAN_MACHINERY_NOT_EXISTS);
        }
        // 1.2 校验方案草稿状态
        checkPlanService.validateCheckPlanPrepare(existRecord.getPlanId());

        // 2. 删除
        checkPlanMachineryMapper.deleteById(id);
    }

    @Override
    public List<MesDvCheckPlanMachineryDO> getCheckPlanMachineryListByPlanId(Long planId) {
        return checkPlanMachineryMapper.selectListByPlanId(planId);
    }

    @Override
    public Long getCheckPlanMachineryCountByPlanId(Long planId) {
        return checkPlanMachineryMapper.selectCountByPlanId(planId);
    }

    @Override
    public void deleteByPlanId(Long planId) {
        checkPlanMachineryMapper.deleteByPlanId(planId);
    }

}
