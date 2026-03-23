package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssuePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssueSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDetailDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue.MesWmProductionIssueMapper;
import cn.iocoder.yudao.module.mes.enums.MesBizTypeConstants;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmProductionIssueStatusEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmTransactionTypeEnum;
import cn.iocoder.yudao.module.mes.service.md.workstation.MesMdWorkstationService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderService;
import cn.iocoder.yudao.module.mes.service.wm.transaction.MesWmTransactionService;
import cn.iocoder.yudao.module.mes.service.wm.transaction.dto.MesWmTransactionSaveReqDTO;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseAreaService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseLocationService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseService;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 领料出库单 Service 实现类
 */
@Service
@Validated
public class MesWmProductionIssueServiceImpl implements MesWmProductionIssueService {

    @Resource
    private MesWmProductionIssueMapper issueMapper;

    @Resource
    private MesWmProductionIssueLineService issueLineService;
    @Resource
    private MesWmProductionIssueDetailService issueDetailService;
    @Resource
    private MesMdWorkstationService workstationService;
    @Resource
    private MesProWorkOrderService workOrderService;
    @Resource
    private MesWmTransactionService wmTransactionService;
    @Resource
    private MesWmWarehouseService warehouseService;
    @Resource
    private MesWmWarehouseLocationService locationService;
    @Resource
    private MesWmWarehouseAreaService areaService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProductionIssue(MesWmProductionIssueSaveReqVO createReqVO) {
        // 1. 校验关联数据
        workOrderService.validateWorkOrderExists(createReqVO.getWorkOrderId());
        if (createReqVO.getWorkstationId() != null) {
            workstationService.validateWorkstationExists(createReqVO.getWorkstationId());
        }

        // 2. 插入主表
        MesWmProductionIssueDO issue = BeanUtils.toBean(createReqVO, MesWmProductionIssueDO.class);
        issue.setStatus(MesWmProductionIssueStatusEnum.PREPARE.getStatus());
        issueMapper.insert(issue);
        return issue.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductionIssue(MesWmProductionIssueSaveReqVO updateReqVO) {
        // 1.1 校验存在 + 准备中状态
        validateProductionIssueExistsAndPrepare(updateReqVO.getId());
        // 1.2 校验关联数据
        workOrderService.validateWorkOrderExists(updateReqVO.getWorkOrderId());
        if (updateReqVO.getWorkstationId() != null) {
            workstationService.validateWorkstationExists(updateReqVO.getWorkstationId());
        }

        // 2. 更新主表
        MesWmProductionIssueDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductionIssueDO.class);
        issueMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductionIssue(Long id) {
        // 1. 校验存在 + 准备中状态
        validateProductionIssueExistsAndPrepare(id);

        // 2.1 级联删除明细
        issueDetailService.deleteProductionIssueDetailByIssueId(id);
        // 2.2 级联删除行
        issueLineService.deleteProductionIssueLineByIssueId(id);
        // 2.3 删除主表
        issueMapper.deleteById(id);
    }

    @Override
    public MesWmProductionIssueDO getProductionIssue(Long id) {
        return issueMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmProductionIssueDO> getProductionIssuePage(MesWmProductionIssuePageReqVO pageReqVO) {
        return issueMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitProductionIssue(Long id) {
        // 校验存在 + 草稿状态
        validateProductionIssueExistsAndPrepare(id);
        // 校验至少有一条行
        List<MesWmProductionIssueLineDO> lines = issueLineService.getProductionIssueLineListByIssueId(id);
        if (CollUtil.isEmpty(lines)) {
            throw exception(WM_PRODUCTION_ISSUE_NO_LINE);
        }

        // 提交（草稿 → 待拣货）
        issueMapper.updateById(new MesWmProductionIssueDO()
                .setId(id).setStatus(MesWmProductionIssueStatusEnum.APPROVING.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockProductionIssue(Long id) {
        // 校验存在
        MesWmProductionIssueDO issue = validateProductionIssueExists(id);
        if (ObjUtil.notEqual(MesWmProductionIssueStatusEnum.APPROVING.getStatus(), issue.getStatus())) {
            throw exception(WM_PRODUCTION_ISSUE_STATUS_INVALID);
        }
        // 执行拣货（待拣货 → 待执行领出）
        issueMapper.updateById(new MesWmProductionIssueDO()
                .setId(id).setStatus(MesWmProductionIssueStatusEnum.APPROVED.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishProductionIssue(Long id) {
        // 1. 校验存在
        MesWmProductionIssueDO issue = validateProductionIssueExists(id);
        if (ObjUtil.notEqual(MesWmProductionIssueStatusEnum.APPROVED.getStatus(), issue.getStatus())) {
            throw exception(WM_PRODUCTION_ISSUE_STATUS_INVALID);
        }

        // 2. 遍历所有明细，创建库存事务（扣减库存 + 记录流水）
        createTransactionList(issue);

        // 3. 更新出库单状态
        issueMapper.updateById(new MesWmProductionIssueDO()
                .setId(id).setStatus(MesWmProductionIssueStatusEnum.FINISHED.getStatus()));
    }

    private void createTransactionList(MesWmProductionIssueDO issue) {
        // 1. 查询虚拟线边库
        MesWmWarehouseDO virtualWarehouse = warehouseService.getWarehouseByCode(MesWmWarehouseDO.WIP_VIRTUAL_WAREHOUSE);
        MesWmWarehouseLocationDO virtualLocation = locationService.getWarehouseLocationByCode(MesWmWarehouseLocationDO.WIP_VIRTUAL_LOCATION);
        MesWmWarehouseAreaDO virtualArea = areaService.getWarehouseAreaByCode(MesWmWarehouseAreaDO.WIP_VIRTUAL_AREA);

        // 2. 遍历明细，每条明细产生 OUT（实际仓库扣减）+ IN（虚拟线边库增加）
        List<MesWmProductionIssueDetailDO> details = issueDetailService.getProductionIssueDetailListByIssueId(issue.getId());
        for (MesWmProductionIssueDetailDO detail : details) {
            // 2.1 先从实际仓库出库（库存减少）
            Long outTransactionId = wmTransactionService.createTransaction(new MesWmTransactionSaveReqDTO()
                    .setType(MesWmTransactionTypeEnum.OUT.getType()).setItemId(detail.getItemId())
                    .setQuantity(detail.getQuantity().negate()) // 库存减少
                    .setBatchId(detail.getBatchId()).setBatchCode(detail.getBatchCode())
                    .setWarehouseId(detail.getWarehouseId()).setLocationId(detail.getLocationId()).setAreaId(detail.getAreaId())
                    .setBizType(MesBizTypeConstants.WM_ISSUE).setBizId(issue.getId())
                    .setBizCode(issue.getCode()).setBizLineId(detail.getLineId()));
            // 2.2 再入虚拟线边库（库存增加）
            wmTransactionService.createTransaction(new MesWmTransactionSaveReqDTO()
                    .setType(MesWmTransactionTypeEnum.IN.getType()).setItemId(detail.getItemId())
                    .setQuantity(detail.getQuantity()) // 库存增加
                    .setBatchId(detail.getBatchId()).setBatchCode(detail.getBatchCode())
                    .setWarehouseId(virtualWarehouse.getId()).setLocationId(virtualLocation.getId()).setAreaId(virtualArea.getId())
                    .setBizType(MesBizTypeConstants.WM_ISSUE).setBizId(issue.getId())
                    .setBizCode(issue.getCode()).setBizLineId(detail.getLineId())
                    .setRelatedTransactionId(outTransactionId)); // 关联出库事务
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelProductionIssue(Long id) {
        // 校验存在
        MesWmProductionIssueDO issue = validateProductionIssueExists(id);
        // 已完成和已取消不允许取消
        if (ObjectUtils.equalsAny(issue.getStatus(),
                MesWmProductionIssueStatusEnum.FINISHED.getStatus(),
                MesWmProductionIssueStatusEnum.CANCELED.getStatus())) {
            throw exception(WM_PRODUCTION_ISSUE_CANCEL_NOT_ALLOWED);
        }

        // 取消
        issueMapper.updateById(new MesWmProductionIssueDO()
                .setId(id).setStatus(MesWmProductionIssueStatusEnum.CANCELED.getStatus()));
    }

    @Override
    public Boolean checkProductionIssueQuantity(Long id) {
        List<MesWmProductionIssueLineDO> lines = issueLineService.getProductionIssueLineListByIssueId(id);
        for (MesWmProductionIssueLineDO line : lines) {
            List<MesWmProductionIssueDetailDO> details = issueDetailService.getProductionIssueDetailListByLineId(line.getId());
            BigDecimal totalDetailQty = CollectionUtils.getSumValue(details,
                    MesWmProductionIssueDetailDO::getQuantity, BigDecimal::add, BigDecimal.ZERO);
            if (line.getQuantity() != null && totalDetailQty.compareTo(line.getQuantity()) != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public MesWmProductionIssueDO validateProductionIssueExists(Long id) {
        MesWmProductionIssueDO issue = issueMapper.selectById(id);
        if (issue == null) {
            throw exception(WM_PRODUCTION_ISSUE_NOT_EXISTS);
        }
        return issue;
    }

    /**
     * 校验领料出库单存在且为准备中状态
     */
    private MesWmProductionIssueDO validateProductionIssueExistsAndPrepare(Long id) {
        MesWmProductionIssueDO issue = validateProductionIssueExists(id);
        if (ObjUtil.notEqual(MesWmProductionIssueStatusEnum.PREPARE.getStatus(), issue.getStatus())) {
            throw exception(WM_PRODUCTION_ISSUE_STATUS_INVALID);
        }
        return issue;
    }

}
