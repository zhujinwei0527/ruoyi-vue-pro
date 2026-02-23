package cn.iocoder.yudao.module.mes.service.qc.pendinginspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.qc.pendinginspect.vo.MesQcPendingInspectPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.pendinginspect.vo.MesQcPendingInspectRespVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.vendor.MesMdVendorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.arrivalnotice.MesWmArrivalNoticeDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.arrivalnotice.MesWmArrivalNoticeLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.arrivalnotice.MesWmArrivalNoticeLineMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.arrivalnotice.MesWmArrivalNoticeMapper;
import cn.iocoder.yudao.module.mes.enums.MesBizTypeConstants;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcTypeEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmArrivalNoticeStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.md.unitmeasure.MesMdUnitMeasureService;
import cn.iocoder.yudao.module.mes.service.md.vendor.MesMdVendorService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

/**
 * MES 待检任务 Service 实现类
 */
@Service
@Validated
public class MesQcPendingInspectServiceImpl implements MesQcPendingInspectService {

    @Resource
    private MesWmArrivalNoticeMapper arrivalNoticeMapper;
    @Resource
    private MesWmArrivalNoticeLineMapper arrivalNoticeLineMapper;
    @Resource
    private MesMdItemService itemService;
    @Resource
    private MesMdVendorService vendorService;
    @Resource
    private MesMdUnitMeasureService unitMeasureService;

    // 【下面的 todo 先晚点改】
    // TODO @AI：是否可以在 mapper 里查询（union 汇总）然后关联数据的读取，尽量放在 controller；
    // TODO @AI：另外，如果使用 mapper 操作，尽量使用 mybatis plus 或者 mybatis plus join，减少 mapper xml；
    @Override
    public PageResult<MesQcPendingInspectRespVO> getPendingInspectPage(MesQcPendingInspectPageReqVO pageReqVO) {
        List<MesQcPendingInspectRespVO> allItems = new ArrayList<>();

        // 1. IQC 来源：到货通知单
        if (pageReqVO.getQcType() == null || Objects.equals(pageReqVO.getQcType(), MesQcTypeEnum.IQC.getType())) {
            allItems.addAll(queryIqcPendingFromArrivalNotice(pageReqVO));
        }
        // TODO 2. IPQC 来源（生产工单，WM 模块未实现）
        // TODO 3. OQC 来源（销售出库单，WM 模块未实现）
        // TODO 4. RQC 来源（退货单，WM 模块未实现）

        // 5. 按 recordTime 降序排序
        allItems.sort(Comparator.comparing(MesQcPendingInspectRespVO::getRecordTime,
                Comparator.nullsLast(Comparator.reverseOrder())));

        // 6. 内存分页
        int total = allItems.size();
        int fromIndex = (pageReqVO.getPageNo() - 1) * pageReqVO.getPageSize();
        if (fromIndex >= total) {
            return new PageResult<>(Collections.emptyList(), (long) total);
        }
        int toIndex = Math.min(fromIndex + pageReqVO.getPageSize(), total);
        return new PageResult<>(allItems.subList(fromIndex, toIndex), (long) total);
    }

    // ==================== IQC 来源：到货通知单 ====================

    private List<MesQcPendingInspectRespVO> queryIqcPendingFromArrivalNotice(MesQcPendingInspectPageReqVO pageReqVO) {
        // 1. 查询状态为"待质检"的到货通知单
        List<MesWmArrivalNoticeDO> notices = arrivalNoticeMapper.selectListByStatus(
                MesWmArrivalNoticeStatusEnum.PENDING_QC.getStatus());
        if (CollUtil.isEmpty(notices)) {
            return Collections.emptyList();
        }
        // 1.1 按 sourceDocCode 模糊过滤
        if (StrUtil.isNotBlank(pageReqVO.getSourceDocCode())) {
            notices = notices.stream()
                    .filter(n -> n.getCode().contains(pageReqVO.getSourceDocCode()))
                    .collect(Collectors.toList());
        }
        if (CollUtil.isEmpty(notices)) {
            return Collections.emptyList();
        }
        Map<Long, MesWmArrivalNoticeDO> noticeMap = convertMap(notices, MesWmArrivalNoticeDO::getId);

        // 2. 查询需要 IQC 但尚未创建检验单的行
        List<MesWmArrivalNoticeLineDO> lines = arrivalNoticeLineMapper.selectListByIqcPending(
                new ArrayList<>(noticeMap.keySet()));
        if (CollUtil.isEmpty(lines)) {
            return Collections.emptyList();
        }

        // 3. 批量查询物料
        Map<Long, MesMdItemDO> itemMap = itemService.getItemMap(convertSet(lines, MesWmArrivalNoticeLineDO::getItemId));
        // 3.1 按 itemId 精确过滤
        if (pageReqVO.getItemId() != null) {
            lines = lines.stream()
                    .filter(line -> pageReqVO.getItemId().equals(line.getItemId()))
                    .collect(Collectors.toList());
        }
        if (CollUtil.isEmpty(lines)) {
            return Collections.emptyList();
        }

        // 4. 批量查询供应商
        Map<Long, MesMdVendorDO> vendorMap = vendorService.getVendorMap(
                convertSet(notices, MesWmArrivalNoticeDO::getVendorId));
        // 5. 批量查询计量单位
        Map<Long, MesMdUnitMeasureDO> unitMeasureMap = unitMeasureService.getUnitMeasureMap(
                convertSet(itemMap.values(), MesMdItemDO::getUnitMeasureId));

        // 6. 组装 VO
        List<MesQcPendingInspectRespVO> result = new ArrayList<>(lines.size());
        for (MesWmArrivalNoticeLineDO line : lines) {
            MesWmArrivalNoticeDO notice = noticeMap.get(line.getNoticeId());
            if (notice == null) {
                continue;
            }
            MesQcPendingInspectRespVO vo = new MesQcPendingInspectRespVO();
            vo.setSourceDocId(notice.getId());
            vo.setSourceDocType(MesBizTypeConstants.WM_ARRIVAL_NOTICE);
            vo.setSourceDocCode(notice.getCode());
            vo.setSourceLineId(line.getId());
            vo.setQcType(MesQcTypeEnum.IQC.getType());
            vo.setQcTypeName(MesQcTypeEnum.IQC.getName());
            vo.setItemId(line.getItemId());
            vo.setQuantityToCheck(line.getArrivalQuantity());
            vo.setVendorId(notice.getVendorId());
            vo.setRecordTime(notice.getArrivalDate());
            // 物料信息
            MesMdItemDO item = itemMap.get(line.getItemId());
            if (item != null) {
                vo.setItemCode(item.getCode());
                vo.setItemName(item.getName());
                vo.setSpecification(item.getSpecification());
                MesMdUnitMeasureDO unit = unitMeasureMap.get(item.getUnitMeasureId());
                if (unit != null) {
                    vo.setUnitName(unit.getName());
                }
            }
            // 供应商信息
            MesMdVendorDO vendor = vendorMap.get(notice.getVendorId());
            if (vendor != null) {
                vo.setVendorName(vendor.getName());
            }
            result.add(vo);
        }
        return result;
    }
}
