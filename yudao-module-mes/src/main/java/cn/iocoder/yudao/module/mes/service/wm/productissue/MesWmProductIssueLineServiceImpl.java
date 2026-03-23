package cn.iocoder.yudao.module.mes.service.wm.productissue;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productissue.vo.line.MesWmProductIssueLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productissue.vo.line.MesWmProductIssueLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.workorder.MesProWorkOrderBomDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productissue.MesWmProductIssueDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productissue.MesWmProductIssueLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productissue.MesWmProductIssueLineMapper;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.pro.workorder.MesProWorkOrderBomService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCT_ISSUE_LINE_ITEM_NOT_IN_BOM;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCT_ISSUE_LINE_NOT_EXISTS;

/**
 * MES 领料出库单行 Service 实现类
 */
@Service
@Validated
public class MesWmProductIssueLineServiceImpl implements MesWmProductIssueLineService {

    @Resource
    private MesWmProductIssueLineMapper issueLineMapper;

    @Resource
    @Lazy
    private MesWmProductIssueService issueService;
    @Resource
    private MesMdItemService itemService;
    @Resource
    private MesProWorkOrderBomService workOrderBomService;

    @Override
    public Long createProductIssueLine(MesWmProductIssueLineSaveReqVO createReqVO) {
        // 校验父数据存在 + 校验物料在工单 BOM 中
        validateItemInWorkOrderBom(createReqVO.getIssueId(), createReqVO.getItemId());
        // 校验物料存在
        itemService.validateItemExists(createReqVO.getItemId());

        // 插入
        MesWmProductIssueLineDO line = BeanUtils.toBean(createReqVO, MesWmProductIssueLineDO.class);
        issueLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateProductIssueLine(MesWmProductIssueLineSaveReqVO updateReqVO) {
        // 校验存在
        validateProductIssueLineExists(updateReqVO.getId());
        // 校验父数据存在 + 校验物料在工单 BOM 中
        validateItemInWorkOrderBom(updateReqVO.getIssueId(), updateReqVO.getItemId());
        // 校验物料存在
        itemService.validateItemExists(updateReqVO.getItemId());

        // 更新
        MesWmProductIssueLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductIssueLineDO.class);
        issueLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteProductIssueLine(Long id) {
        // 校验存在
        validateProductIssueLineExists(id);
        // 删除
        issueLineMapper.deleteById(id);
    }

    @Override
    public MesWmProductIssueLineDO getProductIssueLine(Long id) {
        return issueLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmProductIssueLineDO> getProductIssueLinePage(MesWmProductIssueLinePageReqVO pageReqVO) {
        return issueLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmProductIssueLineDO> getProductIssueLineListByIssueId(Long issueId) {
        return issueLineMapper.selectListByIssueId(issueId);
    }

    @Override
    public void deleteProductIssueLineByIssueId(Long issueId) {
        issueLineMapper.deleteByIssueId(issueId);
    }

    @Override
    public MesWmProductIssueLineDO validateProductIssueLineExists(Long id) {
        MesWmProductIssueLineDO line = issueLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_PRODUCT_ISSUE_LINE_NOT_EXISTS);
        }
        return line;
    }

    private void validateItemInWorkOrderBom(Long issueId, Long itemId) {
        // 校验领料单存在，并获取工单编号
        MesWmProductIssueDO issue = issueService.validateProductIssueExists(issueId);
        // 校验物料是否在工单 BOM 中（防错料）
        List<MesProWorkOrderBomDO> workOrderBoms = workOrderBomService.getWorkOrderBomListByWorkOrderId(issue.getWorkOrderId());
        if (CollUtil.isEmpty(workOrderBoms)) {
            return;
        }
        MesProWorkOrderBomDO workOrderBom = CollUtil.findOne(workOrderBoms,
                bom -> bom.getItemId().equals(itemId));
        if (workOrderBom == null) {
            throw exception(WM_PRODUCT_ISSUE_LINE_ITEM_NOT_IN_BOM);
        }
    }

}
