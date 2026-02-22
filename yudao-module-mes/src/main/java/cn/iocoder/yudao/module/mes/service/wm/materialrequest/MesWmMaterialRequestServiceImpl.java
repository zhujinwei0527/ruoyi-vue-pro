package cn.iocoder.yudao.module.mes.service.wm.materialrequest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.MesWmMaterialRequestPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.MesWmMaterialRequestSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialrequest.MesWmMaterialRequestDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.materialrequest.MesWmMaterialRequestMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmMaterialRequestStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 领料申请单 Service 实现类
 */
@Service
@Validated
public class MesWmMaterialRequestServiceImpl implements MesWmMaterialRequestService {

    @Resource
    private MesWmMaterialRequestMapper materialRequestMapper;

    @Resource
    private MesWmMaterialRequestLineService materialRequestLineService;

    @Override
    public Long createMaterialRequest(MesWmMaterialRequestSaveReqVO createReqVO) {
        // TODO @AI：校验关联数据；

        // 2. 插入
        MesWmMaterialRequestDO materialRequest = BeanUtils.toBean(createReqVO, MesWmMaterialRequestDO.class);
        materialRequest.setStatus(MesWmMaterialRequestStatusEnum.PREPARE.getStatus());
        materialRequestMapper.insert(materialRequest);
        return materialRequest.getId();
    }

    @Override
    public void updateMaterialRequest(MesWmMaterialRequestSaveReqVO updateReqVO) {
        // 1. 校验存在 + 草稿状态
        validateMaterialRequestExistsAndDraft(updateReqVO.getId());
        // TODO @AI：校验关联数据；

        // 2. 更新
        MesWmMaterialRequestDO updateObj = BeanUtils.toBean(updateReqVO, MesWmMaterialRequestDO.class);
        materialRequestMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaterialRequest(Long id) {
        // 1. 校验存在 + 草稿状态
        validateMaterialRequestExistsAndDraft(id);

        // 2.1 级联删除行
        materialRequestLineService.deleteMaterialRequestLineByMaterialRequestId(id);
        // 2.2 删除
        materialRequestMapper.deleteById(id);
    }

    @Override
    public MesWmMaterialRequestDO getMaterialRequest(Long id) {
        return materialRequestMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmMaterialRequestDO> getMaterialRequestPage(MesWmMaterialRequestPageReqVO pageReqVO) {
        return materialRequestMapper.selectPage(pageReqVO);
    }

    @Override
    public void submitMaterialRequest(Long id) {
        // 1. 校验存在 + 草稿状态
        validateMaterialRequestExistsAndDraft(id);

        // 2. 草稿 → 备料中
        materialRequestMapper.updateById(new MesWmMaterialRequestDO()
                .setId(id).setStatus(MesWmMaterialRequestStatusEnum.PREPARING.getStatus()));
    }

    @Override
    public void approveMaterialRequest(Long id) {
        // 1.1 校验存在
        MesWmMaterialRequestDO materialRequest = validateMaterialRequestExists(id);
        // 1.2 校验状态：只有备料中才允许审批
        if (ObjUtil.notEqual(MesWmMaterialRequestStatusEnum.PREPARING.getStatus(), materialRequest.getStatus())) {
            throw exception(WM_MATERIAL_REQUEST_STATUS_INVALID);
        }

        // 2. 备料中 → 待领料
        materialRequestMapper.updateById(new MesWmMaterialRequestDO()
                .setId(id).setStatus(MesWmMaterialRequestStatusEnum.WAITING.getStatus()));
    }

    @Override
    public void finishMaterialRequest(Long id) {
        // 1.1 校验存在
        MesWmMaterialRequestDO materialRequest = validateMaterialRequestExists(id);
        // 1.2 校验状态：只有待领料才允许完成
        if (ObjUtil.notEqual(MesWmMaterialRequestStatusEnum.WAITING.getStatus(), materialRequest.getStatus())) {
            throw exception(WM_MATERIAL_REQUEST_STATUS_INVALID);
        }

        // 2. 待领料 → 已完成
        materialRequestMapper.updateById(new MesWmMaterialRequestDO()
                .setId(id).setStatus(MesWmMaterialRequestStatusEnum.FINISHED.getStatus()));
    }

    @Override
    public void cancelMaterialRequest(Long id) {
        // 1.1 校验存在
        MesWmMaterialRequestDO materialRequest = validateMaterialRequestExists(id);
        // 1.2 已完成不可取消
        if (ObjUtil.equal(MesWmMaterialRequestStatusEnum.FINISHED.getStatus(), materialRequest.getStatus())) {
            throw exception(WM_MATERIAL_REQUEST_STATUS_INVALID);
        }

        // 2. 非已完成 → 已取消
        materialRequestMapper.updateById(new MesWmMaterialRequestDO()
                .setId(id).setStatus(MesWmMaterialRequestStatusEnum.CANCELED.getStatus()));
    }

    @Override
    public List<MesWmMaterialRequestDO> getMaterialRequestList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return materialRequestMapper.selectByIds(ids);
    }

    private MesWmMaterialRequestDO validateMaterialRequestExists(Long id) {
        MesWmMaterialRequestDO materialRequest = materialRequestMapper.selectById(id);
        if (materialRequest == null) {
            throw exception(WM_MATERIAL_REQUEST_NOT_EXISTS);
        }
        return materialRequest;
    }

    /**
     * 校验领料申请单存在且为草稿状态
     */
    private MesWmMaterialRequestDO validateMaterialRequestExistsAndDraft(Long id) {
        MesWmMaterialRequestDO materialRequest = validateMaterialRequestExists(id);
        if (ObjUtil.notEqual(MesWmMaterialRequestStatusEnum.PREPARE.getStatus(), materialRequest.getStatus())) {
            throw exception(WM_MATERIAL_REQUEST_STATUS_INVALID);
        }
        return materialRequest;
    }

}
