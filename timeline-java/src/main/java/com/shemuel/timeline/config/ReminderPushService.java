package com.shemuel.timeline.config;

import com.alibaba.fastjson2.annotation.JSONField;
import com.shemuel.timeline.entity.TUserReminder;
import com.shemuel.timeline.tools.wx.WeComRobotTool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ReminderPushService {
    @Autowired
    private WeComRobotTool weComRobotTool;
    @Data
    public static class ReminderMsg {
        private String type = "REMIND";
        private Long reminderId;
        private String title;
        private String content;
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime remindTime;
    }

    public void pushReminder(TUserReminder remind) {
        ReminderMsg msg = new ReminderMsg();
        msg.setReminderId(remind.getId());
        msg.setTitle(remind.getTitle());
        msg.setContent(remind.getContent());
        msg.setRemindTime(remind.getRemindTime());

        try {
            // ws推送
            ReminderWebSocketEndpoint.sendToUser(remind.getUserId(), msg);
        } catch (Exception e) {

        }

        // 企微推送
        String notifyText = "来自utool提醒助手 \n" + remind.getTitle() + " \n" + remind.getContent();
        // 1. 发送提醒（这里你后面可以接入真正的桌面弹窗 / 微信 / Webhook 等）
        weComRobotTool.sendGroupMessage(notifyText);
    }
}
