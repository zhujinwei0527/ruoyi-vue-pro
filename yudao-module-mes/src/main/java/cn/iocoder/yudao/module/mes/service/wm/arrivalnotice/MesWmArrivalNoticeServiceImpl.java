package cn.iocoder.yudao.module.mes.service.wm.arrivalnotice;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.arrivalnotice.vo.MesWmArrivalNoticePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.arrivalnotice.vo.MesWmArrivalNoticeSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.arrivalnotice.MesWmArrivalNoticeDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.arrivalnotice.MesWmArrivalNoticeLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.arrivalnotice.MesWmArrivalNoticeMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmArrivalNoticeStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 到货通知单 Service 实现类
 */
@Service
@Validated
public class MesWmArrivalNoticeServiceImpl implements MesWmArrivalNoticeService {

    @Resource
    private MesWmArrivalNoticeMapper arrivalNoticeMapper;

    @Resource
    private MesWmArrivalNoticeLineService arrivalNoticeLineService;

    @Override
    public Long createArrivalNotice(MesWmArrivalNoticeSaveReqVO createReqVO) {
        // 校验编码唯一
        validateCodeUnique(null, createReqVO.getCode());

        // 插入
        MesWmArrivalNoticeDO notice = BeanUtils.toBean(createReqVO, MesWmArrivalNoticeDO.class);
        notice.setStatus(MesWmArrivalNoticeStatusEnum.PREPARE.getStatus());
        arrivalNoticeMapper.insert(notice);
        return notice.getId();
    }

    @Override
    public void updateArrivalNotice(MesWmArrivalNoticeSaveReqVO updateReqVO) {
        // 校验存在 + 草稿状态
        validateArrivalNoticeExistsAndDraft(updateReqVO.getId());
        // 校验编码唯一
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 更新
        MesWmArrivalNoticeDO updateObj = BeanUtils.toBean(updateReqVO, MesWmArrivalNoticeDO.class);
        arrivalNoticeMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArrivalNotice(Long id) {
        // 校验存在 + 草稿状态
        validateArrivalNoticeExistsAndDraft(id);

        // 级联删除行
        arrivalNoticeLineService.deleteArrivalNoticeLineByNoticeId(id);
        // 删除
        arrivalNoticeMapper.deleteById(id);
    }

    @Override
    public MesWmArrivalNoticeDO getArrivalNotice(Long id) {
        return arrivalNoticeMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmArrivalNoticeDO> getArrivalNoticePage(MesWmArrivalNoticePageReqVO pageReqVO) {
        return arrivalNoticeMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitArrivalNotice(Long id) {
        // 1.1 校验存在 + 草稿状态
        validateArrivalNoticeExistsAndDraft(id);
        // 1.2 检查是否有行项目
        List<MesWmArrivalNoticeLineDO> lines = arrivalNoticeLineService.getArrivalNoticeLineListByNoticeId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_ARRIVAL_NOTICE_NO_LINE);
        }

        // 2. 检查所有行的 iqcCheckFlag：如果没有需要检验的行，则直接审批通过
        boolean needCheck = CollectionUtils.anyMatch(lines,
                line -> Boolean.TRUE.equals(line.getIqcCheckFlag()));
        if (!needCheck) {
            // 不需要检验，直接进入待入库
            arrivalNoticeMapper.updateById(new MesWmArrivalNoticeDO()
                    .setId(id).setStatus(MesWmArrivalNoticeStatusEnum.PENDING_RECEIPT.getStatus()));
        } else {
            // 需要检验，进入待质检
            arrivalNoticeMapper.updateById(new MesWmArrivalNoticeDO()
                    .setId(id).setStatus(MesWmArrivalNoticeStatusEnum.PENDING_QC.getStatus()));
        }
    }

    // TODO DONE @AI：确认由 IQC 模块在检验完成后调用。前端审批按钮已移除，仅保留 Service 方法供内部调用
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveArrivalNotice(Long id) {
        // 1.1 校验存在
        MesWmArrivalNoticeDO notice = validateArrivalNoticeExists(id);
        // 1.2 校验状态：只有待质检才允许审批
        if (ObjUtil.notEqual(MesWmArrivalNoticeStatusEnum.PENDING_QC.getStatus(), notice.getStatus())) {
            throw exception(WM_ARRIVAL_NOTICE_STATUS_NOT_PENDING_QC);
        }
        // 1.3 校验所有 iqcCheckFlag=true 的行必须 iqcId 不为空
        List<MesWmArrivalNoticeLineDO> lines = arrivalNoticeLineService.getArrivalNoticeLineListByNoticeId(id);
        boolean hasUnchecked = CollectionUtils.anyMatch(lines,
                line -> Boolean.TRUE.equals(line.getIqcCheckFlag()) && line.getIqcId() == null);
        if (hasUnchecked) {
            throw exception(WM_ARRIVAL_NOTICE_IQC_PENDING);
        }

        // 2. 审批通过
        arrivalNoticeMapper.updateById(new MesWmArrivalNoticeDO()
                .setId(id).setStatus(MesWmArrivalNoticeStatusEnum.PENDING_RECEIPT.getStatus()));
    }

    @Override
    public void finishArrivalNotice(Long id) {
        // 校验存在
        MesWmArrivalNoticeDO notice = validateArrivalNoticeExists(id);
        // 校验状态：只有待入库才允许完成
        if (ObjUtil.notEqual(MesWmArrivalNoticeStatusEnum.PENDING_RECEIPT.getStatus(), notice.getStatus())) {
            throw exception(WM_ARRIVAL_NOTICE_STATUS_NOT_PENDING_RECEIPT);
        }

        // 完成
        arrivalNoticeMapper.updateById(new MesWmArrivalNoticeDO()
                .setId(id).setStatus(MesWmArrivalNoticeStatusEnum.FINISHED.getStatus()));
    }

    @Override
    public List<MesWmArrivalNoticeDO> getArrivalNoticeList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return arrivalNoticeMapper.selectByIds(ids);
    }

    @Override
    public List<MesWmArrivalNoticeDO> getArrivalNoticeListByStatus(Integer status) {
        // TODO DONE @AI：已在 Mapper 新增 selectListByStatus 方法
        if (status == null) {
            return arrivalNoticeMapper.selectList();
        }
        return arrivalNoticeMapper.selectListByStatus(status);
    }

    private MesWmArrivalNoticeDO validateArrivalNoticeExists(Long id) {
        MesWmArrivalNoticeDO notice = arrivalNoticeMapper.selectById(id);
        if (notice == null) {
            throw exception(WM_ARRIVAL_NOTICE_NOT_EXISTS);
        }
        return notice;
    }

    /**
     * 校验到货通知单存在且为草稿状态
     */
    private MesWmArrivalNoticeDO validateArrivalNoticeExistsAndDraft(Long id) {
        MesWmArrivalNoticeDO notice = validateArrivalNoticeExists(id);
        if (ObjUtil.notEqual(MesWmArrivalNoticeStatusEnum.PREPARE.getStatus(), notice.getStatus())) {
            throw exception(WM_ARRIVAL_NOTICE_STATUS_NOT_PREPARE);
        }
        return notice;
    }

    private void validateCodeUnique(Long id, String code) {
        MesWmArrivalNoticeDO notice = arrivalNoticeMapper.selectByCode(code);
        if (notice == null) {
            return;
        }
        if (ObjUtil.notEqual(id, notice.getId())) {
            throw exception(WM_ARRIVAL_NOTICE_CODE_DUPLICATE);
        }
    }

}
