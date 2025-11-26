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

    public void pushReminder(TUserReminder tUserReminder, TUserReminderItem remindItem) {
        Long userId = remindItem.getUserId();

        // 1. 构造 WS 消息并推送
        pushWebSocket(tUserReminder, remindItem, userId);

        // 2. 查询用户级通知配置（可能为 null）
        TUserNotifySetting setting = userNotifySettingService.getOne(
                new LambdaQueryWrapper<TUserNotifySetting>()
                        .eq(TUserNotifySetting::getUserId, userId)
        );

        // 统一的文本内容（供各平台复用）
        String notifyText = buildNotifyText(remindItem);

        // 3. 企业微信
        pushWeCom(tUserReminder, setting, userId, notifyText);

        // 4. 钉钉
        pushDingTalk(tUserReminder, setting, userId, notifyText);

        // 5. Bark
        pushBark(tUserReminder,remindItem, setting, userId);
    }



    private void pushWebSocket(TUserReminder tUserReminder, TUserReminderItem remindItem, Long userId) {
        ReminderMsg msg = new ReminderMsg();
        msg.setReminderId(tUserReminder.getId());
        msg.setTitle(remindItem.getTitle());
        msg.setContent(remindItem.getContent());
        msg.setRemindTime(remindItem.getRemindTime());
        msg.setSystemNotify(tUserReminder.getNotifySystem());
        msg.setPosition(WindowPosition.positionMap.get(tUserReminder.getNotifyDesktopPosition()));

        try {
            ReminderWebSocketEndpoint.sendToUser(userId, msg);
        } catch (Exception e) {
            log.error("ws推送失败，userId={}，reminderId={}，err={}",
                    userId, tUserReminder.getId(), e.getMessage(), e);
        }
    }

    /**
     * 统一构造文本通知内容
     */
    private String buildNotifyText(TUserReminderItem remind) {
        StringBuilder sb = new StringBuilder();
        sb.append("来自 uTools 灵犀提醒助手\n");
        sb.append(remind.getTitle()).append("\n");
        if (StringUtils.isNotEmpty(remind.getContent())) {
            sb.append(remind.getContent()).append("\n");
        }
        sb.append(remind.getRemindTime());
        return sb.toString();
    }

    /**
     * 企业微信推送
     */
    private void pushWeCom(TUserReminder tUserReminder,
                           TUserNotifySetting setting,
                           Long userId,
                           String notifyText) {

        boolean enabledBySetting = setting != null && setting.getWecomEnabledDefault() == Constants.active;
        boolean enabledByReminder = tUserReminder.getWecomBotEnable() == Constants.active;

        if (!enabledBySetting && !enabledByReminder) {
            return;
        }

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

    /**
     * 钉钉推送
     */
    private void pushDingTalk(TUserReminder tUserReminder,
                              TUserNotifySetting setting,
                              Long userId,
                              String notifyText) {

        boolean enabledBySetting = setting != null && setting.getDingdingEnabledDefault() == Constants.active;
        boolean enabledByReminder = tUserReminder.getDingdingBotEnable() == Constants.active;

        if (!enabledBySetting && !enabledByReminder) {
            return;
        }

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

    /**
     * Bark 推送
     */
    private void pushBark(TUserReminder tUserReminder,
                          TUserReminderItem remindItem,
                          TUserNotifySetting setting,
                          Long userId) {

        boolean enabledBySetting = setting != null && setting.getBarkEnabledDefault() == Constants.active;
        boolean enabledByReminder = tUserReminder.getWebhookEnable() == Constants.active;

        if (!enabledBySetting && !enabledByReminder) {
            return;
        }

        String url = tUserReminder.getWebhook();
        if (StringUtils.isBlank(url) && setting != null) {
            url = setting.getBarkUrl();
        }

        if (StringUtils.isNotBlank(url)) {
            try {
                barkPushTool.push(url, remindItem.getTitle(), remindItem.getContent());
            } catch (Exception e) {
                log.error("Bark 推送失败，userId={}，url={}，err={}",
                        userId, url, e.getMessage(), e);
            }
        } else {
            log.warn("Bark 推送跳过，userId={}，未配置 url", userId);
        }
    }

}

