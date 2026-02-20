package cn.iocoder.yudao.module.mes.service.dv.maintenrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.dv.maintenrecord.vo.line.MesDvMaintenRecordLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.dv.maintenrecord.vo.line.MesDvMaintenRecordLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.maintenrecord.MesDvMaintenRecordLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.dv.maintenrecord.MesDvMaintenRecordLineMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import jakarta.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.MAINTEN_RECORD_LINE_NOT_EXISTS;

// TODO @AI：注释风格；
/**
 * 设备保养记录明细 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesDvMaintenRecordLineServiceImpl implements MesDvMaintenRecordLineService {

    @Resource
    private MesDvMaintenRecordLineMapper maintenRecordLineMapper;

    @Override
    public Long createMaintenRecordLine(MesDvMaintenRecordLineSaveReqVO createReqVO) {
        // TODO @AI：必要的校验
        MesDvMaintenRecordLineDO maintenRecordLine = BeanUtils.toBean(createReqVO, MesDvMaintenRecordLineDO.class);
        maintenRecordLineMapper.insert(maintenRecordLine);
        return maintenRecordLine.getId();
    }

    @Override
    public void updateMaintenRecordLine(MesDvMaintenRecordLineSaveReqVO updateReqVO) {
        // TODO @AI：必要的校验
        validateMaintenRecordLineExists(updateReqVO.getId());
        MesDvMaintenRecordLineDO updateObj = BeanUtils.toBean(updateReqVO, MesDvMaintenRecordLineDO.class);
        maintenRecordLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteMaintenRecordLine(Long id) {
        validateMaintenRecordLineExists(id);
        maintenRecordLineMapper.deleteById(id);
    }

    private void validateMaintenRecordLineExists(Long id) {
        if (maintenRecordLineMapper.selectById(id) == null) {
            throw exception(MAINTEN_RECORD_LINE_NOT_EXISTS);
        }
    }

    @Override
    public MesDvMaintenRecordLineDO getMaintenRecordLine(Long id) {
        return maintenRecordLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesDvMaintenRecordLineDO> getMaintenRecordLinePage(MesDvMaintenRecordLinePageReqVO pageReqVO) {
        return maintenRecordLineMapper.selectPage(pageReqVO);
    }

}