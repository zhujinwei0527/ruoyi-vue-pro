package cn.iocoder.yudao.module.mes.service.wm.miscissue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.miscissue.vo.line.MesWmMiscIssueLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.miscissue.vo.line.MesWmMiscIssueLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.miscissue.MesWmMiscIssueLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.miscissue.MesWmMiscIssueLineMapper;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.wm.warehouse.MesWmWarehouseAreaService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 杂项出库单行 Service 实现类
 */
@Service
@Validated
public class MesWmMiscIssueLineServiceImpl implements MesWmMiscIssueLineService {

    @Resource
    private MesWmMiscIssueLineMapper miscIssueLineMapper;

    @Resource
    @Lazy
    private MesWmMiscIssueService miscIssueService;

    @Resource
    private MesMdItemService itemService;

    @Resource
    private MesWmWarehouseAreaService warehouseAreaService;

    @Override
    public Long createMiscIssueLine(MesWmMiscIssueLineSaveReqVO createReqVO) {
        // 校验父单据存在且为可编辑状态
        miscIssueService.validateMiscIssueEditable(createReqVO.getIssueId());
        // 校验物料存在
        itemService.validateItemExists(createReqVO.getItemId());
        // 校验仓库、库区、库位的父子关系
        warehouseAreaService.validateWarehouseAreaExists(createReqVO.getWarehouseId(),
                createReqVO.getLocationId(), createReqVO.getAreaId());

        // 新增
        MesWmMiscIssueLineDO line = BeanUtils.toBean(createReqVO, MesWmMiscIssueLineDO.class);
        miscIssueLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateMiscIssueLine(MesWmMiscIssueLineSaveReqVO updateReqVO) {
        // 校验存在
        MesWmMiscIssueLineDO line = validateMiscIssueLineExists(updateReqVO.getId());
        // 校验父单据存在且为可编辑状态
        miscIssueService.validateMiscIssueEditable(line.getIssueId());
        // 校验物料存在
        itemService.validateItemExists(updateReqVO.getItemId());
        // 校验仓库、库区、库位的父子关系
        warehouseAreaService.validateWarehouseAreaExists(updateReqVO.getWarehouseId(),
                updateReqVO.getLocationId(), updateReqVO.getAreaId());

        // 更新
        MesWmMiscIssueLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmMiscIssueLineDO.class);
        miscIssueLineMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMiscIssueLine(Long id) {
        // 校验存在
        validateMiscIssueLineExists(id);

        // 删除
        miscIssueLineMapper.deleteById(id);
    }

    @Override
    public MesWmMiscIssueLineDO getMiscIssueLine(Long id) {
        return miscIssueLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmMiscIssueLineDO> getMiscIssueLinePage(MesWmMiscIssueLinePageReqVO pageReqVO) {
        return miscIssueLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmMiscIssueLineDO> getMiscIssueLineListByIssueId(Long issueId) {
        return miscIssueLineMapper.selectListByIssueId(issueId);
    }

    @Override
    public void deleteMiscIssueLineByIssueId(Long issueId) {
        miscIssueLineMapper.deleteByIssueId(issueId);
    }

    private MesWmMiscIssueLineDO validateMiscIssueLineExists(Long id) {
        MesWmMiscIssueLineDO line = miscIssueLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_MISC_ISSUE_LINE_NOT_EXISTS);
        }
        return line;
    }

}
