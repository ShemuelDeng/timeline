package com.shemuel.timeline.service.impl;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.shemuel.timeline.common.Constants;
import com.shemuel.timeline.common.CustomMode;
import com.shemuel.timeline.common.RemindStatus;
import com.shemuel.timeline.common.RepeatRuleConst;
import com.shemuel.timeline.entity.TUserNotifySetting;
import com.shemuel.timeline.entity.TUserReminderItem;
import com.shemuel.timeline.exception.ServiceException;
import com.shemuel.timeline.schedule.UserRemindScheduler;
import com.shemuel.timeline.service.TUserNotifySettingService;
import com.shemuel.timeline.service.TUserReminderItemService;
import com.shemuel.timeline.utils.CronUtil;
import com.shemuel.timeline.utils.DateUtil;
import com.shemuel.timeline.utils.ScheduleUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
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
import org.springframework.util.CollectionUtils;

/**
 * 用户提醒表主表， 只记录用户需要的提醒类型，方式 服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TUserReminderServiceImpl extends ServiceImpl<TUserReminderMapper, TUserReminder> implements TUserReminderService {

    @Resource
    private TUserReminderItemService tUserReminderItemService;

    @Resource
    private TUserNotifySettingService tUserNotifySettingService;

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
        wrapper.orderByDesc(TUserReminder::getId);
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

    private void mergeSettingNotNull(TUserNotifySetting src, TUserReminder target) {
        if (src == null) return;
        if (src.getWecomEnabledDefault() != null && target.getWecomBotEnable() == null) {
            target.setWecomBotEnable(src.getWecomEnabledDefault());
        }
        if (src.getDingdingEnabledDefault() != null && target.getDingdingBotEnable() == null) {
            target.setDingdingBotEnable(src.getDingdingEnabledDefault());
        }

        if (src.getBarkEnabledDefault() != null && target.getWebhookEnable() == null) {
            target.setWebhookEnable(src.getBarkEnabledDefault());
        }
    }


    private void checkCron(TUserReminder tUserReminder) {
        if (RepeatRuleConst.CRON.equals(tUserReminder.getRepeatRule())) {
            // 1. 兼容 5/6 段 + 语法校验 + 最小间隔校验
            String normalized = CronUtil.normalizeAndValidate(tUserReminder.getCronExpr());

            // 2. 用标准化后的 6 段表达式覆盖保存
            tUserReminder.setCronExpr(normalized);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TUserReminder insert(TUserReminder tUserReminder) {

        // 1. 基础校验
        if (!RepeatRuleConst.checkRepeatType(tUserReminder.getRepeatRule())) {
            throw new ServiceException("非法的重复类型:" + tUserReminder.getRepeatRule());
        }

        // 自定义模式校验
        if (RepeatRuleConst.CUSTOM.equals(tUserReminder.getRepeatRule())
                && (tUserReminder.getCustomMode() == null || tUserReminder.getCustomMode().isEmpty())) {
            throw new ServiceException("自定义重复模式不能为空");
        }

        checkCron(tUserReminder);

        // 默认间隔
        if (tUserReminder.getRepeatInterval() == null || tUserReminder.getRepeatInterval() <= 0) {
            tUserReminder.setRepeatInterval(1);
        }

        TUserNotifySetting byUserId = tUserNotifySettingService.getByUserId(StpUtil.getLoginIdAsLong());
        mergeSettingNotNull(byUserId, tUserReminder);
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
                    Long nextRemindTime = ScheduleUtil.getNextRemindTime(tUserReminder, item, Constants.NOT_FOR_SCHEDULE);
                    if (nextRemindTime == null) {
                        expired = true;
                    }else{
                        expired = false;
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

        return tUserReminder;
    }

    @Override
    public LocalDateTime getNextRemindTime(Long id) {

        TUserReminder main = getById(id);
        if (main == null) {
            return null;
        }
        List<TUserReminderItem> items = tUserReminderItemService.getByMainId(main.getId());

        LocalDateTime now = LocalDateTime.now();

        List<Long> fireTimes = new ArrayList<>();
        for (TUserReminderItem item : items) {

            // ✅ 注意：这里最好用 item 的时间判断，而不是 main 的时间
            if (item.getRemindTime().isBefore(now)) {
                // 已经在过去了，算下一次
                Long next = ScheduleUtil.getNextRemindTime(main, item, Constants.FOR_SCHEDULE);
                if (next == null) {
                    continue; // 这个子项彻底没有下次了
                }
                fireTimes.add(next);
            } else {
                fireTimes.add(DateUtil.toTimestamp(item.getRemindTime()));
            }
        }

        return CollectionUtils.isEmpty(fireTimes) ? null : DateUtil.fromTimestamp(fireTimes.get(0));
    }

    /**
     * 根据主提醒记录构建子提醒项
     */
    /**
     * 根据主提醒记录构建子提醒项（支持一天多次时间点）
     */
    private List<TUserReminderItem> buildReminderItems(TUserReminder main) {

        String repeatRule = main.getRepeatRule();

        // 如果是 CRON 类型，特殊处理：
        if (RepeatRuleConst.CRON.equals(repeatRule)  && StringUtils.isNotEmpty(main.getCronExpr())) {
            TUserReminderItem item = new TUserReminderItem();
            item.setRepeatRule(repeatRule);
            item.setMainId(main.getId());
            item.setUserId(main.getUserId());
            item.setTitle(main.getTitle());
            item.setContent(main.getContent());
            item.setRepeatInterval(main.getRepeatInterval());
            item.setStatus(RemindStatus.ON);

            // 用 cron 算第一次时间
            Long nextRemindTime = ScheduleUtil.getNextRemindTime(main, item, Constants.NOT_FOR_SCHEDULE);
            if (nextRemindTime == null) {
                // 没有下一次，直接返回空列表，相当于不创建子项
                return Collections.emptyList();
            }
            item.setRemindTime(DateUtil.fromTimestamp(nextRemindTime));
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());


            return Collections.singletonList(item);
        }

        // 1. 自定义类型（生日 / 纪念日）
        if (RepeatRuleConst.CUSTOM.equals(repeatRule)) {
            return buildCustomItems(main);
        }

        // 所有非 custom 类型都支持 specifyTimes
        List<LocalTime> timePoints = parseTimePoints(main);

        // 2. 需要多个子项的固定类型（每周 / 每月 / 每年）
        if (RepeatRuleConst.isMultiItemRepeatType(repeatRule)) {
            return buildMultiItemRepeatItems(main, timePoints);
        }

        // 3. 其它类型（NONE / DAILY / WORKDAY / WEEKEND ,CRON...）
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
            case RepeatRuleConst.WEEKLY:
                return buildWeeklyItems(main, timePoints);
            case RepeatRuleConst.MONTHLY:
                return buildMonthlyItems(main, timePoints);
            case RepeatRuleConst.YEARLY:
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
                item.setRepeatRule(RepeatRuleConst.YEARLY);
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
                item.setRepeatRule(RepeatRuleConst.MONTHLY);
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
                item.setRepeatRule(RepeatRuleConst.WEEKLY);
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
        onDay.setRepeatRule(RepeatRuleConst.YEARLY);
        result.add(onDay);

        // 提前提醒
        if (advanceDays > 0) {
            TUserReminderItem advanceItem = baseItemFromMain(main);
            advanceItem.setRemindTime(base.minusDays(advanceDays));
            advanceItem.setRepeatRule(RepeatRuleConst.YEARLY);
            result.add(advanceItem);
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
    @Transactional(rollbackFor = Exception.class)
    public boolean update(TUserReminder patch) {
        if (patch.getId() == null) {
            throw new ServiceException("id 不能为空");
        }
        TUserReminder db = this.getById(patch.getId());
        if (db == null) {
            throw new ServiceException("提醒不存在");
        }

        // 1. 判断这次请求是否只改状态（ON/OFF）
        boolean onlyStatusChange = isOnlyStatusChange(patch);

        // 2. 判断这次是否修改了“时间 & 重复相关字段”
        boolean scheduleFieldChanged = isScheduleFieldChanged(patch);

        // ① 只改状态：开关互相切换
        if (onlyStatusChange && !scheduleFieldChanged) {
            return updateStatusOnly(db, patch.getStatus());
        }
        checkCron(patch);

        // ② 没改时间&重复，只改标题/内容/通知方式等
        if (!scheduleFieldChanged) {
            // 只更新非空字段，避免把 null 覆盖掉
            mergeNotNull(patch, db);
            return this.updateById(db);
        }

        // ③ 改了时间/重复配置（包括“再提醒”和编辑规则）
        // 先把非空字段合并到 db 上，得到“最新规则”
        mergeNotNull(patch, db);

        // 校验（跟 insert 一样）
        if (!RepeatRuleConst.checkRepeatType(db.getRepeatRule())) {
            throw new ServiceException("非法的重复类型:" + db.getRepeatRule());
        }
        if (RepeatRuleConst.CUSTOM.equals(db.getRepeatRule())
                && (db.getCustomMode() == null || db.getCustomMode().isEmpty())) {
            throw new ServiceException("自定义重复模式不能为空");
        }
        if (db.getRepeatInterval() == null || db.getRepeatInterval() <= 0) {
            db.setRepeatInterval(1);
        }

        // 真正的“重新生成子项 + 重新调度”
        return rebuildItemsAndSchedule(db);
    }

    /**
     * 只要这些字段中有任意一个非 null，就认为会影响“子项 & 调度”
     */
    private boolean isScheduleFieldChanged(TUserReminder patch) {
        return patch.getRemindTime() != null
                || patch.getRepeatRule() != null
                || patch.getCustomMode() != null
                || patch.getRepeatInterval() != null
                || !isBlank(patch.getRepeatWeekdays())
                || !isBlank(patch.getRepeatMonthDays())
                || !isBlank(patch.getSpecifyTimes())
                || !isBlank(patch.getSpecifyDates())
                || patch.getAdvanceDays() != null;
    }

    /**
     * 判断这次更新是否只改了 status 字段（id 除外）
     * 前端如果只传 {id, status}，就会走这里
     */
    private boolean isOnlyStatusChange(TUserReminder patch) {
        return patch.getStatus() != null
                && patch.getRemindTime() == null
                && patch.getRepeatRule() == null
                && patch.getCustomMode() == null
                && patch.getRepeatInterval() == null
                && isBlank(patch.getRepeatWeekdays())
                && isBlank(patch.getRepeatMonthDays())
                && isBlank(patch.getSpecifyTimes())
                && isBlank(patch.getSpecifyDates())
                && patch.getAdvanceDays() == null;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }


    /**
     * 只处理开关切换：
     * - 1：开启 → 恢复未来的调度（从“现在”往后算下一次）
     * - 0：关闭 → 取消所有未来调度
     * - 2：一般不通过手工改成过期，不建议走这里
     */
    private boolean updateStatusOnly(TUserReminder db, Integer newStatus) {
        log.info("updateStatusOnly id:{}, old:{}, newStatus:{}", db.getId(), db.getStatus(), newStatus);
        if (newStatus == null || Objects.equals(db.getStatus(), newStatus)) {
            return true;
        }

        db.setStatus(newStatus);
        boolean ok = this.updateById(db);
        if (!ok) {
            return false;
        }

        Long mainId = db.getId();

        // 关
        if (Objects.equals(newStatus, RemindStatus.OFF)) {
            // 关闭：取消所有未来调度（不动子项的 remindTime）
            userRemindScheduler.cancelByMainId(mainId);
            return true;
        }

        // 开
        if (Objects.equals(newStatus, RemindStatus.ON)) {
            List<TUserReminderItem> items = tUserReminderItemService.list(
                    new LambdaQueryWrapper<TUserReminderItem>()
                            .eq(TUserReminderItem::getMainId, mainId)
                            .ne(TUserReminderItem::getStatus, RemindStatus.EXPIRED)
            );

            LocalDateTime now = LocalDateTime.now();
            boolean hasFuture = false;

            for (TUserReminderItem item : items) {
                Long fireTime = null;

                // 1) 如果子项的时间还在未来，直接用它
                if (!item.getRemindTime().isBefore(now)) {
                    fireTime = DateUtil.toTimestamp(item.getRemindTime());
                } else {
                    // 2) 子项时间已经在过去了，需要重新算下一次
                    Long next = ScheduleUtil.getNextRemindTime(db, item, Constants.NOT_FOR_SCHEDULE);
                    if (next == null) {
                        // 这个子项确实已经无后续了，标记为过期
                        item.setStatus(RemindStatus.EXPIRED);
                        item.setUpdateTime(LocalDateTime.now());
                        tUserReminderItemService.updateById(item);
                        continue;
                    }
                    fireTime = next;
                }

                hasFuture = true;
                userRemindScheduler.schedule(fireTime, db.getUserId(), item.getId());
            }

            // 如果一个未来任务都没挂上，相当于整条主提醒已经没意义了
            if (!hasFuture) {
                db.setStatus(RemindStatus.EXPIRED);
                this.updateById(db);
            }

            return true;
        }

        // status == EXPIRED 就不做额外处理了
        return true;
    }



    /**
     * 按最新的 main 配置：
     * 1. 删除旧子项 & 取消旧调度
     * 2. 重建子项
     * 3. 根据 status 决定是否调度
     * 4. 如果算下来没有未来任务，则标记为 EXPIRED
     */
    private boolean rebuildItemsAndSchedule(TUserReminder main) {

        Long mainId = main.getId();

        // 1. 先更新主表“规则”本身
        this.updateById(main);

        // 2. 删除旧子项 & 取消旧任务
        tUserReminderItemService.remove(
                new LambdaQueryWrapper<TUserReminderItem>()
                        .eq(TUserReminderItem::getMainId, mainId)
        );
         userRemindScheduler.cancelByMainId(mainId);

        // 3. 按最新规则生成子项
        List<TUserReminderItem> items = buildReminderItems(main);
        if (items.isEmpty()) {
            // 没有任何子项 = 本身就是“无效/过期”配置
            main.setStatus(RemindStatus.EXPIRED);
            this.updateById(main);
            return true;
        }

        tUserReminderItemService.saveBatch(items);

        // 如果当前状态是“关闭”，只保存子项，不调度
        if (Objects.equals(main.getStatus(), RemindStatus.OFF)) {
            return true;
        }

        // 4. 根据时间计算是否还有未来任务
        boolean hasFuture = false;
        LocalDateTime now = LocalDateTime.now();

        for (TUserReminderItem item : items) {
            long fireTime;

            // ✅ 注意：这里最好用 item 的时间判断，而不是 main 的时间
            if (item.getRemindTime().isBefore(now)) {
                // 已经在过去了，算下一次
                Long next = ScheduleUtil.getNextRemindTime(main, item, Constants.NOT_FOR_SCHEDULE);
                if (next == null) {
                    continue; // 这个子项彻底没有下次了
                }
                fireTime = next;
            } else {
                fireTime = DateUtil.toTimestamp(item.getRemindTime());
            }

            hasFuture = true;
            userRemindScheduler.schedule(fireTime, main.getUserId(), item.getId());
        }

        if (!hasFuture) {
            main.setStatus(RemindStatus.EXPIRED);
        } else {
            // 如果之前是 EXPIRED 或 null，就默认变成 ON
            if (main.getStatus() == null || Objects.equals(main.getStatus(), RemindStatus.EXPIRED)) {
                main.setStatus(RemindStatus.ON);
            }
        }
        this.updateById(main);

        return true;
    }


    private void mergeNotNull(TUserReminder src, TUserReminder target) {
        if (src == null || target == null) {
            return;
        }

        if (src.getTitle() != null) {
            target.setTitle(src.getTitle());
        }

        if (src.getCronExpr() != null) {
            target.setCronExpr(src.getCronExpr());
        }


        if (src.getCronEndTime() != null) {
            target.setCronEndTime(src.getCronEndTime());
        }


        if (src.getCronStartTime() != null) {
            target.setCronStartTime(src.getCronStartTime());
        }

        if (src.getContent() != null) {
            target.setContent(src.getContent());
        }
        if (src.getRemindTime() != null) {
            target.setRemindTime(src.getRemindTime());
        }
        if (src.getRepeatRule() != null) {
            target.setRepeatRule(src.getRepeatRule());
        }
        if (src.getCustomMode() != null) {
            target.setCustomMode(src.getCustomMode());
        }
        if (src.getAdvanceDays() != null) {
            target.setAdvanceDays(src.getAdvanceDays());
        }
        if (src.getRepeatInterval() != null) {
            target.setRepeatInterval(src.getRepeatInterval());
        }
        if (src.getRepeatWeekdays() != null) {
            target.setRepeatWeekdays(src.getRepeatWeekdays());
        }
        if (src.getRepeatMonthDays() != null) {
            target.setRepeatMonthDays(src.getRepeatMonthDays());
        }
        if (src.getSpecifyDates() != null) {
            target.setSpecifyDates(src.getSpecifyDates());
        }
        if (src.getSpecifyTimes() != null) {
            target.setSpecifyTimes(src.getSpecifyTimes());
        }
        if (src.getStatus() != null) {
            target.setStatus(src.getStatus());
        }
        if (src.getDoCircle() != null) {
            target.setDoCircle(src.getDoCircle());
        }
        if (src.getCircleBegin() != null) {
            target.setCircleBegin(src.getCircleBegin());
        }
        if (src.getCircleEnd() != null) {
            target.setCircleEnd(src.getCircleEnd());
        }
        if (src.getCircleInterval() != null) {
            target.setCircleInterval(src.getCircleInterval());
        }
        if (src.getNotifyDesktop() != null) {
            target.setNotifyDesktop(src.getNotifyDesktop());
        }
        if (src.getNotifyWx() != null) {
            target.setNotifyWx(src.getNotifyWx());
        }
        if (src.getNotifySound() != null) {
            target.setNotifySound(src.getNotifySound());
        }
        if (src.getNotifySystem() != null) {
            target.setNotifySystem(src.getNotifySystem());
        }
        if (src.getNotifySoundFile() != null) {
            target.setNotifySoundFile(src.getNotifySoundFile());
        }

        if (src.getWebhook() != null) {
            target.setWebhook(src.getWebhook());
        }
        if (src.getWebhookMethod() != null) {
            target.setWebhookMethod(src.getWebhookMethod());
        }
        if (src.getNotifyDesktopPosition() != null) {
            target.setNotifyDesktopPosition(src.getNotifyDesktopPosition());
        }
        if (src.getNotifyWecomBot() != null) {
            target.setNotifyWecomBot(src.getNotifyWecomBot());
        }
        if (src.getNotifyDingdingBot() != null) {
            target.setNotifyDingdingBot(src.getNotifyDingdingBot());
        }
        if (src.getWecomBotEnable() != null) {
            target.setWecomBotEnable(src.getWecomBotEnable());
        }
        if (src.getDingdingBotEnable() != null) {
            target.setDingdingBotEnable(src.getDingdingBotEnable());
        }
        if (src.getWebhookEnable() != null) {
            target.setWebhookEnable(src.getWebhookEnable());
        }
    }



    /**
     * 批量删除用户提醒表主表， 只记录用户需要的提醒类型，方式
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        Long currentUserId = StpUtil.getLoginIdAsLong();

        // 1. 只查当前用户名下、且 id 在传入列表里的主记录
        List<TUserReminder> myReminders = this.list(
                new LambdaQueryWrapper<TUserReminder>()
                        .in(TUserReminder::getId, ids)
                        .eq(TUserReminder::getUserId, currentUserId)
        );

        if (myReminders.isEmpty()) {
            // 一个都不属于当前用户，直接认为越权
            throw new ServiceException("只允许删除当前用户自己的提醒记录");
        }

        // 可删除的主记录 id 列表
        List<Long> allowedIds = myReminders.stream()
                .map(TUserReminder::getId)
                .collect(Collectors.toList());

        // 如果你希望“只要有一个不是自己的就报错”，可以加这一段：
        if (allowedIds.size() != ids.size()) {
            // 说明传入的 ids 里有别人的数据或不存在的记录
            throw new ServiceException("存在非当前用户的提醒记录，删除被拒绝");
        }

        // 2. 删除主表（仅当前用户自己的）
        boolean main = removeByIds(allowedIds);

        // 3. 处理子表：只查这些 mainId 且 userId 为当前用户
        LambdaQueryWrapper<TUserReminderItem> queryByMain = new LambdaQueryWrapper<TUserReminderItem>()
                .in(TUserReminderItem::getMainId, allowedIds)
                .eq(TUserReminderItem::getUserId, currentUserId);

        List<TUserReminderItem> subItems = tUserReminderItemService.list(queryByMain);

        // 先取消调度
        for (TUserReminderItem subItem : subItems) {
            userRemindScheduler.cancelSchedule(
                    String.valueOf(subItem.getUserId()),
                    String.valueOf(subItem.getId())
            );
        }

        // 再删子表
        boolean item = tUserReminderItemService.remove(queryByMain);

        return main && item;
    }

}
