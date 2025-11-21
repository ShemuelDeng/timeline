package com.shemuel.timeline.utils;

import com.shemuel.timeline.common.CustomMode;
import com.shemuel.timeline.common.RepeatType;
import com.shemuel.timeline.entity.TUserReminder;
import com.shemuel.timeline.entity.TUserReminderItem;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

/**
 * @Author: dengshaoxiang
 * @Date: 2025-11-13-15:01
 * @Description:
 */
public class ScheduleUtil {
    private static final long day_seconds = 24 * 60 * 60 * 1000L;

    /**
     * 根据主表 + 子表项，计算该子项“下一次提醒时间”
     *
     * 说明：
     *  - main：主提醒，用来兜底 repeatRule / repeatInterval / customMode 等
     *  - sub ：子提醒项，承载本次实际的 remindTime + repeatRule + repeatInterval
     *
     * 返回值：
     *  - 下次提醒时间的时间戳（毫秒，13 位）
     *  - 如果没有下一次（比如单次提醒已经过期），返回 null
     *
     * 注意：这里只负责“算时间”，不直接修改 sub.remindTime，
     *       是否把结果写回数据库由调用方决定。
     */
    public static Long getNextRemindTime(TUserReminder main, TUserReminderItem sub) {
        if (main == null || sub == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = calcNextTimeForItem(main, sub, now);
        if (next == null) {
            return null;
        }
        return DateUtil.toTimestamp(next);
    }

    /**
     * 计算单个子项的“下一个提醒时间”
     *
     * 规则说明（都是 **以子项为单位独立滚动**）：
     *  - NONE      : 单次提醒，还没到就用原时间；过了就没有下一次（返回 null）
     *  - DAILY     : 按天滚动，remindTime += interval 天，直到 > now
     *  - WEEKLY    : 按周滚动，remindTime += interval 周，直到 > now
     *  - WORKDAY   : 仅工作日（周一~周五），每天 +1，遇到周六日跳过
     *  - WEEKEND   : 每周同一天（周六或周日），按周滚动（你如果有两天，就建两条子记录）
     *  - MONTHLY   : 按月滚动，remindTime += interval 月，直到 > now
     *  - YEARLY    : 按年滚动，remindTime += interval 年，直到 > now
     *  - CUSTOM    : 目前只支持 BIRTHDAY / ANNIVERSARY，两者都当作 YEARLY 处理
     */
    private static LocalDateTime calcNextTimeForItem(TUserReminder main,
                                                     TUserReminderItem sub,
                                                     LocalDateTime now) {

        LocalDateTime t = sub.getRemindTime();
        if (t == null) {
            return null;
        }

        // 1. 取本子项的 repeatRule，若为空则回退到主表的 repeatRule
        String repeatType = sub.getRepeatRule();
        if (repeatType == null || repeatType.isEmpty()) {
            repeatType = main.getRepeatRule();
        }
        if (repeatType == null || repeatType.isEmpty()) {
            // 没有配置重复规则，就当成单次提醒
            return t.isAfter(now) ? t : null;
        }

        // 2. 处理自定义模式（目前只有 BIRTHDAY / ANNIVERSARY）
        if (RepeatType.CUSTOM.equals(repeatType)) {
            String customMode = main.getCustomMode();
            if (CustomMode.BIRTHDAY.equals(customMode) || CustomMode.ANNIVERSARY.equals(customMode)) {
                // 生日 / 纪念日：本质上就是每年重复
                repeatType = RepeatType.YEARLY;
            } else {
                // 其他自定义暂不支持，当成单次提醒
                return t.isAfter(now) ? t : null;
            }
        }

        // 3. 取 interval，优先子项，其次主表，默认 1
        Integer intervalObj = sub.getRepeatInterval();
        if (intervalObj == null || intervalObj <= 0) {
            intervalObj = main.getRepeatInterval();
        }
        int interval = (intervalObj == null || intervalObj <= 0) ? 1 : intervalObj;

        switch (repeatType) {
            case RepeatType.NONE:
                // 单次提醒：如果还未到，则这就是下次；如果已经过了，则没有下次
                return t.isAfter(now) ? t : null;

            case RepeatType.DAILY:
                // 每 N 天：一直往后加 interval 天，直到 > now
                while (!t.isAfter(now)) {
                    t = t.plusDays(interval);
                }
                return t;

            case RepeatType.WEEKLY:
                // 每 N 周：一直往后加 interval 周，直到 > now
                while (!t.isAfter(now)) {
                    t = t.plusWeeks(interval);
                }
                return t;

            case RepeatType.WORKDAY:
                // 工作日：仅在周一~周五提醒
                // 这里 interval 一般为 1，如果你以后要“每 2 个工作日”，可以在这里扩展
                while (!t.isAfter(now)) {
                    t = t.plusDays(1);
                    DayOfWeek day = t.getDayOfWeek();
                    if (day == DayOfWeek.SATURDAY) {
                        t = t.plusDays(2); // 跳到周一
                    } else if (day == DayOfWeek.SUNDAY) {
                        t = t.plusDays(1); // 跳到周一
                    }
                }
                return t;

            case RepeatType.WEEKEND:
                // 周末：每周同一天（周六或周日）提醒
                // 建议你的拆分逻辑是「周六一条子记录 + 周日一条子记录」，这样这里就跟 WEEKLY 一样滚动即可
                while (!t.isAfter(now)) {
                    t = t.plusWeeks(interval);
                }
                return t;

            case RepeatType.MONTHLY:
                // 每 N 月：一直往后加 interval 月，直到 > now
                while (!t.isAfter(now)) {
                    t = t.plusMonths(interval);
                }
                return t;

            case RepeatType.YEARLY:
                // 每 N 年：一直往后加 interval 年，直到 > now
                while (!t.isAfter(now)) {
                    t = t.plusYears(interval);
                }
                return t;

            default:
                // 未知类型，兜底当成单次
                return t.isAfter(now) ? t : null;
        }
    }

}
