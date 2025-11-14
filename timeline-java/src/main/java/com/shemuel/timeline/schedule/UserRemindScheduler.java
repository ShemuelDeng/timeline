package com.shemuel.timeline.schedule;

import com.shemuel.timeline.common.Constants;
import com.shemuel.timeline.common.RemindStatus;
import com.shemuel.timeline.entity.TUserReminder;
import com.shemuel.timeline.mapper.TUserReminderMapper;
import com.shemuel.timeline.tools.wx.WeComRobotTool;
import com.shemuel.timeline.utils.DateUtil;
import com.shemuel.timeline.utils.ScheduleUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private TUserReminderMapper tUserReminderService;

    @Autowired
    private WeComRobotTool weComRobotTool;


    @Override
    protected String zSetKey() {
        return "user-remind";
    }

    public void schedule(Long time, Long userId, Long remindId) {
        if (time== null || time < System.currentTimeMillis()){
            log.warn("invalid schedule time: {}", time);
            return;
        }
        schedule(time, String.valueOf(userId) ,String.valueOf(remindId));
    }

    @Override
    protected void timeToRun(String[] taskPayloads) {

        Long userId = Long.parseLong(taskPayloads[0]);
        Long reminId = Long.parseLong(taskPayloads[1]);

        TUserReminder remind = tUserReminderService.selectById(reminId);
        if (remind == null || !Objects.equals(remind.getStatus(), RemindStatus.TO_REMIND)){
            return;
        }

        weComRobotTool.sendGroupMessage("提醒用户[" + userId + "] "  + remind.getTitle());

        Long nextRemindTime = ScheduleUtil.getNextRemindTime(remind);
        if (nextRemindTime == null){
            // 没有下次提醒时间，则代表已过期
            remind.setStatus(RemindStatus.EXPIRED);
        }else {
            remind.setRemindTime(DateUtil.fromTimestamp(nextRemindTime));
        }

        remind.setUpdateTime(LocalDateTime.now());
        tUserReminderService.updateById(remind);
        schedule(nextRemindTime, userId, reminId);
        log.info("任务提醒，user:{}, 内容：{}, 下次提醒时间：{}", userId, remind.getTitle(), remind.getRemindTime());
    }
}
