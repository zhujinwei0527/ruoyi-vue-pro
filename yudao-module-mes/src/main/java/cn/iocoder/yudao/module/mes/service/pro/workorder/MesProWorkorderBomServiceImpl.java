package cn.iocoder.yudao.module.mes.service.pro.workorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.bom.MesProWorkOrderBomPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.bom.MesProWorkOrderBomSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderBomDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.workorder.MesProWorkOrderBomMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.PRO_WORK_ORDER_BOM_NOT_EXISTS;

/**
 * MES 生产工单 BOM Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesProWorkOrderBomServiceImpl implements MesProWorkOrderBomService {

    @Resource
    private MesProWorkOrderBomMapper workOrderBomMapper;

    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private MesProWorkOrderService workOrderService;

    @Override
    public Long createWorkOrderBom(MesProWorkOrderBomSaveReqVO createReqVO) {
        // 校验工单存在
        workOrderService.validateWorkOrderExists(createReqVO.getWorkOrderId());
        MesProWorkOrderBomDO workOrderBom = BeanUtils.toBean(createReqVO, MesProWorkOrderBomDO.class);
        workOrderBomMapper.insert(workOrderBom);
        return workOrderBom.getId();
    }

    @Override
    public void updateWorkOrderBom(MesProWorkOrderBomSaveReqVO updateReqVO) {
        // 校验存在
        validateWorkOrderBomExists(updateReqVO.getId());
        // 更新
        MesProWorkOrderBomDO updateObj = BeanUtils.toBean(updateReqVO, MesProWorkOrderBomDO.class);
        workOrderBomMapper.updateById(updateObj);
    }

    @Override
    public void deleteWorkOrderBom(Long id) {
        // 校验存在
        validateWorkOrderBomExists(id);
        // 删除
        workOrderBomMapper.deleteById(id);
    }

    private void validateWorkOrderBomExists(Long id) {
        if (workOrderBomMapper.selectById(id) == null) {
            throw exception(PRO_WORK_ORDER_BOM_NOT_EXISTS);
        }
    }

    @Override
    public MesProWorkOrderBomDO getWorkOrderBom(Long id) {
        return workOrderBomMapper.selectById(id);
    }

    @Override
    public PageResult<MesProWorkOrderBomDO> getWorkOrderBomPage(MesProWorkOrderBomPageReqVO pageReqVO) {
        return workOrderBomMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesProWorkOrderBomDO> getWorkOrderBomListByWorkOrderId(Long workOrderId) {
        return workOrderBomMapper.selectListByWorkOrderId(workOrderId);
    }

    @Override
    public void deleteWorkOrderBomByWorkOrderId(Long workOrderId) {
        workOrderBomMapper.deleteByWorkOrderId(workOrderId);
    }

}
