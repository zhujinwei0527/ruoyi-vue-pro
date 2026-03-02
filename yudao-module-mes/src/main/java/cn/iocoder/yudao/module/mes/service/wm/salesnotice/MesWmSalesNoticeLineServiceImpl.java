package cn.iocoder.yudao.module.mes.service.wm.salesnotice;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.salesnotice.vo.line.MesWmSalesNoticeLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.salesnotice.vo.line.MesWmSalesNoticeLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.salesnotice.MesWmSalesNoticeDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.salesnotice.MesWmSalesNoticeLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.salesnotice.MesWmSalesNoticeLineMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 发货通知单行 Service 实现类
 */
@Service
@Validated
public class MesWmSalesNoticeLineServiceImpl implements MesWmSalesNoticeLineService {

    @Resource
    private MesWmSalesNoticeLineMapper salesNoticeLineMapper;

    @Resource
    @Lazy
    private MesWmSalesNoticeService salesNoticeService;

    @Override
    public Long createSalesNoticeLine(MesWmSalesNoticeLineSaveReqVO createReqVO) {
        // 校验父单据存在且为草稿状态
        validateNoticeStatusDraft(createReqVO.getNoticeId());

        // 插入
        MesWmSalesNoticeLineDO line = BeanUtils.toBean(createReqVO, MesWmSalesNoticeLineDO.class);
        salesNoticeLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateSalesNoticeLine(MesWmSalesNoticeLineSaveReqVO updateReqVO) {
        // 校验存在
        MesWmSalesNoticeLineDO line = validateSalesNoticeLineExists(updateReqVO.getId());
        // 校验父单据存在且为草稿状态
        validateNoticeStatusDraft(line.getNoticeId());

        // 更新
        MesWmSalesNoticeLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmSalesNoticeLineDO.class);
        salesNoticeLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteSalesNoticeLine(Long id) {
        // 校验存在
        validateSalesNoticeLineExists(id);
        // 删除
        salesNoticeLineMapper.deleteById(id);
    }

    @Override
    public MesWmSalesNoticeLineDO getSalesNoticeLine(Long id) {
        return salesNoticeLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmSalesNoticeLineDO> getSalesNoticeLinePage(MesWmSalesNoticeLinePageReqVO pageReqVO) {
        return salesNoticeLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmSalesNoticeLineDO> getSalesNoticeLineListByNoticeId(Long noticeId) {
        return salesNoticeLineMapper.selectListByNoticeId(noticeId);
    }

    @Override
    public void deleteSalesNoticeLineByNoticeId(Long noticeId) {
        salesNoticeLineMapper.deleteByNoticeId(noticeId);
    }

    private MesWmSalesNoticeLineDO validateSalesNoticeLineExists(Long id) {
        MesWmSalesNoticeLineDO line = salesNoticeLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_SALES_NOTICE_LINE_NOT_EXISTS);
        }
        return line;
    }

    /**
     * 校验父发货通知单存在且为草稿状态
     */
    private void validateNoticeStatusDraft(Long noticeId) {
        MesWmSalesNoticeDO notice = salesNoticeService.getSalesNotice(noticeId);
        if (notice == null) {
            throw exception(WM_SALES_NOTICE_NOT_EXISTS);
        }
        if (ObjUtil.notEqual(0, notice.getStatus())) {
            throw exception(WM_SALES_NOTICE_STATUS_NOT_ALLOW_UPDATE);
        }
    }

}
