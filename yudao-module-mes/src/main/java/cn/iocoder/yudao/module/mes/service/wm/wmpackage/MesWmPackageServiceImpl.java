package cn.iocoder.yudao.module.mes.service.wm.wmpackage;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.wmpackage.vo.MesWmPackagePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.wmpackage.vo.MesWmPackageSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.wmpackage.MesWmPackageDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.wmpackage.MesWmPackageMapper;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
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
        validateParentExists(createReqVO.getParentId());

        // 创建装箱单，默认为草稿状态
        MesWmPackageDO packageDO = BeanUtils.toBean(createReqVO, MesWmPackageDO.class);
        packageDO.setStatus(MesOrderStatusConstants.PREPARE);
        if (packageDO.getParentId() == null) {
            packageDO.setParentId(0L);
        }
        packageMapper.insert(packageDO);
        return packageDO.getId();
    }

    @Override
    public void updatePackage(MesWmPackageSaveReqVO updateReqVO) {
        // 校验存在且为草稿
        validatePackageExistsAndDraft(updateReqVO.getId());
        // 校验编码唯一性
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // TODO @AI：是不是创建一个方法，校验 parent；因为 create 和 update 都需要；
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
                .setId(id).setStatus(MesOrderStatusConstants.FINISHED));
    }

    @Override
    public List<MesWmPackageDO> getPackageTree() {
        return packageMapper.selectList();
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
        if (ObjUtil.notEqual(MesOrderStatusConstants.PREPARE, packageDO.getStatus())) {
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
