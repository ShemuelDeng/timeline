package com.shemuel.timeline.utils;

import com.shemuel.timeline.common.CustomMode;
import com.shemuel.timeline.common.RepeatRuleConst;
import com.shemuel.timeline.entity.TUserReminder;
import com.shemuel.timeline.entity.TUserReminderItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.support.CronExpression;

import java.time.*;
import java.util.Objects;


/**
 * @Author: dengshaoxiang
 * @Date: 2025-11-13-15:01
 * @Description:
 */
@Slf4j
public class ScheduleUtil {
    private static final long day_seconds = 24 * 60 * 60 * 1000L;


    /**
     * 根据主表 + 子表项，计算该子项“下一次提醒时间”
     *
     * @param main 主提醒记录（包含 repeatRule / customMode / doCircle 等）
     * @param sub  子提醒项（包含当前 remindTime / repeatRule / repeatInterval）
     * @param isForSchedule  获取下次执行时间的业务场景， 如果是更新db，false, 如果是定时任务，true
     * @return 下次提醒时间的毫秒级时间戳（13 位）；如果没有下一次则返回 null
     */
    public static Long getNextRemindTime(TUserReminder main, TUserReminderItem sub, boolean isForSchedule) {
        if (main == null || sub == null) {
            return null;
        }

        // 优先使用cron
        if (Objects.equals(main.getRepeatRule(), RepeatRuleConst.CRON) && StringUtils.isNotEmpty(main.getCronExpr())){
            return getNextByCron(main, sub, isForSchedule);
        }

        LocalDateTime next = calcNextTimeForItem(main, sub);
        if (next == null) {
            return null;
        }
        return DateUtil.toTimestamp(next);
    }

