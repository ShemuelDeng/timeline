package com.shemuel.timeline.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shemuel.timeline.common.Constants;
import com.shemuel.timeline.common.RemindStatus;
import com.shemuel.timeline.config.ReminderPushService;
import com.shemuel.timeline.entity.TUserReminder;
import com.shemuel.timeline.entity.TUserReminderItem;
import com.shemuel.timeline.mapper.TUserReminderMapper;
import com.shemuel.timeline.service.TUserReminderItemService;
import com.shemuel.timeline.tools.wx.WeComRobotTool;
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
    private WeComRobotTool weComRobotTool;

    @Autowired
    private ReminderPushService reminderPushService;

    @Autowired



    @Override
    protected String zSetKey() {
        return "user-remind";
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
        if (remind == null || Objects.equals(remind.getActive(), Constants.NO_active) ) {
            return;
        }

        reminderPushService.pushReminder(remind);

        // 2. 查询子项
        List<TUserReminderItem> items = tUserReminderItemService.list(
                new LambdaQueryWrapper<TUserReminderItem>()
                        .eq(TUserReminderItem::getMainId, remind.getId())
        );

        if (items == null || items.isEmpty()) {
            // 没有子项 == 视为一次性任务，直接过期
            remind.setStatus(RemindStatus.EXPIRED);
            remind.setUpdateTime(LocalDateTime.now());
            tUserReminderMapper.updateById(remind);
            return;
        }

        // 3. 根据子项计算下一次提醒时间，并推进每个子项的 remindTime
        Long nextRemindTime = ScheduleUtil.getNextRemindTime(remind, remindItem);
        if (nextRemindTime == null) {
            // 没有下次提醒时间，则代表已过期
            remind.setStatus(RemindStatus.EXPIRED);
        } else {
            remind.setRemindTime(DateUtil.fromTimestamp(nextRemindTime));
        }

        remind.setUpdateTime(LocalDateTime.now());
        tUserReminderMapper.updateById(remind);

        // 4. 批量更新子项新的 remindTime
        tUserReminderItemService.updateBatchById(items);

        // 5. 有下次提醒时间的话，重新加入队列
        if (nextRemindTime != null) {
            schedule(nextRemindTime, userId, remindItem.getId());
        }

        log.info("任务提醒，user:{}, 内容：{}, 下次提醒时间：{}",
                userId, remind.getTitle(), remind.getRemindTime());
    }
}
