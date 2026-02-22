package cn.iocoder.yudao.module.mes.service.wm.materialrequest;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.MesWmMaterialRequestPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.materialrequest.vo.MesWmMaterialRequestSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.materialrequest.MesWmMaterialRequestDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.materialrequest.MesWmMaterialRequestLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.materialrequest.MesWmMaterialRequestMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

// TODO @AI：参考 /Users/yunai/Java/yudao-all-in-one/ruoyi-vue-pro/yudao-module-mes/src/main/java/cn/iocoder/yudao/module/mes/service/wm/arrivalnotice/MesWmArrivalNoticeServiceImpl.java 风格，优化代码；
/**
 * MES 领料申请单 Service 实现类
 */
@Service
@Validated
public class MesWmMaterialRequestServiceImpl implements MesWmMaterialRequestService {

    @Resource
    private MesWmMaterialRequestMapper materialRequestMapper;

    @Resource
    private MesWmMaterialRequestLineMapper materialRequestLineMapper;

    @Override
    public Long createMaterialRequest(MesWmMaterialRequestSaveReqVO createReqVO) {
        MesWmMaterialRequestDO materialRequest = BeanUtils.toBean(createReqVO, MesWmMaterialRequestDO.class);
        materialRequest.setStatus(0); // 草稿
        materialRequestMapper.insert(materialRequest);
        return materialRequest.getId();
    }

    @Override
    public void updateMaterialRequest(MesWmMaterialRequestSaveReqVO updateReqVO) {
        // 校验存在
        MesWmMaterialRequestDO materialRequest = validateMaterialRequestExists(updateReqVO.getId());
        // 只有草稿状态才允许修改
        if (!materialRequest.getStatus().equals(0)) {
            throw exception(WM_MATERIAL_REQUEST_STATUS_INVALID);
        }
        MesWmMaterialRequestDO updateObj = BeanUtils.toBean(updateReqVO, MesWmMaterialRequestDO.class);
        materialRequestMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaterialRequest(Long id) {
        // 校验存在
        MesWmMaterialRequestDO materialRequest = validateMaterialRequestExists(id);
        // 只有草稿状态才允许删除
        if (!materialRequest.getStatus().equals(0)) {
            throw exception(WM_MATERIAL_REQUEST_STATUS_INVALID);
        }
        // 级联删除行
        materialRequestLineMapper.deleteByMaterialRequestId(id);
        // 删除
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
        MesWmMaterialRequestDO materialRequest = validateMaterialRequestExists(id);
        // 0=草稿 → 1=备料中
        if (!materialRequest.getStatus().equals(0)) {
            throw exception(WM_MATERIAL_REQUEST_STATUS_INVALID);
        }
        materialRequestMapper.updateById(new MesWmMaterialRequestDO().setId(id).setStatus(1));
    }

    @Override
    public void approveMaterialRequest(Long id) {
        MesWmMaterialRequestDO materialRequest = validateMaterialRequestExists(id);
        // 1=备料中 → 2=待领料
        if (!materialRequest.getStatus().equals(1)) {
            throw exception(WM_MATERIAL_REQUEST_STATUS_INVALID);
        }
        materialRequestMapper.updateById(new MesWmMaterialRequestDO().setId(id).setStatus(2));
    }

    @Override
    public void finishMaterialRequest(Long id) {
        MesWmMaterialRequestDO materialRequest = validateMaterialRequestExists(id);
        // 2=待领料 → 3=已完成
        if (!materialRequest.getStatus().equals(2)) {
            throw exception(WM_MATERIAL_REQUEST_STATUS_INVALID);
        }
        materialRequestMapper.updateById(new MesWmMaterialRequestDO().setId(id).setStatus(3));
    }

    @Override
    public void cancelMaterialRequest(Long id) {
        MesWmMaterialRequestDO materialRequest = validateMaterialRequestExists(id);
        // 已完成不可取消
        if (materialRequest.getStatus().equals(3)) {
            throw exception(WM_MATERIAL_REQUEST_STATUS_INVALID);
        }
        materialRequestMapper.updateById(new MesWmMaterialRequestDO().setId(id).setStatus(4));
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

}
