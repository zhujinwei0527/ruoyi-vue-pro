package cn.iocoder.yudao.module.mes.service.wm.packages;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.packages.vo.MesWmPackagePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.packages.vo.MesWmPackageSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.packages.MesWmPackageDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.packages.MesWmPackageMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmPackageStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

@Service
@Validated
public class MesWmPackageServiceImpl implements MesWmPackageService {

    @Resource
    private MesWmPackageMapper packageMapper;
    @Resource
    private MesWmPackageLineService packageLineService;

    @Override
    public Long createPackage(MesWmPackageSaveReqVO createReqVO) {
        // 校验编码唯一性
        validateCodeUnique(null, createReqVO.getCode());
        // 校验父箱存在
        // TODO @AI：parentId 校验都去掉，不传递了；
        validateParentExists(createReqVO.getParentId());

        // 创建装箱单，默认为草稿状态
        MesWmPackageDO packageDO = BeanUtils.toBean(createReqVO, MesWmPackageDO.class);
        packageDO.setStatus(MesWmPackageStatusEnum.PREPARE.getStatus())
                .setParentId(MesWmPackageDO.PARENT_ID_ROOT);
        packageMapper.insert(packageDO);
        return packageDO.getId();
    }

    @Override
    public void updatePackage(MesWmPackageSaveReqVO updateReqVO) {
        // 校验存在且为草稿
        validatePackageExistsAndDraft(updateReqVO.getId());
        // 校验编码唯一性
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // TODO @AI：parentId 校验都去掉，不传递了；
        // DONE @AI：是不是创建一个方法，校验 parent；因为 create 和 update 都需要；
        // 校验父箱存在，且不能选自己或子节点
        validateParentExists(updateReqVO.getParentId());
        if (updateReqVO.getParentId() != null && ObjUtil.equal(updateReqVO.getParentId(), updateReqVO.getId())) {
            throw exception(WM_PACKAGE_PARENT_SELF);
        }

        // 更新装箱单
        MesWmPackageDO updateObj = BeanUtils.toBean(updateReqVO, MesWmPackageDO.class);
        packageMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePackage(Long id) {
        // 校验存在且为草稿
        validatePackageExistsAndDraft(id);

        // 删除装箱明细
        packageLineService.deletePackageLineByPackageId(id);
        // 删除装箱单
        packageMapper.deleteById(id);

        // 级联删除子箱
        List<MesWmPackageDO> children = packageMapper.selectListByParentId(id);
        if (CollUtil.isNotEmpty(children)) {
            for (MesWmPackageDO child : children) {
                deletePackage(child.getId());
            }
        }
    }

    @Override
    public MesWmPackageDO getPackage(Long id) {
        return packageMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmPackageDO> getPackagePage(MesWmPackagePageReqVO pageReqVO) {
        return packageMapper.selectPage(pageReqVO);
    }

    @Override
    public void finishPackage(Long id) {
        // 校验存在且为草稿
        validatePackageExistsAndDraft(id);

        // 更新状态为已完成
        packageMapper.updateById(new MesWmPackageDO()
                .setId(id).setStatus(MesWmPackageStatusEnum.FINISHED.getStatus()));
    }

    @Override
    public void validatePackageStatusDraft(Long packageId) {
        validatePackageExistsAndDraft(packageId);
    }

    @Override
    public void addSubPackage(Long parentId, Long childId) {
        // 校验父箱存在且为草稿
        validatePackageExistsAndDraft(parentId);
        // 校验子箱存在
        MesWmPackageDO child = validatePackageExists(childId);
        // TODO @AI：参考 validateParentDept 的实现；
        // 校验子箱没有父箱（parentId 为 0 或 null）
        if (child.getParentId() != null && child.getParentId() != 0L) {
            throw exception(WM_PACKAGE_CHILD_HAS_PARENT);
        }
        // 不能将自己作为子箱
        if (ObjUtil.equal(parentId, childId)) {
            throw exception(WM_PACKAGE_PARENT_SELF);
        }

        // 设置子箱的 parentId
        packageMapper.updateById(new MesWmPackageDO().setId(childId).setParentId(parentId));
    }

    @Override
    public void removeSubPackage(Long childId) {
        // 校验子箱存在
        MesWmPackageDO child = validatePackageExists(childId);
        // 校验父箱存在且为草稿
        // TODO @AI：notEquals（null， 和 MesWmPackageDO.PARENT_ID_ROOT）
        if (child.getParentId() != null && child.getParentId() != 0L) {
            validatePackageExistsAndDraft(child.getParentId());
        }

        // 清除 parentId
        packageMapper.updateById(new MesWmPackageDO().setId(childId).setParentId(MesWmPackageDO.PARENT_ID_ROOT));
    }

    @Override
    public List<MesWmPackageDO> getPackageSimpleList() {
        return packageMapper.selectList(new LambdaQueryWrapperX<MesWmPackageDO>()
                // TODO @AI：这些不要直接写在 service 里，要和 mybatis plus 解耦；通过条件传递下去；
                .eq(MesWmPackageDO::getParentId, 0L)
                .eq(MesWmPackageDO::getStatus, MesWmPackageStatusEnum.FINISHED.getStatus())
                .orderByDesc(MesWmPackageDO::getId));
    }

    // ========== 校验方法 ==========

    private MesWmPackageDO validatePackageExists(Long id) {
        MesWmPackageDO packageDO = packageMapper.selectById(id);
        if (packageDO == null) {
            throw exception(WM_PACKAGE_NOT_EXISTS);
        }
        return packageDO;
    }

    private MesWmPackageDO validatePackageExistsAndDraft(Long id) {
        MesWmPackageDO packageDO = validatePackageExists(id);
        if (ObjUtil.notEqual(MesWmPackageStatusEnum.PREPARE.getStatus(), packageDO.getStatus())) {
            throw exception(WM_PACKAGE_STATUS_NOT_PREPARE);
        }
        return packageDO;
    }

    private void validateCodeUnique(Long id, String code) {
        MesWmPackageDO packageDO = packageMapper.selectByCode(code);
        if (packageDO == null) {
            return;
        }
        if (ObjUtil.notEqual(id, packageDO.getId())) {
            throw exception(WM_PACKAGE_CODE_DUPLICATE);
        }
    }

    private void validateParentExists(Long parentId) {
        if (parentId == null || parentId == 0L) {
            return;
        }
        MesWmPackageDO parent = packageMapper.selectById(parentId);
        if (parent == null) {
            throw exception(WM_PACKAGE_PARENT_NOT_EXISTS);
        }
    }

}
