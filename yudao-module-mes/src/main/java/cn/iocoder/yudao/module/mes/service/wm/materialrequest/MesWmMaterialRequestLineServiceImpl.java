package cn.iocoder.yudao.module.mes.service.wm.materialrequest;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.line.MesWmMaterialRequestLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.line.MesWmMaterialRequestLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialrequest.MesWmMaterialRequestLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.materialrequest.MesWmMaterialRequestLineMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_MATERIAL_REQUEST_LINE_NOT_EXISTS;

/**
 * MES 领料申请单行 Service 实现类
 */
@Service
@Validated
public class MesWmMaterialRequestLineServiceImpl implements MesWmMaterialRequestLineService {

    @Resource
    private MesWmMaterialRequestLineMapper materialRequestLineMapper;

    @Override
    public Long createMaterialRequestLine(MesWmMaterialRequestLineSaveReqVO createReqVO) {
        // TODO @AI：校验关联字段

        // 插入
        MesWmMaterialRequestLineDO line = BeanUtils.toBean(createReqVO, MesWmMaterialRequestLineDO.class);
        materialRequestLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateMaterialRequestLine(MesWmMaterialRequestLineSaveReqVO updateReqVO) {
        // 验证是否存在
        validateMaterialRequestLineExists(updateReqVO.getId());
        // TODO @AI：校验关联字段

        // 更新
        MesWmMaterialRequestLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmMaterialRequestLineDO.class);
        materialRequestLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteMaterialRequestLine(Long id) {
        // 验证是否存在
        validateMaterialRequestLineExists(id);
        // 删除
        materialRequestLineMapper.deleteById(id);
    }

    @Override
    public MesWmMaterialRequestLineDO getMaterialRequestLine(Long id) {
        return materialRequestLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmMaterialRequestLineDO> getMaterialRequestLinePage(MesWmMaterialRequestLinePageReqVO pageReqVO) {
        return materialRequestLineMapper.selectPage(pageReqVO);
    }

    private MesWmMaterialRequestLineDO validateMaterialRequestLineExists(Long id) {
        MesWmMaterialRequestLineDO line = materialRequestLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_MATERIAL_REQUEST_LINE_NOT_EXISTS);
        }
        return line;
    }

}
