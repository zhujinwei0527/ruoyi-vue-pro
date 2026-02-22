package cn.iocoder.yudao.module.mes.service.wm.materialrequest;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.line.MesWmMaterialRequestLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.line.MesWmMaterialRequestLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialrequest.MesWmMaterialRequestDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialrequest.MesWmMaterialRequestLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.materialrequest.MesWmMaterialRequestLineMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmMaterialRequestStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 领料申请单行 Service 实现类
 */
@Service
@Validated
public class MesWmMaterialRequestLineServiceImpl implements MesWmMaterialRequestLineService {

    @Resource
    private MesWmMaterialRequestLineMapper materialRequestLineMapper;

    @Resource
    @Lazy
    private MesWmMaterialRequestService materialRequestService;

    @Override
    public Long createMaterialRequestLine(MesWmMaterialRequestLineSaveReqVO createReqVO) {
        // 校验父单据存在且为草稿状态
        validateMaterialRequestStatusDraft(createReqVO.getMaterialRequestId());

        // 插入
        MesWmMaterialRequestLineDO line = BeanUtils.toBean(createReqVO, MesWmMaterialRequestLineDO.class);
        materialRequestLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateMaterialRequestLine(MesWmMaterialRequestLineSaveReqVO updateReqVO) {
        // 校验存在
        MesWmMaterialRequestLineDO line = validateMaterialRequestLineExists(updateReqVO.getId());
        // 校验父单据存在且为草稿状态
        validateMaterialRequestStatusDraft(line.getMaterialRequestId());

        // 更新
        MesWmMaterialRequestLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmMaterialRequestLineDO.class);
        materialRequestLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteMaterialRequestLine(Long id) {
        // 校验存在
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

    @Override
    public List<MesWmMaterialRequestLineDO> getMaterialRequestLineListByMaterialRequestId(Long materialRequestId) {
        return materialRequestLineMapper.selectListByMaterialRequestId(materialRequestId);
    }

    @Override
    public void deleteMaterialRequestLineByMaterialRequestId(Long materialRequestId) {
        materialRequestLineMapper.deleteByMaterialRequestId(materialRequestId);
    }

    private MesWmMaterialRequestLineDO validateMaterialRequestLineExists(Long id) {
        MesWmMaterialRequestLineDO line = materialRequestLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_MATERIAL_REQUEST_LINE_NOT_EXISTS);
        }
        return line;
    }

    /**
     * 校验父领料申请单存在且为草稿状态
     */
    private void validateMaterialRequestStatusDraft(Long materialRequestId) {
        // TODO @AI：类似 itemReceiptService.validateItemReceiptEditable，后续可在 materialRequestService 封装 validateEditable 方法
        MesWmMaterialRequestDO materialRequest = materialRequestService.getMaterialRequest(materialRequestId);
        if (materialRequest == null) {
            throw exception(WM_MATERIAL_REQUEST_NOT_EXISTS);
        }
        if (ObjUtil.notEqual(MesWmMaterialRequestStatusEnum.PREPARE.getStatus(), materialRequest.getStatus())) {
            throw exception(WM_MATERIAL_REQUEST_STATUS_INVALID);
        }
    }

}
