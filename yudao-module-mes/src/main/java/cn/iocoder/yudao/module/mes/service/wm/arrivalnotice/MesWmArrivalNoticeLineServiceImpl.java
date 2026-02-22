package cn.iocoder.yudao.module.mes.service.wm.arrivalnotice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.arrivalnotice.vo.line.MesWmArrivalNoticeLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.arrivalnotice.vo.line.MesWmArrivalNoticeLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.arrivalnotice.MesWmArrivalNoticeLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.arrivalnotice.MesWmArrivalNoticeLineMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_ARRIVAL_NOTICE_LINE_NOT_EXISTS;

/**
 * MES 到货通知单行 Service 实现类
 */
@Service
@Validated
public class MesWmArrivalNoticeLineServiceImpl implements MesWmArrivalNoticeLineService {

    @Resource
    private MesWmArrivalNoticeLineMapper arrivalNoticeLineMapper;

    @Override
    public Long createArrivalNoticeLine(MesWmArrivalNoticeLineSaveReqVO createReqVO) {
        // TODO @AI：关联表的校验；

        // 插入
        MesWmArrivalNoticeLineDO line = BeanUtils.toBean(createReqVO, MesWmArrivalNoticeLineDO.class);
        // 如果不需要检验，则合格品数量直接等于到货数量
        // TODO @AI：isFalase，减少反向判断；
        if (!Boolean.TRUE.equals(line.getIqcCheckFlag())) {
            line.setQualifiedQuantity(line.getArrivalQuantity());
        }
        arrivalNoticeLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateArrivalNoticeLine(MesWmArrivalNoticeLineSaveReqVO updateReqVO) {
        // 校验存在
        validateArrivalNoticeLineExists(updateReqVO.getId());
        // TODO @AI：关联表的校验；

        // 更新
        MesWmArrivalNoticeLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmArrivalNoticeLineDO.class);
        // 如果不需要检验，则合格品数量直接等于到货数量
        // TODO @AI：isFalase，减少反向判断；
        if (!Boolean.TRUE.equals(updateObj.getIqcCheckFlag())) {
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

    private void validateArrivalNoticeLineExists(Long id) {
        if (arrivalNoticeLineMapper.selectById(id) == null) {
            throw exception(WM_ARRIVAL_NOTICE_LINE_NOT_EXISTS);
        }
    }

}
