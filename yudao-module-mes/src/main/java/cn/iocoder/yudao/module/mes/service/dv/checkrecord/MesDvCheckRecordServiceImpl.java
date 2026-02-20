package cn.iocoder.yudao.module.mes.service.dv.checkrecord;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.dv.checkrecord.vo.MesDvCheckRecordPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.dv.checkrecord.vo.MesDvCheckRecordSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.checkplan.MesDvCheckPlanSubjectDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.checkrecord.MesDvCheckRecordDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.checkrecord.MesDvCheckRecordLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.dv.checkrecord.MesDvCheckRecordLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.dv.checkrecord.MesDvCheckRecordMapper;
import cn.iocoder.yudao.module.mes.service.dv.checkplan.MesDvCheckPlanService;
import cn.iocoder.yudao.module.mes.service.dv.checkplan.MesDvCheckPlanSubjectService;
import cn.iocoder.yudao.module.mes.service.dv.machinery.MesDvMachineryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 设备点检记录 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesDvCheckRecordServiceImpl implements MesDvCheckRecordService {

    // TODO @AI：枚举类；
    /**
     * 待点检状态
     */
    private static final int STATUS_PREPARE = 10;
    /**
     * 已完成状态
     */
    private static final int STATUS_FINISHED = 20;

    @Resource
    private MesDvCheckRecordMapper checkRecordMapper;
    @Resource
    private MesDvCheckRecordLineMapper checkRecordLineMapper;
    @Resource
    private MesDvMachineryService machineryService;
    @Resource
    private MesDvCheckPlanService checkPlanService;
    @Resource
    private MesDvCheckPlanSubjectService checkPlanSubjectService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCheckRecord(MesDvCheckRecordSaveReqVO createReqVO) {
        // 1. 校验关联数据
        validateCheckRecordRelation(createReqVO);

        // 2. 插入主记录，状态默认为待点检
        MesDvCheckRecordDO checkRecord = BeanUtils.toBean(createReqVO, MesDvCheckRecordDO.class)
                .setStatus(STATUS_PREPARE);
        checkRecordMapper.insert(checkRecord);

        // 3. 如果指定了点检计划，自动生成明细行
        if (createReqVO.getPlanId() != null) {
            generateCheckRecordLines(checkRecord.getId(), createReqVO.getPlanId());
        }
        return checkRecord.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCheckRecord(MesDvCheckRecordSaveReqVO updateReqVO) {
        // 1.1 校验存在 + 状态为待点检
        MesDvCheckRecordDO existRecord = validateCheckRecordPrepare(updateReqVO.getId());
        // 1.2 校验关联数据
        validateCheckRecordRelation(updateReqVO);

        // 2. 如果计划变更，删除旧明细并重新生成
        Long oldPlanId = existRecord.getPlanId();
        Long newPlanId = updateReqVO.getPlanId();
        boolean planChanged = !java.util.Objects.equals(oldPlanId, newPlanId);
        if (planChanged) {
            checkRecordLineMapper.deleteByRecordId(updateReqVO.getId());
            if (newPlanId != null) {
                generateCheckRecordLines(updateReqVO.getId(), newPlanId);
            }
        }

        // 3. 更新主记录
        MesDvCheckRecordDO updateObj = BeanUtils.toBean(updateReqVO, MesDvCheckRecordDO.class);
        checkRecordMapper.updateById(updateObj);
    }

    @Override
    public void submitCheckRecord(Long id) {
        // 1.1 校验状态为待点检
        validateCheckRecordPrepare(id);
        // 1.2 校验至少有一条明细
        List<MesDvCheckRecordLineDO> lines = checkRecordLineMapper.selectListByRecordId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(DV_CHECK_RECORD_NO_LINE);
        }

        // 2. 状态改为已完成
        MesDvCheckRecordDO updateObj = new MesDvCheckRecordDO()
                .setId(id).setStatus(STATUS_FINISHED);
        checkRecordMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCheckRecord(Long id) {
        // 1. 校验状态为待点检（已完成不可删除）
        validateCheckRecordPrepare(id);

        // 2.1 删除主记录
        checkRecordMapper.deleteById(id);
        // 2.2 级联删除明细
        checkRecordLineMapper.deleteByRecordId(id);
    }

    @Override
    public void validateCheckRecordExists(Long id) {
        if (checkRecordMapper.selectById(id) == null) {
            throw exception(DV_CHECK_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public MesDvCheckRecordDO getCheckRecord(Long id) {
        return checkRecordMapper.selectById(id);
    }

    @Override
    public PageResult<MesDvCheckRecordDO> getCheckRecordPage(MesDvCheckRecordPageReqVO pageReqVO) {
        return checkRecordMapper.selectPage(pageReqVO);
    }

    // ==================== 私有方法 ====================

    /**
     * 校验点检记录为待点检状态
     */
    private MesDvCheckRecordDO validateCheckRecordPrepare(Long id) {
        MesDvCheckRecordDO record = checkRecordMapper.selectById(id);
        if (record == null) {
            throw exception(DV_CHECK_RECORD_NOT_EXISTS);
        }
        // TODO @AI：ObjUtil notEquals
        if (!STATUS_PREPARE.equals(record.getStatus())) {
            throw exception(DV_CHECK_RECORD_NOT_PREPARE);
        }
        return record;
    }

    private void validateCheckRecordRelation(MesDvCheckRecordSaveReqVO reqVO) {
        // 校验设备是否存在
        machineryService.validateMachineryExists(reqVO.getMachineryId());
        // 校验点检计划是否存在
        if (reqVO.getPlanId() != null) {
            checkPlanService.validateCheckPlanExists(reqVO.getPlanId());
        }
    }

    /**
     * 根据计划自动生成点检项目明细行
     */
    private void generateCheckRecordLines(Long recordId, Long planId) {
        List<MesDvCheckPlanSubjectDO> planSubjects = checkPlanSubjectService.getCheckPlanSubjectListByPlanId(planId);
        if (CollUtil.isEmpty(planSubjects)) {
            return;
        }
        // TODO @AI：批量插入；
        for (MesDvCheckPlanSubjectDO planSubject : planSubjects) {
            MesDvCheckRecordLineDO line = new MesDvCheckRecordLineDO()
                    .setRecordId(recordId).setSubjectId(planSubject.getSubjectId()).setCheckStatus("Y");
            checkRecordLineMapper.insert(line);
        }
    }

}
