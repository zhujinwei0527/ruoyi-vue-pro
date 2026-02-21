package cn.iocoder.yudao.module.mes.service.pro.andon;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.andon.vo.record.MesProAndonRecordHandleReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.andon.vo.record.MesProAndonRecordPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.andon.vo.record.MesProAndonRecordSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.andon.MesProAndonRecordDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.andon.MesProAndonRecordMapper;
import cn.iocoder.yudao.module.mes.enums.pro.MesProAndonStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.process.MesProProcessService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.PRO_ANDON_RECORD_ALREADY_HANDLED;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.PRO_ANDON_RECORD_NOT_EXISTS;

/**
 * MES 安灯呼叫记录 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesProAndonRecordServiceImpl implements MesProAndonRecordService {

    @Resource
    private MesProAndonRecordMapper andonRecordMapper;

    @Resource
    private MesMdWorkstationService workstationService;

    @Resource
    private MesProWorkOrderService workOrderService;

    @Resource
    private MesProProcessService processService;

    @Override
    public Long createAndonRecord(MesProAndonRecordSaveReqVO createReqVO) {
        // 1. 校验关联数据存在
        workstationService.getWorkstation(createReqVO.getWorkstationId()); // 工作站必填，直接校验
        if (createReqVO.getWorkOrderId() != null) {
            workOrderService.validateWorkOrderExists(createReqVO.getWorkOrderId());
        }
        if (createReqVO.getProcessId() != null) {
            processService.getProcess(createReqVO.getProcessId());
        }

        // 2. 插入
        MesProAndonRecordDO record = BeanUtils.toBean(createReqVO, MesProAndonRecordDO.class);
        record.setStatus(MesProAndonStatusEnum.ACTIVE.getStatus());
        andonRecordMapper.insert(record);
        return record.getId();
    }

    @Override
    public void deleteAndonRecord(Long id) {
        // 1. 校验存在 + 未处置
        validateAndonRecordNotHandled(id);
        // 2. 删除
        andonRecordMapper.deleteById(id);
    }

    @Override
    public void handleAndonRecord(MesProAndonRecordHandleReqVO handleReqVO) {
        // 1. 校验存在 + 未处置
        validateAndonRecordNotHandled(handleReqVO.getId());

        // 2. 更新为已处置
        andonRecordMapper.updateById(new MesProAndonRecordDO().setId(handleReqVO.getId())
                .setStatus(MesProAndonStatusEnum.HANDLED.getStatus())
                .setHandleTime(handleReqVO.getHandleTime())
                .setHandlerUserId(handleReqVO.getHandlerUserId())
                .setRemark(handleReqVO.getRemark()));
    }

    /**
     * 校验安灯记录存在，且未处置
     *
     * @param id 编号
     * @return 安灯记录
     */
    private MesProAndonRecordDO validateAndonRecordNotHandled(Long id) {
        MesProAndonRecordDO record = andonRecordMapper.selectById(id);
        if (record == null) {
            throw exception(PRO_ANDON_RECORD_NOT_EXISTS);
        }
        if (ObjUtil.equal(record.getStatus(), MesProAndonStatusEnum.HANDLED.getStatus())) {
            throw exception(PRO_ANDON_RECORD_ALREADY_HANDLED);
        }
        return record;
    }

    @Override
    public MesProAndonRecordDO getAndonRecord(Long id) {
        return andonRecordMapper.selectById(id);
    }

    @Override
    public PageResult<MesProAndonRecordDO> getAndonRecordPage(MesProAndonRecordPageReqVO pageReqVO) {
        return andonRecordMapper.selectPage(pageReqVO);
    }

}
