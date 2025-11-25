CREATE TABLE t_reminder_category (
     id BIGINT PRIMARY KEY AUTO_INCREMENT,
     name VARCHAR(64) NOT NULL COMMENT '分类名称，如健康、生活、工作',
     code VARCHAR(32) UNIQUE NOT NULL COMMENT '分类编码，如 HEALTH, LIFE, WORK',
     sort_order INT DEFAULT 0 COMMENT '排序字段',
     create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
     update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='提醒分类表';

CREATE TABLE t_reminder_template (
     id BIGINT PRIMARY KEY AUTO_INCREMENT,
     category_id BIGINT NOT NULL COMMENT '所属分类ID',
     name VARCHAR(128) NOT NULL COMMENT '模板名称，如喝水提醒、运动提醒',
     description TEXT COMMENT '模板描述',
     icon VARCHAR(255) COMMENT '图标URL',
     is_system TINYINT(1) DEFAULT 1 COMMENT '是否系统模板，1系统，0用户自定义模板',
     user_id BIGINT DEFAULT NULL COMMENT '创建人（系统模板为空）',
     create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
     update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     FOREIGN KEY (category_id) REFERENCES t_reminder_category(id)
) COMMENT='提醒模板表';

CREATE TABLE t_reminder_template_field (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   template_id BIGINT NOT NULL COMMENT '模板ID',
   field_name VARCHAR(64) NOT NULL COMMENT '字段名，如 pay_type, sport_type',
   field_label VARCHAR(64) NOT NULL COMMENT '字段显示名，如 缴费类型, 运动类型',
   field_type VARCHAR(32) NOT NULL COMMENT '字段类型，如 text, select, number, date',
   field_options TEXT COMMENT '可选项(JSON数组)，例如 ["电费","水费","燃气费"]',
   sort_order INT DEFAULT 0,
   create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (template_id) REFERENCES t_reminder_template(id)
) COMMENT='模板扩展字段定义表';


CREATE TABLE t_user_reminder (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户微信openID',
    template_id BIGINT DEFAULT NULL COMMENT '引用的模板ID，可为空',
    title VARCHAR(128) NOT NULL COMMENT '提醒标题',
    content TEXT COMMENT '提醒内容',
    remind_time DATETIME NOT NULL COMMENT '提醒时间',
    repeat_rule VARCHAR(64) DEFAULT NULL COMMENT '重复规则：NONE, DAILY, WEEKLY, MONTHLY, YEARLY,WORKDAY, CUSTOM',
    custom_mode VARCHAR(64) DEFAULT NULL COMMENT '当重复规则是自定义时，WEEKLY, YEARLY, MEDICINE,ANNIVERSARY 自定义模式： 按月，按年， 用药提醒，纪念日',
    advance_days INT NOT NULL DEFAULT 0 COMMENT '提起几天提醒',
    repeat_interval INT NOT NULL DEFAULT 1 COMMENT '间隔数，如每2天/每2周',
    repeat_weekdays SET('1','2','3','4','5','6','7') NULL COMMENT '每周的星期几',
    repeat_month_days VARCHAR(256) NULL COMMENT '每月的哪几天，逗号分隔',
    specify_dates TEXT DEFAULT NULL COMMENT '自定义某年的哪些天 逗号分隔',
    specify_times TEXT DEFAULT NULL COMMENT '自定义当天的哪几个时间， 逗号分隔',

    status TINYINT(1) DEFAULT 0 COMMENT '提醒状态，0：待提醒，1：已过期，2：已完成',
    active TINYINT(1) DEFAULT 1 COMMENT '是否开启：1：开启， 0：关闭',
    do_circle TINYINT(1) DEFAULT 0 COMMENT '是否循环：1：开启， 0：关闭',
    circle_begin DATETIME DEFAULT NULL COMMENT '循环开始时间',
    circle_end DATETIME DEFAULT NULL COMMENT '循环结束时间',
    circle_interval TINYINT(1) DEFAULT 20 COMMENT '循环间隔，如 20， 单位分钟',

    notify_desktop  TINYINT(1) NOT NULL DEFAULT 1 COMMENT '桌面弹窗',
    notify_desktop_position  TINYINT(1)  DEFAULT 0 COMMENT '弹窗位置,0:中间，1，左上，2 左下，3，右上，4，右下',
    notify_wx  TINYINT(1) NOT NULL DEFAULT 1 COMMENT '微信提醒',
    notify_wecom_bot  VARCHAR(1024) NOT NULL DEFAULT 1 COMMENT '企业微信机器人url',
    notify_dingding_bot  VARCHAR(1024) NOT NULL DEFAULT 1 COMMENT '钉钉机器人url',

    wecom_bot_enable  TINYINT(1) DEFAULT 0 COMMENT '企业微信机器人url是否开启',
    dingding_bot_enable  TINYINT(1) DEFAULT 0  COMMENT '钉钉机器人url是否开启',

    notify_sound    TINYINT(1) NOT NULL DEFAULT 1 COMMENT '声音提醒',
    notify_system   TINYINT(1) NOT NULL DEFAULT 0 COMMENT '系统通知/托盘气泡',
    notify_sound_file    varchar(128) DEFAULT NULL  COMMENT '声音文件',

    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    webhook VARCHAR(1024) DEFAULT NULL COMMENT '提醒钩子',
    webhook_method VARCHAR(16) DEFAULT 'POST'
    FOREIGN KEY (template_id) REFERENCES t_reminder_template(id)
) COMMENT='用户提醒表主表， 只记录用户需要的提醒类型，方式';




CREATE TABLE t_user_reminder_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    main_id BIGINT NOT NULL COMMENT '主表ID',
    user_id BIGINT NOT NULL COMMENT '用户微信openID',
    template_id BIGINT DEFAULT NULL COMMENT '引用的模板ID，可为空',
    title VARCHAR(128) NOT NULL COMMENT '提醒标题',
    content TEXT COMMENT '提醒内容',
    remind_time DATETIME NOT NULL COMMENT '提醒时间',
    repeat_rule VARCHAR(64) DEFAULT NULL COMMENT '重复规则，如 DAILY, WEEKLY, MONTHLY',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (template_id) REFERENCES t_reminder_template(id)
) COMMENT='用户提醒表子表， 主要记录由主表产生的确切的提示项';

