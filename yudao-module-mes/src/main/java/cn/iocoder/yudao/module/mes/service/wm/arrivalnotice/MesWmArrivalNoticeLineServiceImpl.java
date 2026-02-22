package cn.iocoder.yudao.module.mes.service.wm.arrivalnotice;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.arrivalnotice.vo.line.MesWmArrivalNoticeLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.arrivalnotice.vo.line.MesWmArrivalNoticeLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.arrivalnotice.MesWmArrivalNoticeDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.arrivalnotice.MesWmArrivalNoticeLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.arrivalnotice.MesWmArrivalNoticeLineMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmArrivalNoticeStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 到货通知单行 Service 实现类
 */
@Service
@Validated
public class MesWmArrivalNoticeLineServiceImpl implements MesWmArrivalNoticeLineService {

    @Resource
    private MesWmArrivalNoticeLineMapper arrivalNoticeLineMapper;

    @Resource
    @Lazy
    private MesWmArrivalNoticeService arrivalNoticeService;

    @Override
    public Long createArrivalNoticeLine(MesWmArrivalNoticeLineSaveReqVO createReqVO) {
        // 校验父单据存在且为草稿状态
        validateNoticeStatusDraft(createReqVO.getNoticeId());

        // 插入
        MesWmArrivalNoticeLineDO line = BeanUtils.toBean(createReqVO, MesWmArrivalNoticeLineDO.class);
        // 如果不需要检验，则合格品数量直接等于到货数量
        if (BooleanUtil.isFalse(line.getIqcCheckFlag())) {
            line.setQualifiedQuantity(line.getArrivalQuantity());
        }
        arrivalNoticeLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateArrivalNoticeLine(MesWmArrivalNoticeLineSaveReqVO updateReqVO) {
        // 校验存在
        MesWmArrivalNoticeLineDO line = validateArrivalNoticeLineExists(updateReqVO.getId());
        // 校验父单据存在且为草稿状态
        validateNoticeStatusDraft(line.getNoticeId());

        // 更新
        MesWmArrivalNoticeLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmArrivalNoticeLineDO.class);
        // 如果不需要检验，则合格品数量直接等于到货数量
        if (BooleanUtil.isFalse(updateObj.getIqcCheckFlag())) {
            updateObj.setQualifiedQuantity(updateObj.getArrivalQuantity());
        }
        arrivalNoticeLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteArrivalNoticeLine(Long id) {
        // 校验存在
        validateArrivalNoticeLineExists(id);
        // 删除
        arrivalNoticeLineMapper.deleteById(id);
    }

    @Override
    public MesWmArrivalNoticeLineDO getArrivalNoticeLine(Long id) {
        return arrivalNoticeLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmArrivalNoticeLineDO> getArrivalNoticeLinePage(MesWmArrivalNoticeLinePageReqVO pageReqVO) {
        return arrivalNoticeLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmArrivalNoticeLineDO> getArrivalNoticeLineListByNoticeId(Long noticeId) {
        return arrivalNoticeLineMapper.selectListByNoticeId(noticeId);
    }

    @Override
    public void deleteArrivalNoticeLineByNoticeId(Long noticeId) {
        arrivalNoticeLineMapper.deleteByNoticeId(noticeId);
    }

    private MesWmArrivalNoticeLineDO validateArrivalNoticeLineExists(Long id) {
        MesWmArrivalNoticeLineDO line = arrivalNoticeLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_ARRIVAL_NOTICE_LINE_NOT_EXISTS);
        }
        return line;
    }

    /**
     * 校验父到货通知单存在且为草稿状态
     */
    private void validateNoticeStatusDraft(Long noticeId) {
        MesWmArrivalNoticeDO notice = arrivalNoticeService.getArrivalNotice(noticeId);
        if (notice == null) {
            throw exception(WM_ARRIVAL_NOTICE_NOT_EXISTS);
        }
        if (ObjUtil.notEqual(MesWmArrivalNoticeStatusEnum.PREPARE.getStatus(), notice.getStatus())) {
            throw exception(WM_ARRIVAL_NOTICE_STATUS_NOT_PREPARE);
        }
    }

}
