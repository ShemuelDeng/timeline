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
    repeat_rule VARCHAR(64) DEFAULT NULL COMMENT '重复规则，如 DAILY, WEEKLY, MONTHLY',
    status TINYINT(1) DEFAULT 1 COMMENT '提醒状态，0：待提醒，1：已过期，2：已完成',
    visible TINYINT(1) DEFAULT 1 COMMENT '是否可以见1：可见， 0：不可见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (template_id) REFERENCES t_reminder_template(id)
) COMMENT='用户提醒表';


CREATE TABLE t_user_reminder_field (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   user_reminder_id BIGINT NOT NULL COMMENT '用户提醒ID',
   field_name VARCHAR(64) NOT NULL COMMENT '字段名',
   field_value VARCHAR(255) COMMENT '字段值',
   FOREIGN KEY (user_reminder_id) REFERENCES t_user_reminder(id)
) COMMENT='用户提醒的扩展字段值表';
