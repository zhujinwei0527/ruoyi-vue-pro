package cn.iocoder.yudao.module.mes.service.dv.maintenrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.dv.maintenrecord.vo.MesDvMaintenRecordPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.dv.maintenrecord.vo.MesDvMaintenRecordSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.maintenrecord.MesDvMaintenRecordDO;
import cn.iocoder.yudao.module.mes.dal.mysql.dv.maintenrecord.MesDvMaintenRecordMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.dv.maintenrecord.MesDvMaintenRecordLineMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import jakarta.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.MAINTEN_RECORD_NOT_EXISTS;

// TODO @AI：注释风格；
/**
 * 设备保养记录 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesDvMaintenRecordServiceImpl implements MesDvMaintenRecordService {

    @Resource
    private MesDvMaintenRecordMapper maintenRecordMapper;

    @Resource
    private MesDvMaintenRecordLineMapper maintenRecordLineMapper;

    @Override
    public Long createMaintenRecord(MesDvMaintenRecordSaveReqVO createReqVO) {
        // TODO @AI：必要的校验
        // 插入
        MesDvMaintenRecordDO maintenRecord = BeanUtils.toBean(createReqVO, MesDvMaintenRecordDO.class);
        maintenRecordMapper.insert(maintenRecord);
        return maintenRecord.getId();
    }

    @Override
    public void updateMaintenRecord(MesDvMaintenRecordSaveReqVO updateReqVO) {
        // TODO @AI：必要的校验
        // 校验存在
        validateMaintenRecordExists(updateReqVO.getId());

        // 更新
        MesDvMaintenRecordDO updateObj = BeanUtils.toBean(updateReqVO, MesDvMaintenRecordDO.class);
        maintenRecordMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaintenRecord(Long id) {
        // 校验存在
        validateMaintenRecordExists(id);

        // 删除
        maintenRecordMapper.deleteById(id);
        // 级联删除子表
        maintenRecordLineMapper.deleteByRecordId(id);
    }

    private void validateMaintenRecordExists(Long id) {
        if (maintenRecordMapper.selectById(id) == null) {
            throw exception(MAINTEN_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public MesDvMaintenRecordDO getMaintenRecord(Long id) {
        return maintenRecordMapper.selectById(id);
    }

    @Override
    public PageResult<MesDvMaintenRecordDO> getMaintenRecordPage(MesDvMaintenRecordPageReqVO pageReqVO) {
        return maintenRecordMapper.selectPage(pageReqVO);
    }

}
