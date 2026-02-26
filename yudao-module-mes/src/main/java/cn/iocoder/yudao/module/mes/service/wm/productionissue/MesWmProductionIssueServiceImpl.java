package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssueLineSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssuePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.MesWmProductionIssueSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.item.MesMdItemMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.md.workstation.MesMdWorkstationMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.workorder.MesProWorkOrderMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue.MesWmProductionIssueLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue.MesWmProductionIssueMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.warehouse.MesWmWarehouseMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmProductionIssueStatusEnum;
import cn.iocoder.yudao.module.mes.service.wm.materialstock.MesWmMaterialStockService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCTION_ISSUE_NOT_EXISTS;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCTION_ISSUE_STATUS_INVALID;

// TODO @AI：参考别的模块，拆分多个；
/**
 * MES 领料出库单 Service 实现类
 */
@Service
@Validated
public class MesWmProductionIssueServiceImpl implements MesWmProductionIssueService {

    @Resource
    private MesWmProductionIssueMapper issueHeaderMapper;

    @Resource
    private MesWmProductionIssueLineMapper issueLineMapper;

    @Resource
    private MesWmProductionIssueLineService issueLineService;

    @Resource
    private MesWmProductionIssueDetailService issueDetailService;

    @Resource
    private MesWmWarehouseMapper warehouseMapper;

    @Resource
    private MesMdItemMapper itemMapper;

    @Resource
    private MesMdWorkstationMapper workstationMapper;

    @Resource
    private MesProWorkOrderMapper workOrderMapper;

    @Resource
    private MesWmMaterialStockService materialStockService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createIssue(MesWmProductionIssueSaveReqVO createReqVO) {
        // 1. 校验关联数据
        validateRelatedData(createReqVO);

        // 2. 插入主表
        MesWmProductionIssueDO issueHeader = BeanUtils.toBean(createReqVO, MesWmProductionIssueDO.class);
        issueHeader.setStatus(MesWmProductionIssueStatusEnum.PREPARE.getStatus());
        issueHeaderMapper.insert(issueHeader);

        // 3. 插入行表
        if (CollUtil.isNotEmpty(createReqVO.getLines())) {
            for (MesWmProductionIssueLineSaveReqVO lineVO : createReqVO.getLines()) {
                MesWmProductionIssueLineDO line = BeanUtils.toBean(lineVO, MesWmProductionIssueLineDO.class);
                line.setIssueId(issueHeader.getId());
                issueLineMapper.insert(line);
            }
        }

        return issueHeader.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIssue(MesWmProductionIssueSaveReqVO updateReqVO) {
        // 1. 校验存在 + 准备中状态
        validateIssueExistsAndPrepare(updateReqVO.getId());

        // 2. 校验关联数据
        validateRelatedData(updateReqVO);

        // 3. 更新主表
        MesWmProductionIssueDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductionIssueDO.class);
        issueHeaderMapper.updateById(updateObj);

        // 4. 更新行表：先删除旧数据，再插入新数据
        issueLineMapper.deleteByIssueId(updateReqVO.getId());
        if (CollUtil.isNotEmpty(updateReqVO.getLines())) {
            for (MesWmProductionIssueLineSaveReqVO lineVO : updateReqVO.getLines()) {
                MesWmProductionIssueLineDO line = BeanUtils.toBean(lineVO, MesWmProductionIssueLineDO.class);
                line.setIssueId(updateReqVO.getId());
                issueLineMapper.insert(line);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIssue(Long id) {
        // 1. 校验存在 + 准备中状态
        validateIssueExistsAndPrepare(id);

        // 2.1 级联删除明细
        issueDetailService.deleteIssueDetailByIssueId(id);
        // 2.2 级联删除行
        issueLineService.deleteIssueLineByIssueId(id);
        // 2.3 删除主表
        issueHeaderMapper.deleteById(id);
    }

    @Override
    public MesWmProductionIssueDO getIssue(Long id) {
        return issueHeaderMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmProductionIssueDO> getIssuePage(MesWmProductionIssuePageReqVO pageReqVO) {
        return issueHeaderMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishIssue(Long id) {
        // 1.1 校验存在
        MesWmProductionIssueDO issueHeader = validateIssueExists(id);
        // 1.2 校验状态：只有草稿才允许完成
        if (ObjUtil.notEqual(MesWmProductionIssueStatusEnum.PREPARE.getStatus(), issueHeader.getStatus())) {
            throw exception(WM_PRODUCTION_ISSUE_STATUS_INVALID);
        }

        // 2. 扣减库存（根据 line 表）
        List<MesWmProductionIssueLineDO> lines = issueLineMapper.selectByIssueId(id);
        if (CollUtil.isNotEmpty(lines)) {
            for (MesWmProductionIssueLineDO line : lines) {
                materialStockService.increaseStock(
                        line.getItemId(),
                        issueHeader.getWarehouseId(), // 使用主表的仓库ID
                        null, // locationId
                        null, // areaId
                        line.getBatchId(),
                        line.getQuantityIssued().negate(), // 负数表示扣减
                        null, // vendorId
                        null, // productionDate
                        null  // expireDate
                );
            }
        }

        // 3. 草稿 → 已完成
        issueHeaderMapper.updateById(new MesWmProductionIssueDO()
                .setId(id).setStatus(MesWmProductionIssueStatusEnum.FINISHED.getStatus()));
    }

    @Override
    public List<MesWmProductionIssueDO> getIssueList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return issueHeaderMapper.selectByIds(ids);
    }

    private MesWmProductionIssueDO validateIssueExists(Long id) {
        MesWmProductionIssueDO issueHeader = issueHeaderMapper.selectById(id);
        if (issueHeader == null) {
            throw exception(WM_PRODUCTION_ISSUE_NOT_EXISTS);
        }
        return issueHeader;
    }

    /**
     * 校验领料出库单存在且为准备中状态
     */
    private MesWmProductionIssueDO validateIssueExistsAndPrepare(Long id) {
        MesWmProductionIssueDO issueHeader = validateIssueExists(id);
        if (ObjUtil.notEqual(MesWmProductionIssueStatusEnum.PREPARE.getStatus(), issueHeader.getStatus())) {
            throw exception(WM_PRODUCTION_ISSUE_STATUS_INVALID);
        }
        return issueHeader;
    }

}
