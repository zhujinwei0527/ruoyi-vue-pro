package cn.iocoder.yudao.module.mes.service.wm.arrivalnotice;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.arrivalnotice.vo.MesWmArrivalNoticePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.arrivalnotice.vo.MesWmArrivalNoticeSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.arrivalnotice.MesWmArrivalNoticeDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.arrivalnotice.MesWmArrivalNoticeLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.arrivalnotice.MesWmArrivalNoticeLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.arrivalnotice.MesWmArrivalNoticeMapper;
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

    // TODO @AI：不允许调用对方的，调用 service；
    @Resource
    private MesWmArrivalNoticeLineMapper arrivalNoticeLineMapper;

    @Override
    public Long createArrivalNotice(MesWmArrivalNoticeSaveReqVO createReqVO) {
        // 校验编码唯一
        validateCodeUnique(null, createReqVO.getCode());
        // TODO @AI：关联数据的校验

        // 插入
        MesWmArrivalNoticeDO notice = BeanUtils.toBean(createReqVO, MesWmArrivalNoticeDO.class);
        // TODO @AI：使用枚举类；
        notice.setStatus(0); // 草稿
        arrivalNoticeMapper.insert(notice);
        return notice.getId();
    }

    @Override
    public void updateArrivalNotice(MesWmArrivalNoticeSaveReqVO updateReqVO) {
        // 校验存在
        MesWmArrivalNoticeDO notice = validateArrivalNoticeExists(updateReqVO.getId());
        // 校验状态：只有草稿才允许修改
        // TODO @AI：使用枚举类；
        if (notice.getStatus() != 0) {
            throw exception(WM_ARRIVAL_NOTICE_STATUS_NOT_PREPARE);
        }
        // 校验编码唯一
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // TODO @AI：关联数据的校验

        // 更新
        MesWmArrivalNoticeDO updateObj = BeanUtils.toBean(updateReqVO, MesWmArrivalNoticeDO.class);
        arrivalNoticeMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArrivalNotice(Long id) {
        // 校验存在
        MesWmArrivalNoticeDO notice = validateArrivalNoticeExists(id);
        // 校验状态：只有草稿才允许删除 TODO @AI：抽个统一的校验+ 存在 “草稿”的方法，允许修改、删除
        // TODO @AI：使用枚举类；
        if (notice.getStatus() != 0) {
            throw exception(WM_ARRIVAL_NOTICE_STATUS_NOT_PREPARE);
        }

        // 级联删除行
        arrivalNoticeLineMapper.deleteByNoticeId(id);
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
    public void submitArrivalNotice(Long id) {
        // 校验存在
        MesWmArrivalNoticeDO notice = validateArrivalNoticeExists(id);
        if (notice.getStatus() != 0) { //  TODO @AI：抽个统一的校验+ 存在 “草稿”的方法，允许修改、删除
            throw exception(WM_ARRIVAL_NOTICE_STATUS_NOT_PREPARE);
        }

        // 检查是否有行项目
        // TODO @AI：搞个 count 方法，在对方 service，传递 noticeId + 状态
        List<MesWmArrivalNoticeLineDO> lines = arrivalNoticeLineMapper.selectListByNoticeId(id);
        // 检查所有行的 iqcCheckFlag：如果没有需要检验的行，则直接审批通过
        boolean needCheck = lines.stream().anyMatch(line -> Boolean.TRUE.equals(line.getIqcCheckFlag()));
        // TODO @AI：计算 status，然后去更新；
        if (!needCheck) {
            // 不需要检验，直接审批通过
            arrivalNoticeMapper.updateById(new MesWmArrivalNoticeDO().setId(id).setStatus(2));
        } else {
            // 需要检验，提交待审批
            arrivalNoticeMapper.updateById(new MesWmArrivalNoticeDO().setId(id).setStatus(1));
        }
    }

    @Override
    public void approveArrivalNotice(Long id) {
        // 校验存在
        MesWmArrivalNoticeDO notice = validateArrivalNoticeExists(id);
        if (notice.getStatus() != 1) {
            throw exception(WM_ARRIVAL_NOTICE_STATUS_NOT_PREPARE);
        }
        // TODO @AI：校验状态 + 状态；

        // 校验所有 iqcCheckFlag=true 的行必须 iqcId 不为空
        // TODO @AI：对方 service ；搞个 count 方法，在对方 service，传递 noticeId + 状态；另外，看看能不能和上面的，保持 service 方法，保持统一；
        List<MesWmArrivalNoticeLineDO> lines = arrivalNoticeLineMapper.selectListByNoticeId(id);
        boolean hasUnchecked = lines.stream()
                .anyMatch(line -> Boolean.TRUE.equals(line.getIqcCheckFlag()) && line.getIqcId() == null);
        if (hasUnchecked) {
            throw exception(WM_ARRIVAL_NOTICE_IQC_PENDING);
        }
        // 审批通过
        arrivalNoticeMapper.updateById(new MesWmArrivalNoticeDO().setId(id).setStatus(2));
    }

    @Override
    public void finishArrivalNotice(Long id) {
        validateArrivalNoticeExists(id);
        // TODO @AI：校验状态 + 状态；

        // 完成
        arrivalNoticeMapper.updateById(new MesWmArrivalNoticeDO().setId(id).setStatus(3));
    }

    @Override
    public List<MesWmArrivalNoticeDO> getArrivalNoticeList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return arrivalNoticeMapper.selectByIds(ids);
    }

    private MesWmArrivalNoticeDO validateArrivalNoticeExists(Long id) {
        MesWmArrivalNoticeDO notice = arrivalNoticeMapper.selectById(id);
        if (notice == null) {
            throw exception(WM_ARRIVAL_NOTICE_NOT_EXISTS);
        }
        return notice;
    }

    private void validateCodeUnique(Long id, String code) {
        MesWmArrivalNoticeDO notice = arrivalNoticeMapper.selectByCode(code);
        if (notice == null) {
            return;
        }
        // TODO @AI：notEquals
        if (id == null || !id.equals(notice.getId())) {
            throw exception(WM_ARRIVAL_NOTICE_CODE_DUPLICATE);
        }
    }

}
