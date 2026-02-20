package cn.iocoder.yudao.module.mes.service.dv.checkrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.dv.checkrecord.vo.line.MesDvCheckRecordLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.dv.checkrecord.vo.line.MesDvCheckRecordLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.checkrecord.MesDvCheckRecordLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.dv.checkrecord.MesDvCheckRecordLineMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.DV_CHECK_RECORD_LINE_NOT_EXISTS;

/**
 * MES 设备点检记录明细 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesDvCheckRecordLineServiceImpl implements MesDvCheckRecordLineService {

    @Resource
    private MesDvCheckRecordLineMapper checkRecordLineMapper;

    @Override
    public Long createCheckRecordLine(MesDvCheckRecordLineSaveReqVO createReqVO) {
        // TODO @AI：校验下存在
        MesDvCheckRecordLineDO line = BeanUtils.toBean(createReqVO, MesDvCheckRecordLineDO.class);
        checkRecordLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateCheckRecordLine(MesDvCheckRecordLineSaveReqVO updateReqVO) {
        // TODO @AI：校验下存在
        // 校验存在
        validateCheckRecordLineExists(updateReqVO.getId());

        // 更新
        MesDvCheckRecordLineDO updateObj = BeanUtils.toBean(updateReqVO, MesDvCheckRecordLineDO.class);
        checkRecordLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteCheckRecordLine(Long id) {
        // 校验存在
        validateCheckRecordLineExists(id);
        // 删除
        checkRecordLineMapper.deleteById(id);
    }

    @Override
    public void deleteByRecordId(Long recordId) {
        checkRecordLineMapper.deleteByRecordId(recordId);
    }

    @Override
    public void validateCheckRecordLineExists(Long id) {
        if (checkRecordLineMapper.selectById(id) == null) {
            throw exception(DV_CHECK_RECORD_LINE_NOT_EXISTS);
        }
    }

    @Override
    public MesDvCheckRecordLineDO getCheckRecordLine(Long id) {
        return checkRecordLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesDvCheckRecordLineDO> getCheckRecordLinePage(MesDvCheckRecordLinePageReqVO pageReqVO) {
        return checkRecordLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesDvCheckRecordLineDO> getCheckRecordLineListByRecordId(Long recordId) {
        return checkRecordLineMapper.selectListByRecordId(recordId);
    }

}
