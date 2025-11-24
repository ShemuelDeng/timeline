package com.shemuel.timeline.service.impl;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.shemuel.timeline.common.CustomMode;
import com.shemuel.timeline.common.RemindStatus;
import com.shemuel.timeline.common.RepeatType;
import com.shemuel.timeline.entity.TUserReminderItem;
import com.shemuel.timeline.exception.ServiceException;
import com.shemuel.timeline.schedule.UserRemindScheduler;
import com.shemuel.timeline.service.TUserReminderItemService;
import com.shemuel.timeline.utils.DateUtil;
import com.shemuel.timeline.utils.ScheduleUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shemuel.timeline.mapper.TUserReminderMapper;
import com.shemuel.timeline.entity.TUserReminder;
import com.shemuel.timeline.service.TUserReminderService;
import com.shemuel.timeline.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户提醒表主表， 只记录用户需要的提醒类型，方式 服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TUserReminderServiceImpl extends ServiceImpl<TUserReminderMapper, TUserReminder> implements TUserReminderService {

    @Resource
    private TUserReminderItemService tUserReminderItemService;

    /**
     * 查询用户提醒表主表， 只记录用户需要的提醒类型，方式分页列表
     */
    @Override
    public IPage<TUserReminder> selectPage(TUserReminder tUserReminder) {
        LambdaQueryWrapper<TUserReminder> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tUserReminder.getId() != null, TUserReminder::getId, tUserReminder.getId());
        wrapper.eq(tUserReminder.getUserId() != null, TUserReminder::getUserId, tUserReminder.getUserId());
        wrapper.eq(tUserReminder.getTemplateId() != null, TUserReminder::getTemplateId, tUserReminder.getTemplateId());
        wrapper.eq(tUserReminder.getTitle() != null, TUserReminder::getTitle, tUserReminder.getTitle());
        wrapper.eq(tUserReminder.getContent() != null, TUserReminder::getContent, tUserReminder.getContent());
        wrapper.eq(tUserReminder.getRemindTime() != null, TUserReminder::getRemindTime, tUserReminder.getRemindTime());
        wrapper.eq(tUserReminder.getRepeatRule() != null, TUserReminder::getRepeatRule, tUserReminder.getRepeatRule());
        wrapper.eq(tUserReminder.getCustomMode() != null, TUserReminder::getCustomMode, tUserReminder.getCustomMode());
        wrapper.eq(tUserReminder.getAdvanceDays() != null, TUserReminder::getAdvanceDays, tUserReminder.getAdvanceDays());
        wrapper.eq(tUserReminder.getRepeatInterval() != null, TUserReminder::getRepeatInterval, tUserReminder.getRepeatInterval());
        wrapper.eq(tUserReminder.getRepeatWeekdays() != null, TUserReminder::getRepeatWeekdays, tUserReminder.getRepeatWeekdays());
        wrapper.eq(tUserReminder.getRepeatMonthDays() != null, TUserReminder::getRepeatMonthDays, tUserReminder.getRepeatMonthDays());
        wrapper.eq(tUserReminder.getSpecifyDates() != null, TUserReminder::getSpecifyDates, tUserReminder.getSpecifyDates());
        wrapper.eq(tUserReminder.getSpecifyTimes() != null, TUserReminder::getSpecifyTimes, tUserReminder.getSpecifyTimes());
        wrapper.eq(tUserReminder.getStatus() != null, TUserReminder::getStatus, tUserReminder.getStatus());
        wrapper.eq(tUserReminder.getActive() != null, TUserReminder::getActive, tUserReminder.getActive());
        wrapper.eq(tUserReminder.getVisible() != null, TUserReminder::getVisible, tUserReminder.getVisible());
        wrapper.eq(tUserReminder.getDoCircle() != null, TUserReminder::getDoCircle, tUserReminder.getDoCircle());
        wrapper.eq(tUserReminder.getCircleBegin() != null, TUserReminder::getCircleBegin, tUserReminder.getCircleBegin());
        wrapper.eq(tUserReminder.getCircleEnd() != null, TUserReminder::getCircleEnd, tUserReminder.getCircleEnd());
        wrapper.eq(tUserReminder.getCircleInterval() != null, TUserReminder::getCircleInterval, tUserReminder.getCircleInterval());
        wrapper.eq(tUserReminder.getNotifyDesktop() != null, TUserReminder::getNotifyDesktop, tUserReminder.getNotifyDesktop());
        wrapper.eq(tUserReminder.getNotifyWx() != null, TUserReminder::getNotifyWx, tUserReminder.getNotifyWx());
        wrapper.eq(tUserReminder.getNotifySound() != null, TUserReminder::getNotifySound, tUserReminder.getNotifySound());
        wrapper.eq(tUserReminder.getNotifySystem() != null, TUserReminder::getNotifySystem, tUserReminder.getNotifySystem());
        wrapper.eq(tUserReminder.getNotifySoundFile() != null, TUserReminder::getNotifySoundFile, tUserReminder.getNotifySoundFile());
        wrapper.eq(tUserReminder.getCreateTime() != null, TUserReminder::getCreateTime, tUserReminder.getCreateTime());
        wrapper.eq(tUserReminder.getUpdateTime() != null, TUserReminder::getUpdateTime, tUserReminder.getUpdateTime());
        wrapper.eq(tUserReminder.getWebhook() != null, TUserReminder::getWebhook, tUserReminder.getWebhook());
        wrapper.eq(tUserReminder.getWebhookMethod() != null, TUserReminder::getWebhookMethod, tUserReminder.getWebhookMethod());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 查询用户提醒表主表， 只记录用户需要的提醒类型，方式列表
     */
    @Override
    public List<TUserReminder> selectList(TUserReminder tUserReminder) {
        LambdaQueryWrapper<TUserReminder> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tUserReminder.getId() != null, TUserReminder::getId, tUserReminder.getId());
        wrapper.eq(tUserReminder.getUserId() != null, TUserReminder::getUserId, tUserReminder.getUserId());
        wrapper.eq(tUserReminder.getTemplateId() != null, TUserReminder::getTemplateId, tUserReminder.getTemplateId());
        wrapper.eq(tUserReminder.getTitle() != null, TUserReminder::getTitle, tUserReminder.getTitle());
        wrapper.eq(tUserReminder.getContent() != null, TUserReminder::getContent, tUserReminder.getContent());
        wrapper.eq(tUserReminder.getRemindTime() != null, TUserReminder::getRemindTime, tUserReminder.getRemindTime());
        wrapper.eq(tUserReminder.getRepeatRule() != null, TUserReminder::getRepeatRule, tUserReminder.getRepeatRule());
        wrapper.eq(tUserReminder.getCustomMode() != null, TUserReminder::getCustomMode, tUserReminder.getCustomMode());
        wrapper.eq(tUserReminder.getAdvanceDays() != null, TUserReminder::getAdvanceDays, tUserReminder.getAdvanceDays());
        wrapper.eq(tUserReminder.getRepeatInterval() != null, TUserReminder::getRepeatInterval, tUserReminder.getRepeatInterval());
        wrapper.eq(tUserReminder.getRepeatWeekdays() != null, TUserReminder::getRepeatWeekdays, tUserReminder.getRepeatWeekdays());
        wrapper.eq(tUserReminder.getRepeatMonthDays() != null, TUserReminder::getRepeatMonthDays, tUserReminder.getRepeatMonthDays());
        wrapper.eq(tUserReminder.getSpecifyDates() != null, TUserReminder::getSpecifyDates, tUserReminder.getSpecifyDates());
        wrapper.eq(tUserReminder.getSpecifyTimes() != null, TUserReminder::getSpecifyTimes, tUserReminder.getSpecifyTimes());
        wrapper.eq(tUserReminder.getStatus() != null, TUserReminder::getStatus, tUserReminder.getStatus());
        wrapper.eq(tUserReminder.getActive() != null, TUserReminder::getActive, tUserReminder.getActive());
        wrapper.eq(tUserReminder.getDoCircle() != null, TUserReminder::getDoCircle, tUserReminder.getDoCircle());
        wrapper.eq(tUserReminder.getCircleBegin() != null, TUserReminder::getCircleBegin, tUserReminder.getCircleBegin());
        wrapper.eq(tUserReminder.getCircleEnd() != null, TUserReminder::getCircleEnd, tUserReminder.getCircleEnd());
        wrapper.eq(tUserReminder.getCircleInterval() != null, TUserReminder::getCircleInterval, tUserReminder.getCircleInterval());
        wrapper.eq(tUserReminder.getNotifyDesktop() != null, TUserReminder::getNotifyDesktop, tUserReminder.getNotifyDesktop());
        wrapper.eq(tUserReminder.getNotifyWx() != null, TUserReminder::getNotifyWx, tUserReminder.getNotifyWx());
        wrapper.eq(tUserReminder.getNotifySound() != null, TUserReminder::getNotifySound, tUserReminder.getNotifySound());
        wrapper.eq(tUserReminder.getNotifySystem() != null, TUserReminder::getNotifySystem, tUserReminder.getNotifySystem());
        wrapper.eq(tUserReminder.getNotifySoundFile() != null, TUserReminder::getNotifySoundFile, tUserReminder.getNotifySoundFile());
        wrapper.eq(tUserReminder.getCreateTime() != null, TUserReminder::getCreateTime, tUserReminder.getCreateTime());
        wrapper.eq(tUserReminder.getUpdateTime() != null, TUserReminder::getUpdateTime, tUserReminder.getUpdateTime());
        wrapper.eq(tUserReminder.getWebhook() != null, TUserReminder::getWebhook, tUserReminder.getWebhook());
        wrapper.eq(tUserReminder.getWebhookMethod() != null, TUserReminder::getWebhookMethod, tUserReminder.getWebhookMethod());
        return list(wrapper);
    }

    /**
     * 新增用户提醒表主表， 只记录用户需要的提醒类型，方式
     */
    @Autowired
    private UserRemindScheduler userRemindScheduler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(TUserReminder tUserReminder) {

        // 1. 基础校验
        if (!RepeatType.checkRepeatType(tUserReminder.getRepeatRule())) {
            throw new ServiceException("非法的重复类型:" + tUserReminder.getRepeatRule());
        }

        // 自定义模式校验
        if (RepeatType.CUSTOM.equals(tUserReminder.getRepeatRule())
                && (tUserReminder.getCustomMode() == null || tUserReminder.getCustomMode().isEmpty())) {
            throw new ServiceException("自定义重复模式不能为空");
        }

        // 默认间隔
        if (tUserReminder.getRepeatInterval() == null || tUserReminder.getRepeatInterval() <= 0) {
            tUserReminder.setRepeatInterval(1);
        }

        log.info("保存提醒记录：", JSON.toJSONString(tUserReminder));
        // 2. 先保存主表（保存后 id 会回填）
        this.save(tUserReminder);

        // 3. 根据主表规则生成子项
        List<TUserReminderItem> items = buildReminderItems(tUserReminder);

        // 4. 批量保存子项
        if (!items.isEmpty()) {
            tUserReminderItemService.saveBatch(items);

            boolean expired = false;
            for (TUserReminderItem item : items) {

                if (tUserReminder.getRemindTime().isBefore(LocalDateTime.now())){
                    Long nextRemindTime = ScheduleUtil.getNextRemindTime(tUserReminder, item );
                    if (nextRemindTime == null) {
                        expired = true;
                    }
                    userRemindScheduler.schedule(
                            nextRemindTime,
                            tUserReminder.getUserId(),
                            item.getId()
                    );

                }else{
                    userRemindScheduler.schedule(
                            DateUtil.toTimestamp(item.getRemindTime()),
                            tUserReminder.getUserId(),
                            item.getId()
                    );
                }

            }
            if (expired){
                tUserReminder.setStatus(RemindStatus.EXPIRED);
                updateById(tUserReminder);
            }
        }

        return true;
    }

    /**
     * 根据主提醒记录构建子提醒项
     */
    /**
     * 根据主提醒记录构建子提醒项（支持一天多次时间点）
     */
    private List<TUserReminderItem> buildReminderItems(TUserReminder main) {

        String repeatRule = main.getRepeatRule();

        // 1. 自定义类型（生日 / 纪念日）
        if (RepeatType.CUSTOM.equals(repeatRule)) {
            return buildCustomItems(main);
        }

        // 所有非 custom 类型都支持 specifyTimes
        List<LocalTime> timePoints = parseTimePoints(main);

        // 2. 需要多个子项的固定类型（每周 / 每月 / 每年）
        if (RepeatType.isMultiItemRepeatType(repeatRule)) {
            return buildMultiItemRepeatItems(main, timePoints);
        }

        // 3. 其它类型（NONE / DAILY / WORKDAY / WEEKEND ...）
        return buildSingleItems(main, timePoints);
    }

    /**
     * 非多子项类型：NONE / DAILY / WORKDAY / WEEKEND ...
     * 按时间点展开成多条子项
     */
    private List<TUserReminderItem> buildSingleItems(TUserReminder main, List<LocalTime> timePoints) {
        LocalDateTime base = main.getRemindTime();
        if (base == null) {
            return Collections.emptyList();
        }
        LocalDate baseDate = base.toLocalDate();

        List<TUserReminderItem> result = new ArrayList<>();
        for (LocalTime time : timePoints) {
            LocalDateTime remindTime = LocalDateTime.of(baseDate, time);

            TUserReminderItem item = baseItemFromMain(main);
            item.setRemindTime(remindTime);
            item.setRepeatRule(main.getRepeatRule());
            item.setRepeatInterval(main.getRepeatInterval());
            result.add(item);
        }
        return result;
    }


    private List<TUserReminderItem> buildMultiItemRepeatItems(TUserReminder main, List<LocalTime> timePoints) {
        switch (main.getRepeatRule()) {
            case RepeatType.WEEKLY:
                return buildWeeklyItems(main, timePoints);
            case RepeatType.MONTHLY:
                return buildMonthlyItems(main, timePoints);
            case RepeatType.YEARLY:
                return buildYearlyItems(main, timePoints);
            default:
                return Collections.emptyList();
        }
    }

    /**
     * 每年：优先使用 specifyDates（如 "06-01,12-25" 或 "2025-06-01"）
     * 生成：每个日期 × 每个时间点 的子项
     */
    private List<TUserReminderItem> buildYearlyItems(TUserReminder main, List<LocalTime> timePoints) {
        String specifyDates = main.getSpecifyDates();

        LocalDateTime base = main.getRemindTime();
        if (base == null) {
            return buildSingleItems(main, timePoints);
        }
        LocalDate baseDate = base.toLocalDate();
        int year = baseDate.getYear();

        List<LocalDate> dateList = new ArrayList<>();

        if (specifyDates == null || specifyDates.trim().isEmpty()) {
            // 没有指定多天，就按“每年同一天”
            dateList.add(baseDate);
        } else {
            String[] dates = specifyDates.split(",");
            for (String d : dates) {
                String dateStr = d.trim();
                if (dateStr.isEmpty()) continue;

                LocalDate date;
                try {
                    if (dateStr.length() == 5) { // 例如 "06-01"
                        date = LocalDate.parse(year + "-" + dateStr);
                    } else { // "2025-06-01"
                        date = LocalDate.parse(dateStr);
                    }
                } catch (DateTimeParseException e) {
                    continue;
                }

                // 如果今年这个日期已经过去，就从明年开始
                if (date.isBefore(baseDate)) {
                    date = date.plusYears(1);
                }
                dateList.add(date);
            }
        }

        if (dateList.isEmpty()) {
            return buildSingleItems(main, timePoints);
        }

        List<TUserReminderItem> result = new ArrayList<>();
        for (LocalDate date : dateList) {
            for (LocalTime time : timePoints) {
                LocalDateTime remindTime = LocalDateTime.of(date, time);

                TUserReminderItem item = baseItemFromMain(main);
                item.setRemindTime(remindTime);
                item.setRepeatRule(RepeatType.YEARLY);
                item.setRepeatInterval(main.getRepeatInterval());
                result.add(item);
            }
        }

        return result;
    }


    /**
     * 每月：repeatMonthDays 为 "1,10,20"
     * 生成：每个“日” × 每个时间点 的子项
     */
    private List<TUserReminderItem> buildMonthlyItems(TUserReminder main, List<LocalTime> timePoints) {
        String monthDays = main.getRepeatMonthDays();
        if (monthDays == null || monthDays.trim().isEmpty()) {
            // 没配具体哪几天，就按当前日期 + 时间点
            return buildSingleItems(main, timePoints);
        }

        LocalDateTime base = main.getRemindTime();
        if (base == null) {
            return buildSingleItems(main, timePoints);
        }
        LocalDate baseDate = base.toLocalDate();

        List<TUserReminderItem> result = new ArrayList<>();
        String[] days = monthDays.split(",");
        for (String d : days) {
            String dayStr = d.trim();
            if (dayStr.isEmpty()) continue;

            int dayOfMonth = Integer.parseInt(dayStr);

            LocalDate candidate;
            try {
                candidate = baseDate.withDayOfMonth(dayOfMonth);
            } catch (DateTimeException e) {
                // 比如 2 月没有 30 号，可以按需求：跳过
                continue;
            }

            // 如果这个日期已经过去，从下个月开始
            if (candidate.isBefore(baseDate)) {
                candidate = candidate.plusMonths(1);
            }

            for (LocalTime time : timePoints) {
                LocalDateTime remindTime = LocalDateTime.of(candidate, time);

                TUserReminderItem item = baseItemFromMain(main);
                item.setRemindTime(remindTime);
                item.setRepeatRule(RepeatType.MONTHLY);
                item.setRepeatInterval(main.getRepeatInterval());
                result.add(item);
            }
        }

        if (result.isEmpty()) {
            return buildSingleItems(main, timePoints);
        }
        return result;
    }


    /**
     * 每周：repeatWeekdays 存的是 "1,3,5" 这样的字符串
     * 生成：每个星期几 × 每个时间点 的子项
     */
    private List<TUserReminderItem> buildWeeklyItems(TUserReminder main, List<LocalTime> timePoints) {
        String weekdaysStr = main.getRepeatWeekdays();
        if (weekdaysStr == null || weekdaysStr.trim().isEmpty()) {
            // 没配置星期几，就退化成单条
            return buildSingleItems(main, timePoints);
        }

        Set<Integer> weekdays = Arrays.stream(weekdaysStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toCollection(TreeSet::new));

        if (weekdays.isEmpty()) {
            return buildSingleItems(main, timePoints);
        }

        LocalDateTime base = main.getRemindTime();
        if (base == null) {
            return buildSingleItems(main, timePoints);
        }
        LocalDate baseDate = base.toLocalDate();
        int baseDow = baseDate.getDayOfWeek().getValue(); // 1=Monday .. 7=Sunday

        List<TUserReminderItem> result = new ArrayList<>();
        for (Integer targetDow : weekdays) {
            int diff = targetDow - baseDow;
            if (diff < 0) {
                diff += 7;
            }
            LocalDate targetDate = baseDate.plusDays(diff);

            for (LocalTime time : timePoints) {
                LocalDateTime remindTime = LocalDateTime.of(targetDate, time);

                TUserReminderItem item = baseItemFromMain(main);
                item.setRemindTime(remindTime);
                item.setRepeatRule(RepeatType.WEEKLY);
                item.setRepeatInterval(main.getRepeatInterval());
                result.add(item);
            }
        }
        return result;
    }


    /**
     * 自定义类型：目前只支持 生日 / 纪念日
     */
    private List<TUserReminderItem> buildCustomItems(TUserReminder main) {
        String customMode = main.getCustomMode();
        if (customMode == null || customMode.isEmpty()) {
            return Collections.emptyList();
        }

        switch (customMode) {
            case CustomMode.BIRTHDAY:
            case CustomMode.ANNIVERSARY:
                // 生日 / 纪念日：当天 + 提前几天
                return buildAnniversaryItems(main);

            default:
                // 暂时不支持的自定义模式
                return Collections.emptyList();
        }
    }


    /**
     * 自定义时间点（用药提示：每天多次）
     * main.specifyTimes 例如: "08:00,12:00,18:00"
     */
    private List<TUserReminderItem> buildSpecifyTimeItems(TUserReminder main) {
        String specifyTimes = main.getSpecifyTimes();
        if (specifyTimes == null || specifyTimes.isEmpty()) {
            return Collections.emptyList();
        }

        LocalDateTime base = main.getRemindTime();
        LocalDate baseDate = base.toLocalDate();

        List<TUserReminderItem> result = new ArrayList<>();

        String[] times = specifyTimes.split(",");
        for (String t : times) {
            String timeStr = t.trim();
            if (timeStr.isEmpty()) {
                continue;
            }
            // 支持 "HH:mm" 或 "HH:mm:ss"
            LocalTime time;
            if (timeStr.length() == 5) {
                time = LocalTime.parse(timeStr); // 08:00
            } else {
                time = LocalTime.parse(timeStr); // 08:00:00
            }

            LocalDateTime remindTime = LocalDateTime.of(baseDate, time);

            // 如果当天这个时间已经过去，可以看业务需求决定：是从明天开始，还是仍保留今天
            if (remindTime.isBefore(base)) {
                remindTime = remindTime.plusDays(1);
            }

            TUserReminderItem item = baseItemFromMain(main);
            item.setRemindTime(remindTime);
            // 用药场景一般是“每天这些时间点”
            item.setRepeatRule(RepeatType.DAILY);

            result.add(item);
        }

        return result;
    }

    /**
     * 解析主表上的时间点：
     * - 如果 specifyTimes 不为空：按 "HH:mm" / "HH:mm:ss" 解析
     * - 否则：使用 main.remindTime 的时间部分
     */
    private List<LocalTime> parseTimePoints(TUserReminder main) {
        List<LocalTime> times = new ArrayList<>();

        String specifyTimes = main.getSpecifyTimes();
        if (specifyTimes != null && !specifyTimes.isEmpty()) {
            String[] arr = specifyTimes.split(",");
            for (String t : arr) {
                String timeStr = t.trim();
                if (timeStr.isEmpty()) {
                    continue;
                }
                LocalTime time;
                if (timeStr.length() == 5) {
                    // "HH:mm"
                    time = LocalTime.parse(timeStr);
                } else {
                    // "HH:mm:ss"
                    time = LocalTime.parse(timeStr);
                }
                times.add(time);
            }
        }

        if (times.isEmpty()) {
            LocalDateTime base = main.getRemindTime();
            if (base != null) {
                times.add(base.toLocalTime());
            }
        }

        times.sort(Comparator.naturalOrder());
        return times;
    }


    /**
     * 生日/纪念日：当天 + 提前几天（如果 advanceDays > 0）
     */
    private List<TUserReminderItem> buildAnniversaryItems(TUserReminder main) {
        List<TUserReminderItem> result = new ArrayList<>();

        LocalDateTime base = main.getRemindTime();
        Integer advanceDays = main.getAdvanceDays();
        if (advanceDays == null) {
            advanceDays = 0;
        }

        // 当天
        TUserReminderItem onDay = baseItemFromMain(main);
        onDay.setRemindTime(base);
        onDay.setRepeatRule(RepeatType.YEARLY);
        result.add(onDay);

        // 提前提醒
        if (advanceDays > 0) {
            TUserReminderItem advanceItem = baseItemFromMain(main);
            advanceItem.setRemindTime(base.minusDays(advanceDays));
            advanceItem.setRepeatRule(RepeatType.YEARLY);
            result.add(advanceItem);
        }

        return result;
    }


    private List<TUserReminderItem> buildMultiItemRepeatItems(TUserReminder main) {
        switch (main.getRepeatRule()) {
            case RepeatType.WEEKLY:
                return buildWeeklyItems(main);
            case RepeatType.MONTHLY:
                return buildMonthlyItems(main);
            case RepeatType.YEARLY:
                return buildYearlyItems(main);
            default:
                return Collections.emptyList();
        }
    }

    /**
     * 每周：repeat_weekdays 存的是 '1'..'7'
     */
    private List<TUserReminderItem> buildWeeklyItems(TUserReminder main) {
        Set<String> weekdays = new HashSet<>(JSON.parseArray(main.getRepeatWeekdays(), String.class));
        if (weekdays == null || weekdays.isEmpty()) {
            // 没配置星期几，就退化成单条
            return Collections.singletonList(buildSingleItem(main));
        }

        LocalDateTime base = main.getRemindTime();
        LocalDate baseDate = base.toLocalDate();
        LocalTime baseTime = base.toLocalTime();
        int baseDow = baseDate.getDayOfWeek().getValue(); // 1=Monday .. 7=Sunday

        List<TUserReminderItem> result = new ArrayList<>();
        for (String w : weekdays) {
            int targetDow = Integer.parseInt(w);
            int diff = targetDow - baseDow;
            if (diff < 0) {
                diff += 7;
            }
            LocalDate targetDate = baseDate.plusDays(diff);

            TUserReminderItem item = baseItemFromMain(main);
            item.setRemindTime(LocalDateTime.of(targetDate, baseTime));
            item.setRepeatRule(RepeatType.WEEKLY);
            // 间隔（每2周/每3周）继承主表
            item.setRepeatInterval(main.getRepeatInterval());
            result.add(item);
        }
        return result;
    }

    /**
     * 每月：repeat_month_days 为 "1,10,20"
     */
    private List<TUserReminderItem> buildMonthlyItems(TUserReminder main) {
        String monthDays = main.getRepeatMonthDays();
        if (monthDays == null || monthDays.isEmpty()) {
            return Collections.singletonList(buildSingleItem(main));
        }

        LocalDateTime base = main.getRemindTime();
        LocalDate baseDate = base.toLocalDate();
        LocalTime baseTime = base.toLocalTime();

        List<TUserReminderItem> result = new ArrayList<>();
        String[] days = monthDays.split(",");
        for (String d : days) {
            String dayStr = d.trim();
            if (dayStr.isEmpty()) continue;

            int dayOfMonth = Integer.parseInt(dayStr);

            LocalDate candidate;
            try {
                candidate = baseDate.withDayOfMonth(dayOfMonth);
            } catch (DateTimeException e) {
                // 比如 2 月没有 30 号，可以根据需求选择：跳过 或者取该月最后一天
                continue;
            }

            // 如果这个日期已经过去，从下个月开始
            if (candidate.isBefore(baseDate)) {
                candidate = candidate.plusMonths(1);
            }

            TUserReminderItem item = baseItemFromMain(main);
            item.setRemindTime(LocalDateTime.of(candidate, baseTime));
            item.setRepeatRule(RepeatType.MONTHLY);
            item.setRepeatInterval(main.getRepeatInterval());
            result.add(item);
        }

        if (result.isEmpty()) {
            return Collections.singletonList(buildSingleItem(main));
        }
        return result;
    }

    /**
     * 每年：优先使用 specify_dates（如 "06-01,12-25" 或 "2025-06-01"）
     */
    private List<TUserReminderItem> buildYearlyItems(TUserReminder main) {
        String specifyDates = main.getSpecifyDates();
        if (specifyDates == null || specifyDates.isEmpty()) {
            // 没有指定多天，就按“每年同一天”来，一条就够
            return Collections.singletonList(buildSingleItem(main));
        }

        LocalDateTime base = main.getRemindTime();
        LocalDate baseDate = base.toLocalDate();
        LocalTime baseTime = base.toLocalTime();
        int year = baseDate.getYear();

        List<TUserReminderItem> result = new ArrayList<>();

        String[] dates = specifyDates.split(",");
        for (String d : dates) {
            String dateStr = d.trim();
            if (dateStr.isEmpty()) continue;

            LocalDate date;
            try {
                if (dateStr.length() == 5) { // 例如 "06-01"
                    date = LocalDate.parse(year + "-" + dateStr);
                } else { // "2025-06-01"
                    date = LocalDate.parse(dateStr);
                }
            } catch (DateTimeParseException e) {
                continue;
            }

            // 如果今年这个日期已经过去，就从明年开始
            if (date.isBefore(baseDate)) {
                date = date.plusYears(1);
            }

            TUserReminderItem item = baseItemFromMain(main);
            item.setRemindTime(LocalDateTime.of(date, baseTime));
            item.setRepeatRule(RepeatType.YEARLY);
            item.setRepeatInterval(main.getRepeatInterval());
            result.add(item);
        }

        if (result.isEmpty()) {
            return Collections.singletonList(buildSingleItem(main));
        }
        return result;
    }


    /**
     * 非多子项类型：只建一条子提醒
     */
    private TUserReminderItem buildSingleItem(TUserReminder main) {
        TUserReminderItem item = baseItemFromMain(main);
        item.setRemindTime(main.getRemindTime());
        item.setRepeatRule(main.getRepeatRule());
        item.setRepeatInterval(main.getRepeatInterval());
        return item;
    }

    /**
     * 公共复制逻辑：从主表复制公共字段
     */
    private TUserReminderItem baseItemFromMain(TUserReminder main) {
        TUserReminderItem item = new TUserReminderItem();
        BeanUtils.copyProperties(main, item);

        // 别把主表 id 覆盖到子表
        item.setId(null);
        // 指明主表 id
        item.setMainId(main.getId());
        return item;
    }



    /**
     * 修改用户提醒表主表， 只记录用户需要的提醒类型，方式
     */
    @Override
    public boolean update(TUserReminder tUserReminder) {
        return updateById(tUserReminder);
    }

    /**
     * 批量删除用户提醒表主表， 只记录用户需要的提醒类型，方式
     */
    @Override
    public boolean deleteByIds(List<Long> ids) {
        return removeByIds(ids);
    }
}