CREATE TABLE t_user_reminder_field (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   user_reminder_id BIGINT NOT NULL COMMENT '用户提醒ID',
   field_name VARCHAR(64) NOT NULL COMMENT '字段名',
   field_value VARCHAR(255) COMMENT '字段值',
   FOREIGN KEY (user_reminder_id) REFERENCES t_user_reminder(id)
) COMMENT='用户提醒的扩展字段值表';



CREATE TABLE `t_user_notify_setting` (
 `id` bigint NOT NULL AUTO_INCREMENT,
 `user_id` bigint NOT NULL COMMENT '用户ID（跟提醒表里的 user_id 对应）',

 `wecom_bot_url` varchar(512) DEFAULT NULL COMMENT '企业微信机器人 Webhook',
 `dingding_bot_url` varchar(512) DEFAULT NULL COMMENT '钉钉机器人 Webhook',
 `bark_url` varchar(512) DEFAULT NULL COMMENT 'Bark 推送 URL',
 `bark_enabled_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Bark 默认开启：1 开启，0 关闭',
 `wecom_enabled_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '企业微信机器人默认开启：1 开启，0 关闭',
 `dingding_enabled_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '钉钉机器人默认开启：1 开启，0 关闭',
 `webhook_enabled_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '通用 Webhook 默认开启：1 开启，0 关闭',

 `dingding_secret` varchar(255) DEFAULT NULL COMMENT '钉钉机器人加签 Secret',

 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 PRIMARY KEY (`id`),
 UNIQUE KEY `uk_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户通知渠道配置表';
