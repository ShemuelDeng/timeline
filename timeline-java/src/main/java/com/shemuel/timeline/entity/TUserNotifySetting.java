package com.shemuel.timeline.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
@TableName("t_user_notify_setting")
@Tag(name = "用户通知渠道配置表对象")
public class TUserNotifySetting implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "")
    private Long id;

    @Schema(description = "用户ID（跟提醒表里的 user_id 对应）")
    private Long userId;

    @Schema(description = "企业微信机器人 Webhook")
    private String wecomBotUrl;

    @Schema(description = "钉钉机器人 Webhook")
    private String dingdingBotUrl;

    @Schema(description = "Bark 推送 URL")
    private String barkUrl;


    @Schema(description = "")
    private LocalDateTime createTime;

    @Schema(description = "")
    private LocalDateTime updateTime;
}
