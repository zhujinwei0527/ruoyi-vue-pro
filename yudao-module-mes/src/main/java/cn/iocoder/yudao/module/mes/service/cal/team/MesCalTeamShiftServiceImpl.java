package cn.iocoder.yudao.module.mes.service.cal.team;

import cn.iocoder.yudao.module.mes.controller.admin.cal.team.vo.shift.MesCalTeamShiftListReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.plan.MesCalPlanDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.plan.MesCalPlanShiftDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.plan.MesCalPlanTeamDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.cal.team.MesCalTeamShiftDO;
import cn.iocoder.yudao.module.mes.dal.mysql.cal.team.MesCalTeamShiftMapper;
import cn.iocoder.yudao.module.mes.enums.cal.MesCalShiftMethodEnum;
import cn.iocoder.yudao.module.mes.enums.cal.MesCalShiftTypeEnum;
import cn.iocoder.yudao.module.mes.service.cal.plan.MesCalPlanService;
import cn.iocoder.yudao.module.mes.service.cal.plan.MesCalPlanShiftService;
import cn.iocoder.yudao.module.mes.service.cal.plan.MesCalPlanTeamService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * MES 班组排班 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesCalTeamShiftServiceImpl implements MesCalTeamShiftService {

    @Resource
    private MesCalTeamShiftMapper teamShiftMapper;
    @Resource
    @Lazy
    private MesCalPlanService planService;
    @Resource
    @Lazy
    private MesCalPlanShiftService planShiftService;
    @Resource
    @Lazy
    private MesCalPlanTeamService planTeamService;

    @Async
    @Override
    public void genRecords(Long planId) {
        // 1.1 查询排班计划
        MesCalPlanDO plan = planService.getPlan(planId);
        // 1.2 查询计划中的班次列表
        List<MesCalPlanShiftDO> shifts = planShiftService.getPlanShiftListByPlanId(planId);
        // 1.3 查询计划中的班组列表
        List<MesCalPlanTeamDO> teams = planTeamService.getPlanTeamListByPlanId(planId);

        // 2.1 计算日期差值（天数）
        LocalDate startDate = plan.getStartDate().toLocalDate();
        LocalDate endDate = plan.getEndDate().toLocalDate();
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        // TODO @AI：dateutils，还是 localdateutils 应该有方法；或者 hutool 的
        // 2.2 遍历每一天，生成排班记录
        int shiftIndex = 0;
        for (int i = 0; i < days; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            LocalDateTime currentDay = currentDate.atStartOfDay();
            // 2.1 根据倒班方式计算 shiftIndex
            if (MesCalShiftMethodEnum.QUARTER.getMethod().equals(plan.getShiftMethod())) {
                // 按季度轮班：到了新季度的第一天，且不是排班计划开始的季度
                LocalDate quarterStart = getQuarterStart(currentDate);
                LocalDate planQuarterStart = getQuarterStart(startDate);
                if (currentDate.equals(quarterStart) && !quarterStart.equals(planQuarterStart)) {
                    shiftIndex++;
                }
            } else if (MesCalShiftMethodEnum.MONTH.getMethod().equals(plan.getShiftMethod())) {
                // 按月轮班：到了月初，且不是排班计划开始的月份
                LocalDate monthStart = currentDate.withDayOfMonth(1);
                LocalDate planMonthStart = startDate.withDayOfMonth(1);
                if (currentDate.equals(monthStart) && !monthStart.equals(planMonthStart)) {
                    shiftIndex++;
                }
            } else if (MesCalShiftMethodEnum.WEEK.getMethod().equals(plan.getShiftMethod())) {
                // 按周轮班：到了周一，且不是排班计划开始的周
                LocalDate weekStart = getWeekStart(currentDate);
                LocalDate planWeekStart = getWeekStart(startDate);
                if (currentDate.equals(weekStart) && !weekStart.equals(planWeekStart)) {
                    shiftIndex++;
                }
            } else {
                // 按天轮班：到了指定轮班天数的倍数，且不是刚开始
                if (i % plan.getShiftCount() == 0 && i != 0) {
                    shiftIndex++;
                }
            }
            // 2.2 根据轮班方式生成排班记录
            // TODO @AI：要不要先把记录生成好，最后去存入数据库；
            // TODO @AI：逻辑写的又点乱（复杂），看看能不能在优化下；
            if (MesCalShiftTypeEnum.SINGLE.getType().equals(plan.getShiftType())) {
                // 单白班：不需要倒班
                saveTeamShift(MesCalTeamShiftDO.builder()
                        .planId(planId)
                        .day(currentDay)
                        .teamId(teams.get(0).getTeamId())
                        .shiftId(shifts.get(0).getId())
                        .build());
            } else if (MesCalShiftTypeEnum.TWO.getType().equals(plan.getShiftType())) {
                // 两班倒
                if (shiftIndex % 2 == 0) {
                    // A 组上白班
                    saveTeamShift(MesCalTeamShiftDO.builder()
                            .planId(planId).day(currentDay)
                            .teamId(teams.get(0).getTeamId())
                            .shiftId(shifts.get(0).getId())
                            .sort(1).build());
                    // B 组上夜班
                    saveTeamShift(MesCalTeamShiftDO.builder()
                            .planId(planId).day(currentDay)
                            .teamId(teams.get(1).getTeamId())
                            .shiftId(shifts.get(1).getId())
                            .sort(2).build());
                } else {
                    // A 组上夜班
                    saveTeamShift(MesCalTeamShiftDO.builder()
                            .planId(planId).day(currentDay)
                            .teamId(teams.get(0).getTeamId())
                            .shiftId(shifts.get(1).getId())
                            .sort(2).build());
                    // B 组上白班
                    saveTeamShift(MesCalTeamShiftDO.builder()
                            .planId(planId).day(currentDay)
                            .teamId(teams.get(1).getTeamId())
                            .shiftId(shifts.get(0).getId())
                            .sort(1).build());
                }
            } else if (MesCalShiftTypeEnum.THREE.getType().equals(plan.getShiftType())) {
                // 三班倒
                if (shiftIndex % 3 == 0) {
                    // A 组上白班、B 组上中班、C 组上夜班
                    saveTeamShift(MesCalTeamShiftDO.builder()
                            .planId(planId).day(currentDay)
                            .teamId(teams.get(0).getTeamId())
                            .shiftId(shifts.get(0).getId())
                            .sort(1).build());
                    saveTeamShift(MesCalTeamShiftDO.builder()
                            .planId(planId).day(currentDay)
                            .teamId(teams.get(1).getTeamId())
                            .shiftId(shifts.get(1).getId())
                            .sort(2).build());
                    saveTeamShift(MesCalTeamShiftDO.builder()
                            .planId(planId).day(currentDay)
                            .teamId(teams.get(2).getTeamId())
                            .shiftId(shifts.get(2).getId())
                            .sort(3).build());
                } else if (shiftIndex % 3 == 1) {
                    // A 组上中班、B 组上夜班、C 组上白班
                    saveTeamShift(MesCalTeamShiftDO.builder()
                            .planId(planId).day(currentDay)
                            .teamId(teams.get(0).getTeamId())
                            .shiftId(shifts.get(1).getId())
                            .sort(2).build());
                    saveTeamShift(MesCalTeamShiftDO.builder()
                            .planId(planId).day(currentDay)
                            .teamId(teams.get(1).getTeamId())
                            .shiftId(shifts.get(2).getId())
                            .sort(3).build());
                    saveTeamShift(MesCalTeamShiftDO.builder()
                            .planId(planId).day(currentDay)
                            .teamId(teams.get(2).getTeamId())
                            .shiftId(shifts.get(0).getId())
                            .sort(1).build());
                } else {
                    // A 组上夜班、B 组上白班、C 组上中班
                    saveTeamShift(MesCalTeamShiftDO.builder()
                            .planId(planId).day(currentDay)
                            .teamId(teams.get(0).getTeamId())
                            .shiftId(shifts.get(2).getId())
                            .sort(3).build());
                    saveTeamShift(MesCalTeamShiftDO.builder()
                            .planId(planId).day(currentDay)
                            .teamId(teams.get(1).getTeamId())
                            .shiftId(shifts.get(0).getId())
                            .sort(1).build());
                    saveTeamShift(MesCalTeamShiftDO.builder()
                            .planId(planId).day(currentDay)
                            .teamId(teams.get(2).getTeamId())
                            .shiftId(shifts.get(1).getId())
                            .sort(2).build());
                }
            }
        }
    }

    @Override
    public List<MesCalTeamShiftDO> getTeamShiftList(MesCalTeamShiftListReqVO reqVO) {
        return teamShiftMapper.selectList(reqVO);
    }

    @Override
    public void deleteByPlanId(Long planId) {
        teamShiftMapper.deleteByPlanId(planId);
    }

    /**
     * 保存班组排班记录（已存在则更新，不存在则新增）
     */
    private void saveTeamShift(MesCalTeamShiftDO teamShift) {
        MesCalTeamShiftDO existing = teamShiftMapper.selectByDayAndTeamId(
                teamShift.getDay(), teamShift.getTeamId());
        if (existing != null) {
            teamShift.setId(existing.getId());
            teamShiftMapper.updateById(teamShift);
        } else {
            teamShiftMapper.insert(teamShift);
        }
    }

    /**
     * 获取指定日期所在季度的第一天
     */
    // TODO @AI：看看日期工具类，有没类似的方法
    private static LocalDate getQuarterStart(LocalDate date) {
        Month month = date.getMonth();
        Month firstMonthOfQuarter = month.firstMonthOfQuarter();
        return LocalDate.of(date.getYear(), firstMonthOfQuarter, 1);
    }

    /**
     * 获取指定日期所在周的第一天（周一）
     */
    // TODO @AI：看看日期工具类，有没类似的方法
    private static LocalDate getWeekStart(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

}
