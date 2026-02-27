package cn.iocoder.yudao.module.mes.service.wm.productionissue;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.line.MesWmProductionIssueLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productionissue.vo.line.MesWmProductionIssueLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderBomDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productionissue.MesWmProductionIssueLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productionissue.MesWmProductionIssueLineMapper;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderBomService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCTION_ISSUE_LINE_ITEM_NOT_IN_BOM;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCTION_ISSUE_LINE_NOT_EXISTS;

/**
 * MES 领料出库单行 Service 实现类
 */
@Service
@Validated
public class MesWmProductionIssueLineServiceImpl implements MesWmProductionIssueLineService {

    @Resource
    private MesWmProductionIssueLineMapper issueLineMapper;

    @Resource
    @Lazy
    private MesWmProductionIssueService issueService;
    @Resource
    private MesMdItemService itemService;
    @Resource
    private MesProWorkOrderBomService workOrderBomService;

    @Override
    public Long createProductionIssueLine(MesWmProductionIssueLineSaveReqVO createReqVO) {
        // 校验父数据存在 + 校验物料在工单 BOM 中
        validateItemInWorkOrderBom(createReqVO.getIssueId(), createReqVO.getItemId());
        // 校验物料存在
        itemService.validateItemExists(createReqVO.getItemId());

        // 插入
        MesWmProductionIssueLineDO line = BeanUtils.toBean(createReqVO, MesWmProductionIssueLineDO.class);
        issueLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateProductionIssueLine(MesWmProductionIssueLineSaveReqVO updateReqVO) {
        // 校验存在
        validateProductionIssueLineExists(updateReqVO.getId());
        // 校验父数据存在 + 校验物料在工单 BOM 中
        validateItemInWorkOrderBom(updateReqVO.getIssueId(), updateReqVO.getItemId());
        // 校验物料存在
        itemService.validateItemExists(updateReqVO.getItemId());

        // 更新
        MesWmProductionIssueLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductionIssueLineDO.class);
        issueLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteProductionIssueLine(Long id) {
        // 校验存在
        validateProductionIssueLineExists(id);
        // 删除
        issueLineMapper.deleteById(id);
    }

    @Override
    public MesWmProductionIssueLineDO getProductionIssueLine(Long id) {
        return issueLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmProductionIssueLineDO> getProductionIssueLinePage(MesWmProductionIssueLinePageReqVO pageReqVO) {
        return issueLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmProductionIssueLineDO> getProductionIssueLineListByIssueId(Long issueId) {
        return issueLineMapper.selectListByIssueId(issueId);
    }

    @Override
    public void deleteProductionIssueLineByIssueId(Long issueId) {
        issueLineMapper.deleteByIssueId(issueId);
    }

    @Override
    public MesWmProductionIssueLineDO validateProductionIssueLineExists(Long id) {
        MesWmProductionIssueLineDO line = issueLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_PRODUCTION_ISSUE_LINE_NOT_EXISTS);
        }
        return line;
    }

    private void validateItemInWorkOrderBom(Long issueId, Long itemId) {
        // 校验领料单存在，并获取工单编号
        MesWmProductionIssueDO issue = issueService.validateProductionIssueExists(issueId);
        // 校验物料是否在工单 BOM 中（防错料）
        List<MesProWorkOrderBomDO> workOrderBoms = workOrderBomService.getWorkOrderBomListByWorkOrderId(issue.getWorkOrderId());
        if (CollUtil.isEmpty(workOrderBoms)) {
            return;
        }
        MesProWorkOrderBomDO workOrderBom = CollUtil.findOne(workOrderBoms,
                bom -> bom.getItemId().equals(itemId));
        if (workOrderBom == null) {
            throw exception(WM_PRODUCTION_ISSUE_LINE_ITEM_NOT_IN_BOM);
        }
    }

}
