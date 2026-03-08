package cn.iocoder.yudao.module.mes.service.wm.wmpackage;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.wmpackage.vo.line.MesWmPackageLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.wmpackage.vo.line.MesWmPackageLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.wmpackage.MesWmPackageDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.wmpackage.MesWmPackageLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.wmpackage.MesWmPackageLineMapper;
import cn.iocoder.yudao.module.mes.enums.MesOrderStatusConstants;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

@Service
@Validated
public class MesWmPackageLineServiceImpl implements MesWmPackageLineService {

    @Resource
    private MesWmPackageLineMapper packageLineMapper;
    @Resource
    @Lazy
    private MesWmPackageService packageService;

    @Override
    public Long createPackageLine(MesWmPackageLineSaveReqVO createReqVO) {
        // TODO @AI：检查关联的 packageId（草稿）、itemId

        // 校验装箱单状态为草稿
        validatePackageStatusDraft(createReqVO.getPackageId());
        MesWmPackageLineDO line = BeanUtils.toBean(createReqVO, MesWmPackageLineDO.class);
        packageLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updatePackageLine(MesWmPackageLineSaveReqVO updateReqVO) {
        // 校验存在
        MesWmPackageLineDO line = validatePackageLineExists(updateReqVO.getId());
        // TODO @AI：检查关联的 packageId（草稿）、itemId

        // 校验装箱单状态为草稿
        validatePackageStatusDraft(line.getPackageId());
        MesWmPackageLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmPackageLineDO.class);
        packageLineMapper.updateById(updateObj);
    }

    @Override
    public void deletePackageLine(Long id) {
        // 校验存在
        MesWmPackageLineDO line = validatePackageLineExists(id);
        // 校验装箱单状态为草稿
        validatePackageStatusDraft(line.getPackageId());

        // 删除
        packageLineMapper.deleteById(id);
    }

    @Override
    public MesWmPackageLineDO getPackageLine(Long id) {
        return packageLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmPackageLineDO> getPackageLinePage(MesWmPackageLinePageReqVO pageReqVO) {
        return packageLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmPackageLineDO> getPackageLineListByPackageId(Long packageId) {
        return packageLineMapper.selectListByPackageId(packageId);
    }

    @Override
    public void deletePackageLineByPackageId(Long packageId) {
        packageLineMapper.deleteByPackageId(packageId);
    }

    // ========== 校验方法 ==========

    private MesWmPackageLineDO validatePackageLineExists(Long id) {
        MesWmPackageLineDO line = packageLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_PACKAGE_LINE_NOT_EXISTS);
        }
        return line;
    }

    // TODO @AI：这个方法，应该 packageService 实现；
    private void validatePackageStatusDraft(Long packageId) {
        MesWmPackageDO packageDO = packageService.getPackage(packageId);
        if (packageDO == null) {
            throw exception(WM_PACKAGE_NOT_EXISTS);
        }
        if (ObjUtil.notEqual(MesOrderStatusConstants.PREPARE, packageDO.getStatus())) {
            throw exception(WM_PACKAGE_STATUS_NOT_PREPARE);
        }
    }

}
