package com.shemuel.timeline.config;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shemuel.timeline.common.Constants;
import com.shemuel.timeline.common.WindowPosition;
import com.shemuel.timeline.entity.TUserNotifySetting;
import com.shemuel.timeline.entity.TUserReminder;
import com.shemuel.timeline.entity.TUserReminderItem;
import com.shemuel.timeline.service.TUserNotifySettingService;
import com.shemuel.timeline.tools.BarkPushTool;
import com.shemuel.timeline.tools.DingTalkRobotTool;
import com.shemuel.timeline.tools.wx.WeComRobotTool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ReminderPushService {

    @Autowired
    private WeComRobotTool weComRobotTool;

    @Autowired
    private TUserNotifySettingService userNotifySettingService;

    // 如果后续有钉钉、Bark工具，也可以注入
     @Autowired
     private DingTalkRobotTool dingTalkRobotTool;
     @Autowired
     private BarkPushTool barkPushTool;


    @Data
    public static class ReminderMsg {
        private String type = "REMIND";
        private Long reminderId;
        private String title;
        private String content;
        private String position;
        private Integer systemNotify;
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime remindTime;
    }

    public void pushReminder(TUserReminder tUserReminder, TUserReminderItem remind) {
        Long userId = remind.getUserId();

        // 1. 构造 WS 消息并推送
        ReminderMsg msg = new ReminderMsg();
        msg.setReminderId(tUserReminder.getId());
        msg.setTitle(remind.getTitle());
        msg.setContent(remind.getContent());
        msg.setRemindTime(remind.getRemindTime());
        msg.setSystemNotify(tUserReminder.getNotifySystem());
        msg.setPosition(WindowPosition.positionMap.get(tUserReminder.getNotifyDesktopPosition()));

        try {
            ReminderWebSocketEndpoint.sendToUser(userId, msg);
        } catch (Exception e) {
            log.error("ws推送失败，userId={}，reminderId={}，err={}",
                    userId, tUserReminder.getId(), e.getMessage(), e);
        }

        // 2. 查询用户级通知配置
        TUserNotifySetting setting = userNotifySettingService.getOne(new LambdaQueryWrapper<TUserNotifySetting>()
                .eq(TUserNotifySetting::getUserId, userId));


        // 统一的文本内容（供各平台复用）
        String notifyText = "来自 uTools 灵犀提醒助手\n"
                + remind.getTitle() + "\n"
                + remind.getContent() + "\n"
                + remind.getRemindTime();

        // 3. 企业微信 —— 不同渠道互不影响，独立 try/catch
        if (setting.getWecomEnabledDefault() == Constants.active || tUserReminder.getWecomBotEnable() == Constants.active) {
            String url = tUserReminder.getNotifyWecomBot();
            if (StringUtils.isBlank(url) && setting != null) {
                url = setting.getWecomBotUrl();
            }

            if (StringUtils.isNotBlank(url)) {
                try {
                    weComRobotTool.sendGroupMessageByUrl(url, notifyText);
                } catch (Exception e) {
                    log.error("企微推送失败，userId={}，url={}，err={}",
                            userId, url, e.getMessage(), e);
                }
            } else {
                log.warn("企微推送跳过，userId={}，未配置 webhook", userId);
            }
        }

        // 4. 钉钉推送（示例）
        if (tUserReminder.getDingdingBotEnable() == Constants.active || setting.getDingdingEnabledDefault() == Constants.active) {
            String url = tUserReminder.getNotifyDingdingBot();
            if (StringUtils.isBlank(url) && setting != null) {
                url = setting.getDingdingBotUrl();
            }

            if (StringUtils.isNotBlank(url)) {
                try {
                     dingTalkRobotTool.sendTextByUrl(url, notifyText);
                } catch (Exception e) {
                    log.error("钉钉推送失败，userId={}，url={}，err={}",
                            userId, url, e.getMessage(), e);
                }
            } else {
                log.warn("钉钉推送跳过，userId={}，未配置 webhook", userId);
            }
        }

//        // 5. Bark 推送（示例）
        if (tUserReminder.getWebhookEnable() == Constants.active || setting.getBarkEnabledDefault() == Constants.active) {
            String url = tUserReminder.getWebhook();
            if (StringUtils.isBlank(url) && setting != null) {
                url = setting.getBarkUrl();
            }

            if (StringUtils.isNotBlank(url)) {
                try {
                     barkPushTool.push(url, remind.getTitle(), remind.getContent());
                } catch (Exception e) {
                    log.error("Bark 推送失败，userId={}，url={}，err={}",
                            userId, url, e.getMessage(), e);
                }
            } else {
                log.warn("Bark 推送跳过，userId={}，未配置 url", userId);
            }
        }

    }
}

