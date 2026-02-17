package cn.iocoder.yudao.module.mes.service.pro.workorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.bom.MesProWorkorderBomPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.workorder.vo.bom.MesProWorkorderBomSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkorderBomDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.workorder.MesProWorkorderBomMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.PRO_WORKORDER_BOM_NOT_EXISTS;

/**
 * MES 生产工单 BOM Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesProWorkorderBomServiceImpl implements MesProWorkorderBomService {

    @Resource
    private MesProWorkorderBomMapper workorderBomMapper;

    @Override
    public Long createWorkorderBom(MesProWorkorderBomSaveReqVO createReqVO) {
        // TODO @AI：是不是要校验 workorder 存在？另外，大小写应该是 workOrder？
        MesProWorkorderBomDO workorderBom = BeanUtils.toBean(createReqVO, MesProWorkorderBomDO.class);
        workorderBomMapper.insert(workorderBom);
        return workorderBom.getId();
    }

    @Override
    public void updateWorkorderBom(MesProWorkorderBomSaveReqVO updateReqVO) {
        // 校验存在
        validateWorkorderBomExists(updateReqVO.getId());
        // 更新
        MesProWorkorderBomDO updateObj = BeanUtils.toBean(updateReqVO, MesProWorkorderBomDO.class);
        workorderBomMapper.updateById(updateObj);
    }

    @Override
    public void deleteWorkorderBom(Long id) {
        // 校验存在
        validateWorkorderBomExists(id);
        // 删除
        workorderBomMapper.deleteById(id);
    }

    private void validateWorkorderBomExists(Long id) {
        if (workorderBomMapper.selectById(id) == null) {
            throw exception(PRO_WORKORDER_BOM_NOT_EXISTS);
        }
    }

    @Override
    public MesProWorkorderBomDO getWorkorderBom(Long id) {
        return workorderBomMapper.selectById(id);
    }

    @Override
    public PageResult<MesProWorkorderBomDO> getWorkorderBomPage(MesProWorkorderBomPageReqVO pageReqVO) {
        return workorderBomMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesProWorkorderBomDO> getWorkorderBomListByWorkorderId(Long workorderId) {
        return workorderBomMapper.selectListByWorkorderId(workorderId);
    }

    @Override
    public void deleteWorkorderBomByWorkorderId(Long workorderId) {
        workorderBomMapper.deleteByWorkorderId(workorderId);
    }

}
