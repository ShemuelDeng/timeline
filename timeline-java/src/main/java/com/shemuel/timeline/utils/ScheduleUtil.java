package com.shemuel.timeline.utils;

import com.shemuel.timeline.common.RepeatType;
import com.shemuel.timeline.entity.TUserReminder;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * @Author: dengshaoxiang
 * @Date: 2025-11-13-15:01
 * @Description:
 */
public class ScheduleUtil {
    private static final long day_seconds = 24 * 60 * 60 * 1000L;

    /**
     * 根据题型类型获取下次提醒时间
     * @param remind
     * @return
     */
    /**
     * 根据重复类型获取下次提醒时间（毫秒级 13 位时间戳）
     */
    public static Long getNextRemindTime(TUserReminder remind) {
        if (remind == null) {
            return null;
        }

        String repeatType = remind.getRepeatRule();
        LocalDateTime remindTime = remind.getRemindTime();
        if (remindTime == null) {
            return null;
        }

        switch (repeatType) {

            case RepeatType.NONE:
                // 不重复
                return null;

            case RepeatType.DAILY:
                // 每日
                return DateUtil.toTimestamp(remindTime.plusDays(1));

            case RepeatType.WEEKLY:
                // 每周
                return DateUtil.toTimestamp(remindTime.plusWeeks(1));

            case RepeatType.WORKDAY:
                // 工作日：跳到下一个工作日
                LocalDateTime next = remindTime.plusDays(1);
                DayOfWeek day = next.getDayOfWeek();
                if (day == DayOfWeek.SATURDAY) {
                    next = next.plusDays(2); // 跳到周一
                } else if (day == DayOfWeek.SUNDAY) {
                    next = next.plusDays(1); // 跳到周一
                }
                return DateUtil.toTimestamp(next);

            case RepeatType.MONTHLY:
                // 每月：同一天
                return DateUtil.toTimestamp(remindTime.plusMonths(1));

            case RepeatType.YEARLY:
                // 每年：同一天
                return DateUtil.toTimestamp(remindTime.plusYears(1));

            default:
                // 未知类型
                return null;
        }
    }

}
