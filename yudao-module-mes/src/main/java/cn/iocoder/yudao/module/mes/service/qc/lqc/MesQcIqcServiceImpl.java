package cn.iocoder.yudao.module.mes.service.qc.lqc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.MesQcIqcPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.MesQcIqcSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.template.MesQcTemplateItemDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.lqc.MesQcIqcMapper;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcIqcStatusEnum;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import cn.iocoder.yudao.module.mes.service.qc.template.MesQcTemplateService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 来料检验单（IQC） Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesQcIqcServiceImpl implements MesQcIqcService {

    @Resource
    private MesQcIqcMapper iqcMapper;
    @Resource
    private MesQcTemplateService templateService;

    @Resource
    private MesQcIqcLineService iqcLineService;
    @Resource
    private MesQcIqcDefectService iqcDefectService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createIqc(MesQcIqcSaveReqVO createReqVO) {
        // 1.1 校验编号唯一
        validateIqcCodeUnique(null, createReqVO.getCode());
        // 1.2 查找物料关联的 IQC 检测模板
        Long templateId = createReqVO.getTemplateId();
        MesQcTemplateDO template = templateService.validateTemplateExists(templateId);
        if (!CollUtil.contains(template.getTypes(), MesQcTypeEnum.IQC.getType())) {
            throw exception(QC_IQC_NO_TEMPLATE);
        }

        // 3.1 从模板的产品关联中获取检测参数（min_check_quantity、max_unqualified_quantity）
        MesQcTemplateItemDO templateItem = templateService.getTemplateItemByTemplateIdAndItemId(
                templateId, createReqVO.getItemId());
        if (templateItem != null) {
            if (createReqVO.getMinCheckQuantity() == null) {
                createReqVO.setMinCheckQuantity(templateItem.getQuantityCheck());
            }
            if (createReqVO.getMaxUnqualifiedQuantity() == null) {
                createReqVO.setMaxUnqualifiedQuantity(templateItem.getQuantityUnqualified());
            }
        }
        // 3.2 插入主表
        MesQcIqcDO iqc = BeanUtils.toBean(createReqVO, MesQcIqcDO.class);
        iqc.setStatus(MesQcIqcStatusEnum.PREPARE.getType());
        iqcMapper.insert(iqc);

        // 4. 从模板指标自动生成检验行
        iqcLineService.createLinesFromTemplate(iqc.getId(), templateId);
        return iqc.getId();
    }

    @Override
    public void updateIqc(MesQcIqcSaveReqVO updateReqVO) {
        // 1.1 校验存在
        MesQcIqcDO iqc = validateIqcExists(updateReqVO.getId());
        // 1.2 校验状态为草稿
        validatePrepareStatus(iqc, QC_IQC_ONLY_PREPARE_CAN_COMPLETE);
        // 1.3 校验编号唯一
        validateIqcCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 2. 更新
        MesQcIqcDO updateObj = BeanUtils.toBean(updateReqVO, MesQcIqcDO.class);
        iqcMapper.updateById(updateObj);
    }

    @Override
    public void completeIqc(Long id) {
        // 1.1 校验存在
        MesQcIqcDO iqc = validateIqcExists(id);
        // 1.2 校验状态为草稿
        validatePrepareStatus(iqc, QC_IQC_ONLY_PREPARE_CAN_COMPLETE);
        // 1.3 校验合格品 + 不合格品 = 检测数量
        if (iqc.getCheckQuantity() != null && iqc.getCheckQuantity() > 0) {
            int total = (iqc.getQualifiedQuantity() != null ? iqc.getQualifiedQuantity() : 0)
                    + (iqc.getUnqualifiedQuantity() != null ? iqc.getUnqualifiedQuantity() : 0);
            if (total != iqc.getCheckQuantity()) {
                throw exception(QC_IQC_QUANTITY_MISMATCH);
            }
        }

        // 2. 更新状态为已完成
        MesQcIqcDO updateObj = new MesQcIqcDO()
                .setId(id).setStatus(MesQcIqcStatusEnum.FINISHED.getType());
        iqcMapper.updateById(updateObj);

        // TODO @芋艿：WM 模块迁移后，更新到货通知单行/外协入库单行的检验结果
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIqc(Long id) {
        // 1.1 校验存在
        MesQcIqcDO iqc = validateIqcExists(id);
        // 1.2 仅草稿可删
        validatePrepareStatus(iqc, QC_IQC_ONLY_PREPARE_CAN_DELETE);

        // 2.1 删除主表
        iqcMapper.deleteById(id);
        // 2.2 级联删除行
        iqcLineService.deleteByIqcId(id);
        // 2.3 级联删除缺陷记录
        iqcDefectService.deleteByIqcId(id);
    }

    private MesQcIqcDO validateIqcExists(Long id) {
        MesQcIqcDO iqc = iqcMapper.selectById(id);
        if (iqc == null) {
            throw exception(QC_IQC_NOT_EXISTS);
        }
        return iqc;
    }

    // TODO @AI：就搞个方法，传递 id 进去；抛出不为 草稿状态的异常；然后在 update/delete/complete 方法中调用这个方法（不用专门 errorCode）
    private void validatePrepareStatus(MesQcIqcDO iqc, ErrorCode errorCode) {
        if (ObjUtil.notEqual(iqc.getStatus(), MesQcIqcStatusEnum.PREPARE.getType())) {
            throw exception(errorCode);
        }
    }

    private void validateIqcCodeUnique(Long id, String code) {
        MesQcIqcDO iqc = iqcMapper.selectByCode(code);
        if (iqc == null) {
            return;
        }
        if (ObjUtil.notEqual(iqc.getId(), id)) {
            throw exception(QC_IQC_CODE_DUPLICATE);
        }
    }

    @Override
    public MesQcIqcDO getIqc(Long id) {
        return iqcMapper.selectById(id);
    }

    @Override
    public PageResult<MesQcIqcDO> getIqcPage(MesQcIqcPageReqVO pageReqVO) {
        return iqcMapper.selectPage(pageReqVO);
    }

}
