package cn.iocoder.yudao.module.mes.service.cal.calendar;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.mes.controller.admin.cal.calendar.vo.MesCalCalendarListReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.calendar.vo.MesCalCalendarRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.cal.team.vo.shift.MesCalTeamShiftListReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.holiday.MesCalHolidayDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.plan.MesCalPlanDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.plan.MesCalPlanShiftDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.team.MesCalTeamDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.team.MesCalTeamMemberDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.team.MesCalTeamShiftDO;
import cn.iocoder.yudao.module.mes.dal.mysql.cal.holiday.MesCalHolidayMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.cal.plan.MesCalPlanMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.cal.team.MesCalTeamMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.cal.team.MesCalTeamMemberMapper;
import cn.iocoder.yudao.module.mes.service.cal.plan.MesCalPlanShiftService;
import cn.iocoder.yudao.module.mes.service.cal.team.MesCalTeamShiftService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MES 排班日历 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesCalCalendarServiceImpl implements MesCalCalendarService {

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Resource
    private MesCalTeamShiftService teamShiftService;
    @Resource
    private MesCalPlanShiftService planShiftService;
    @Resource
    private MesCalTeamMapper teamMapper;
    @Resource
    private MesCalTeamMemberMapper teamMemberMapper;
    @Resource
    private MesCalPlanMapper planMapper;
    @Resource
    private MesCalHolidayMapper holidayMapper;

    // TODO @AI：放到 controller 实现。这里不要有；
    @Override
    public List<MesCalCalendarRespVO> getCalendarList(MesCalCalendarListReqVO reqVO) {
        // 1. 计算月份范围
        LocalDateTime startDay = reqVO.getStartDay();
        LocalDateTime endDay = reqVO.getEndDay();
        if (startDay == null || endDay == null) {
            return Collections.emptyList();
        }

        // 2. 根据查询类型查询班组排班记录
        List<MesCalTeamShiftDO> teamShifts;
        switch (reqVO.getQueryType()) {
            // TODO @AI：枚举类，放到 MesCalCalendarListReqVO，直接使用 String 静态枚举；
            case "TYPE":
                teamShifts = getTeamShiftsByCalendarType(reqVO.getCalendarType(), startDay, endDay);
                break;
            case "TEAM":
                teamShifts = getTeamShiftsByTeamId(reqVO.getTeamId(), startDay, endDay);
                break;
            case "USER":
                teamShifts = getTeamShiftsByUserId(reqVO.getUserId(), startDay, endDay);
                break;
            default:
                return Collections.emptyList();
        }
        if (CollUtil.isEmpty(teamShifts)) {
            return Collections.emptyList();
        }

        // 3. 查询假期列表，构建假期日期集合
        Set<String> holidaySet = buildHolidaySet();

        // 4. 批量查询关联数据：班组名称、班次名称
        // TODO @AI：批量查询；service 封装
        Set<Long> teamIds = teamShifts.stream().map(MesCalTeamShiftDO::getTeamId).collect(Collectors.toSet());
        Map<Long, String> teamNameMap = teamMapper.selectBatchIds(teamIds).stream()
                .collect(Collectors.toMap(MesCalTeamDO::getId, MesCalTeamDO::getName));

        Set<Long> shiftIds = teamShifts.stream().map(MesCalTeamShiftDO::getShiftId).collect(Collectors.toSet());
        Map<Long, MesCalPlanShiftDO> shiftMap = new HashMap<>();
        if (CollUtil.isNotEmpty(shiftIds)) {
            for (Long shiftId : shiftIds) {
                MesCalPlanShiftDO shift = planShiftService.getPlanShift(shiftId);
                if (shift != null) {
                    shiftMap.put(shiftId, shift);
                }
            }
        }

        // 查询 plan 用于获取 shiftType
        // TODO @AI：批量查询；service 封装
        Set<Long> planIds = teamShifts.stream().map(MesCalTeamShiftDO::getPlanId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, MesCalPlanDO> planMap = new HashMap<>();
        if (CollUtil.isNotEmpty(planIds)) {
            planMap.putAll(planMapper.selectBatchIds(planIds).stream()
                    .collect(Collectors.toMap(MesCalPlanDO::getId, p -> p)));
        }

        // 5. 按 day 分组聚合，过滤假期
        Map<String, List<MesCalTeamShiftDO>> dayGroupMap = teamShifts.stream()
                .collect(Collectors.groupingBy(
                        ts -> ts.getDay().toLocalDate().format(DAY_FORMATTER),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<MesCalCalendarRespVO> result = new ArrayList<>();
        for (Map.Entry<String, List<MesCalTeamShiftDO>> entry : dayGroupMap.entrySet()) {
            String dayStr = entry.getKey();
            // 过滤假期
            if (holidaySet.contains(dayStr)) {
                continue;
            }
            List<MesCalTeamShiftDO> dayShifts = entry.getValue();
            // 排序
            dayShifts.sort(Comparator.comparing(ts -> ts.getSort() != null ? ts.getSort() : 0));

            // 获取 shiftType（取第一条记录的 planId 对应的 plan）
            Integer shiftType = null;
            MesCalTeamShiftDO first = dayShifts.get(0);
            if (first.getPlanId() != null) {
                MesCalPlanDO plan = planMap.get(first.getPlanId());
                if (plan != null) {
                    shiftType = plan.getShiftType();
                }
            }

            List<MesCalCalendarRespVO.TeamShiftItem> items = dayShifts.stream()
                    .map(ts -> MesCalCalendarRespVO.TeamShiftItem.builder()
                            .teamId(ts.getTeamId())
                            .teamName(teamNameMap.getOrDefault(ts.getTeamId(), ""))
                            .shiftId(ts.getShiftId())
                            .shiftName(shiftMap.containsKey(ts.getShiftId())
                                    ? shiftMap.get(ts.getShiftId()).getName() : "")
                            .sort(ts.getSort())
                            .build())
                    .collect(Collectors.toList());

            result.add(MesCalCalendarRespVO.builder()
                    .day(dayStr)
                    .shiftType(shiftType)
                    .teamShifts(items)
                    .build());
        }
        return result;
    }

    /**
     * 按班组类型查询排班记录
     */
    private List<MesCalTeamShiftDO> getTeamShiftsByCalendarType(Integer calendarType,
                                                                  LocalDateTime startDay, LocalDateTime endDay) {
        if (calendarType == null) {
            return Collections.emptyList();
        }
        // 1. 查询指定类型的所有班组
        List<MesCalTeamDO> teams = teamMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MesCalTeamDO>()
                .eq(MesCalTeamDO::getCalendarType, calendarType));
        if (CollUtil.isEmpty(teams)) {
            return Collections.emptyList();
        }
        // 2. 查询这些班组在日期范围内的排班记录
        List<MesCalTeamShiftDO> result = new ArrayList<>();
        for (MesCalTeamDO team : teams) {
            MesCalTeamShiftListReqVO reqVO = new MesCalTeamShiftListReqVO();
            reqVO.setTeamId(team.getId());
            reqVO.setStartDay(startDay);
            reqVO.setEndDay(endDay);
            result.addAll(teamShiftService.getTeamShiftList(reqVO));
        }
        return result;
    }

    /**
     * 按班组编号查询排班记录
     */
    private List<MesCalTeamShiftDO> getTeamShiftsByTeamId(Long teamId,
                                                            LocalDateTime startDay, LocalDateTime endDay) {
        if (teamId == null) {
            return Collections.emptyList();
        }
        MesCalTeamShiftListReqVO reqVO = new MesCalTeamShiftListReqVO();
        reqVO.setTeamId(teamId);
        reqVO.setStartDay(startDay);
        reqVO.setEndDay(endDay);
        return teamShiftService.getTeamShiftList(reqVO);
    }

    /**
     * 按用户编号查询排班记录（先查用户所属班组，再查班组排班）
     */
    private List<MesCalTeamShiftDO> getTeamShiftsByUserId(Long userId,
                                                            LocalDateTime startDay, LocalDateTime endDay) {
        if (userId == null) {
            return Collections.emptyList();
        }
        // 1. 查询用户所属的班组
        List<MesCalTeamMemberDO> members = teamMemberMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MesCalTeamMemberDO>()
                        .eq(MesCalTeamMemberDO::getUserId, userId));
        if (CollUtil.isEmpty(members)) {
            return Collections.emptyList();
        }
        // 2. 查询这些班组在日期范围内的排班记录
        List<MesCalTeamShiftDO> result = new ArrayList<>();
        for (MesCalTeamMemberDO member : members) {
            MesCalTeamShiftListReqVO reqVO = new MesCalTeamShiftListReqVO();
            reqVO.setTeamId(member.getTeamId());
            reqVO.setStartDay(startDay);
            reqVO.setEndDay(endDay);
            result.addAll(teamShiftService.getTeamShiftList(reqVO));
        }
        return result;
    }

    /**
     * 构建假期日期集合
     */
    private Set<String> buildHolidaySet() {
        List<MesCalHolidayDO> holidays = holidayMapper.selectList();
        Set<String> holidaySet = new HashSet<>();
        for (MesCalHolidayDO holiday : holidays) {
            // type=2 表示节假日
            if (holiday.getType() != null && holiday.getType() == 2) {
                LocalDate day = holiday.getDay().toLocalDate();
                holidaySet.add(day.format(DAY_FORMATTER));
            }
        }
        return holidaySet;
    }

}
