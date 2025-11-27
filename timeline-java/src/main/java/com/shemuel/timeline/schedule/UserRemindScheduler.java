package com.shemuel.timeline.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shemuel.timeline.common.Constants;
import com.shemuel.timeline.common.RemindStatus;
import com.shemuel.timeline.config.ReminderPushService;
import com.shemuel.timeline.entity.TUserReminder;
import com.shemuel.timeline.entity.TUserReminderItem;
import com.shemuel.timeline.mapper.TUserReminderMapper;
import com.shemuel.timeline.service.TUserReminderItemService;
import com.shemuel.timeline.utils.DateUtil;
import com.shemuel.timeline.utils.ScheduleUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @Author: dengshaoxiang
 * @Date: 2025-11-13-14:48
 * @Description:
 */
@Service
@Slf4j
public class UserRemindScheduler extends ZSetDelayScheduler{
    @Autowired
    private TUserReminderMapper tUserReminderMapper;

    @Autowired
    private TUserReminderItemService tUserReminderItemService;

    @Autowired
    private ReminderPushService reminderPushService;

    @Autowired



    @Override
    protected String zSetKey() {
        return "user-remind";
    }

    public void cancelByMainId(Long mainId) {
        List<TUserReminderItem> subItems = tUserReminderItemService
                .list(new LambdaQueryWrapper<TUserReminderItem>().eq(TUserReminderItem::getMainId, mainId));

        for (TUserReminderItem subItem : subItems) {
            cancelSchedule(String.valueOf(subItem.getUserId()), String.valueOf(subItem.getId()));
        }
    }



    public void schedule(Long time, Long userId, Long remindId) {
        if (time == null || time < System.currentTimeMillis()) {
            log.warn("invalid schedule time: {}", time);
            return;
        }
        schedule(time, String.valueOf(userId), String.valueOf(remindId));
    }

    @Override
    protected void timeToRun(String[] taskPayloads) {

        Long userId = Long.parseLong(taskPayloads[0]);
        Long subId = Long.parseLong(taskPayloads[1]);

        TUserReminderItem remindItem = tUserReminderItemService.getById(subId);
        if (remindItem == null) {
            return;
        }
        // 查询主表
        TUserReminder remind = tUserReminderMapper.selectById(remindItem.getMainId());
        if (remind == null ) {
            return;
        }
        if (Objects.equals(remind.getStatus(), Constants.active) ){
            reminderPushService.pushReminder(remind, remindItem);
        }


        // 3. 根据子项计算下一次提醒时间，并推进每个子项的 remindTime
        Long nextRemindTime = ScheduleUtil.getNextRemindTime(remind, remindItem);
        if (nextRemindTime == null) {
            log.info("任务提醒，user:{}, userId：{}, 本提醒任务已完结，无下次提醒", userId,remind.getId());
            remindItem.setStatus(RemindStatus.EXPIRED);
            remindItem.setUpdateTime(LocalDateTime.now());
            tUserReminderItemService.updateById(remindItem);
            checkAndSetMainStatus(remind);
            // 更新状态
            return;
        }


        // 4. 有下次提醒时间的话，重新加入队列
        schedule(nextRemindTime, userId, remindItem.getId());

        // 更新
        remindItem.setRemindTime(DateUtil.fromTimestamp(nextRemindTime));
        remindItem.setUpdateTime(LocalDateTime.now());
        tUserReminderItemService.updateById(remindItem);
        log.info("任务提醒，user:{}, 内容：{}, 下次提醒时间：{}",
                userId, remind.getTitle(), remindItem.getRemindTime());
    }

    private void checkAndSetMainStatus(TUserReminder remind ){
        TUserReminderItem param = new TUserReminderItem();
        param.setMainId(remind.getId());
        List<TUserReminderItem> items = tUserReminderItemService.selectList(param);

        boolean noExpire = items.stream().anyMatch(i -> !Objects.equals(i.getStatus(), RemindStatus.EXPIRED));
        if (!noExpire){

            if (Objects.equals(remind.getVisible(), Constants.active)){
                remind.setStatus(RemindStatus.EXPIRED);
                tUserReminderMapper.updateById(remind);
            }else{
                // 删主表
                tUserReminderMapper.deleteById(remind.getId());
                tUserReminderItemService.deleteByMainId(remind.getId());
            }
            log.info("当前任务无下次提醒时间， 已过期，{}", remind.getId());
        }
    }
}
