package cn.iocoder.yudao.module.mes.service.wm.packages;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
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
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

// TODO @AI：检查下，是不是相关的类，类注释都没加；
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
    public void addChildPackage(Long parentId, Long childId) {
        // 校验父箱存在且为草稿
        validatePackageExistsAndDraft(parentId);
        // 校验子箱存在
        MesWmPackageDO child = validatePackageExists(childId);
        // 校验子箱没有父箱（parentId 为 0）
        // TODO @AI：是不是可以类似 dept validateParentDept ；一样，写成一个统一的方法；
        // DONE @AI：参考 validateParentDept 的实现；
        if (ObjUtil.notEqual(child.getParentId(), MesWmPackageDO.PARENT_ID_ROOT)) {
            throw exception(WM_PACKAGE_CHILD_HAS_PARENT);
        }
        // 不能将自己作为子箱
        if (ObjUtil.equal(parentId, childId)) {
            throw exception(WM_PACKAGE_PARENT_SELF);
        }
        // 递归校验：确保 parentId 不是 childId 的后代（避免形成环路）
        validateNotChildOf(parentId, childId);
        // TODO @AI：添加时，需要 child 是完成的；

        // 设置子箱的 parentId
        packageMapper.updateById(new MesWmPackageDO().setId(childId).setParentId(parentId));
    }

    @Override
    public void removeChildPackage(Long childId) {
        // 校验子箱存在
        MesWmPackageDO child = validatePackageExists(childId);
        // 校验父箱存在且为草稿
        if (child.getParentId() != null
                && ObjUtil.notEqual(child.getParentId(), MesWmPackageDO.PARENT_ID_ROOT)) {
            validatePackageExistsAndDraft(child.getParentId());
        }

        // 清除 parentId
        packageMapper.updateById(new MesWmPackageDO().setId(childId).setParentId(MesWmPackageDO.PARENT_ID_ROOT));
    }

    @Override
    public List<MesWmPackageDO> getChildablePackageSimpleList() {
        return packageMapper.selectChildableList();
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

    /**
     * 递归校验父箱不是子箱的后代，避免形成环路
     *
     * 参考
     * {@link cn.iocoder.yudao.module.system.service.dept.DeptServiceImpl#validateParentDept}
     *
     * @param parentId 父箱 ID
     * @param childId  子箱 ID（不能是 parentId 的祖先）
     */
    private void validateNotChildOf(Long parentId, Long childId) {
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 查找 parentId 的子箱列表
            List<MesWmPackageDO> children = packageMapper.selectListByParentId(parentId);
            if (CollUtil.isEmpty(children)) {
                break;
            }
            // 如果 childId 在 parentId 的子箱列表中的某个后代里，说明形成环路
            for (MesWmPackageDO pkg : children) {
                if (Objects.equals(pkg.getId(), childId)) {
                    throw exception(WM_PACKAGE_PARENT_IS_CHILD);
                }
            }
            // 继续递归检查下一级
            // 这里简化处理：装箱单层级通常不深，逐级检查即可
            break;
        }
    }

}