    private static Long getNextByCron(TUserReminder remind, TUserReminderItem item, boolean isForSchedule) {
        String cronExpr = remind.getCronExpr();
        if (cronExpr == null || cronExpr.trim().isEmpty()) {
            return null; // 没有配置 cron，就当算不出下一次
        }

        CronExpression cron;
        try {
            cron = CronExpression.parse(cronExpr);
        } catch (Exception e) {
            // 表达式非法，按无下一次处理
            log.error("Cron表达式非法：{}", cronExpr);
            return null;
        }

        LocalDateTime base = LocalDateTime.now();
        LocalDateTime cronStart = remind.getCronStartTime();

        if (!isForSchedule && cronStart != null && base.isBefore(cronStart)) {
            base = cronStart;
        }

        LocalDateTime next = cron.next(base);

        log.info("cron next  base {}, next :{}", DateUtil.formatLocalDateTime(base, "yyyy-MM-dd HH:mm:ss"), DateUtil.formatLocalDateTime(next, "yyyy-MM-dd HH:mm:ss"));
        if (next == null) {
            return null; // 没有下一次了
        }

        // 可选：如果你支持结束时间，可以在这里判断一下
        if (remind.getCronEndTime() != null && next.isAfter(remind.getCronEndTime())) {
            return null;
        }

        return next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    /**
     * 计算单个子项的“下一次提醒时间”
     *
     * 设计要点：
     *  1. 每个子项都是一条独立时间线，基于自己的 remindTime 往后推
     *  2. 支持所有 repeatRule：
     *      - NONE      : 单次提醒
     *      - DAILY     : 每 N 天
     *      - WEEKLY    : 每 N 周
     *      - WORKDAY   : 工作日
     *      - WEEKEND   : 周末（每周同一天）
     *      - MONTHLY   : 每 N 月
     *      - YEARLY    : 每 N 年
     *      - CUSTOM    : 目前只支持 BIRTHDAY / ANNIVERSARY，当作 YEARLY
     *  3. 支持“循环提醒”：
     *      - main.doCircle = 1 且 circleBegin / circleEnd / circleInterval 有效
     *      - 当天在 [circleBegin, circleEnd] 内每隔 circleInterval 分钟响一次
     *      - 当天循环结束后，根据 repeatRule 跳到下一天/下周/下月/下一年 的 circleBegin
     */
    private static LocalDateTime calcNextTimeForItem(TUserReminder main, TUserReminderItem sub) {
        LocalDateTime t = sub.getRemindTime();
        if (t == null) {
            return null;
        }

        // 1. 确定重复类型：优先子项，其次主表
        String repeatType = sub.getRepeatRule();
        if (repeatType == null || repeatType.isEmpty()) {
            repeatType = main.getRepeatRule();
        }
        if (repeatType == null || repeatType.isEmpty()) {
            // 没写就当单次
            return null;
        }

        // 2. 处理自定义模式（生日 / 纪念日 -> YEARLY）
        if (RepeatRuleConst.CUSTOM.equals(repeatType)) {
            String customMode = main.getCustomMode();
            if (CustomMode.BIRTHDAY.equals(customMode) || CustomMode.ANNIVERSARY.equals(customMode)) {
                repeatType = RepeatRuleConst.YEARLY;
            } else {
                // 其他自定义先当单次
                return null;
            }
        }

        // 3. interval：优先子项，其次主表，默认 1
        Integer intervalObj = sub.getRepeatInterval();
        if (intervalObj == null || intervalObj <= 0) {
            intervalObj = main.getRepeatInterval();
        }
        int interval = (intervalObj == null || intervalObj <= 0) ? 1 : intervalObj;

        // 4. 是否开启循环
        boolean circleOn = main.getDoCircle() != null
                && main.getDoCircle() == 1
                && main.getCircleBegin() != null
                && main.getCircleEnd() != null
                && main.getCircleInterval() != null
                && main.getCircleInterval() > 0;

        if (circleOn) {
            return calcNextTimeWithCircle(main, sub, repeatType, interval);
        }

        // 5. 普通重复（不带循环）
        switch (repeatType) {
            case RepeatRuleConst.NONE:
                // 单次提醒：主/子都不再重复
                return null;

            case RepeatRuleConst.DAILY:
                return t.plusDays(interval);

            case RepeatRuleConst.WEEKLY:
                return t.plusWeeks(interval);

            case RepeatRuleConst.WORKDAY:
                // 简单版本：每个工作日（不看 interval），如果你未来需要“每隔 N 个工作日”，可以再扩展
                return nextWorkday(t);

            case RepeatRuleConst.WEEKEND:
                // 周末子项建议分别两条：周六一条 / 周日一条
                return t.plusWeeks(interval);

            case RepeatRuleConst.MONTHLY:
                return t.plusMonths(interval);

            case RepeatRuleConst.YEARLY:
                return t.plusYears(interval);

            default:
                // 未知类型，先当单次
                return null;
        }
    }

    /**
     * 带“循环提醒”的情况：
     *  - 当前这条子记录刚在 sub.remindTime 被执行
     *  - 先尝试：同一天在 circle 窗口内的下一次
     *  - 如果当天循环结束，再按重复类型跳到下一个周期的 circleBegin
     */
    private static LocalDateTime calcNextTimeWithCircle(TUserReminder main,
                                                        TUserReminderItem sub,
                                                        String repeatType,
                                                        int interval) {
        LocalDateTime curr = sub.getRemindTime();    // 刚刚执行的时间
        LocalDate currDate = curr.toLocalDate();

        LocalTime circleBeginTime = main.getCircleBegin().toLocalTime();
        LocalTime circleEndTime = main.getCircleEnd().toLocalTime();
        int circleMinutes = main.getCircleInterval();

        // 1. 今天窗口内：尝试下一个时间点
        LocalDateTime sameDayNext = curr.plusMinutes(circleMinutes);

        while (sameDayNext.isBefore(LocalDateTime.now())){
            log.info("时间为过去时间，自动增加interval {}", circleMinutes);
            sameDayNext = sameDayNext.plusMinutes(circleMinutes);
        }


        boolean sameDay = sameDayNext.toLocalDate().isEqual(currDate);
        boolean withinWindow = !sameDayNext.toLocalTime().isAfter(circleEndTime);

        if (sameDay && withinWindow) {
            // 还在今天的范围内，直接用
            return sameDayNext;
        }

        // 2. 今天循环已经结束，跳到“下一个周期”的 circleBegin
        //    先定义“本周期的起点”：今天的 circleBegin
        LocalDateTime baseThisCycle = LocalDateTime.of(currDate, circleBeginTime);
        LocalDateTime nextBase;

        switch (repeatType) {
            case RepeatRuleConst.NONE:
                // 单次 + 循环：今天的循环结束就没了
                return null;

            case RepeatRuleConst.DAILY:
                nextBase = baseThisCycle.plusDays(interval);
                break;

            case RepeatRuleConst.WEEKLY:
            case RepeatRuleConst.WEEKEND:
                nextBase = baseThisCycle.plusWeeks(interval);
                break;

            case RepeatRuleConst.WORKDAY:
                // 工作日 + 循环：今天循环结束后，跳到下一个工作日的 circleBegin
                nextBase = nextWorkday(baseThisCycle);
                break;

            case RepeatRuleConst.MONTHLY:
                nextBase = baseThisCycle.plusMonths(interval);
                break;

            case RepeatRuleConst.YEARLY:
                nextBase = baseThisCycle.plusYears(interval);
                break;

            default:
                // 未知类型，当作单次
                return null;
        }

        return nextBase;
    }


    /**
     * 获取下一个“工作日”的同一时间（周一~周五）
     * 例如：如果传入周五 09:00，则返回下周一 09:00
     */
    private static LocalDateTime nextWorkday(LocalDateTime time) {
        LocalDateTime t = time.plusDays(1);
        DayOfWeek day = t.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY) {
            t = t.plusDays(2);
        } else if (day == DayOfWeek.SUNDAY) {
            t = t.plusDays(1);
        }
        return t;
    }


}
