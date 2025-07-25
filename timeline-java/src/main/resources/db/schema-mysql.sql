DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`
(
    `table_id`      bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    `table_name`    varchar(200) NULL DEFAULT '' COMMENT '表名称',
    `table_comment` varchar(500) NULL DEFAULT '' COMMENT '表描述',
    `create_time`   datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`table_id`) USING BTREE,
    UNIQUE INDEX idx_tablename (table_name)
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4  COMMENT = '代码生成业务表' ROW_FORMAT = DYNAMIC;


DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`
(
    `column_id`      bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    `table_id`       bigint NOT NULL COMMENT '归属表编号',
    `column_name`    varchar(200) NULL DEFAULT NULL COMMENT '列名称',
    `column_comment` varchar(500) NULL DEFAULT NULL COMMENT '列描述',
    `column_type`    varchar(100) NULL DEFAULT NULL COMMENT '列类型',
    `java_type`      varchar(500) NULL DEFAULT NULL COMMENT 'JAVA类型',
    `java_field`     varchar(200) NULL DEFAULT NULL COMMENT 'JAVA字段名',
    `is_pk`          char(1) NULL DEFAULT NULL COMMENT '是否主键（1是）',
    `is_required`    char(1) NULL DEFAULT NULL COMMENT '是否必填（1是）',
    `is_insert`      char(1) NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
    `is_edit`        char(1) NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
    `is_list`        char(1) NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
    `is_query`       char(1) NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
    `query_type`     varchar(200) NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
    `html_type`      varchar(200) NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
    `sort`           int NULL DEFAULT NULL COMMENT '排序',
    PRIMARY KEY (`column_id`) USING BTREE,
    UNIQUE INDEX idx_table_id_column (table_id, column_name)
) ENGINE = InnoDB AUTO_INCREMENT = 263 CHARACTER SET = utf8mb4  COMMENT = '代码生成业务表字段' ROW_FORMAT = DYNAMIC;


-- 用户表：记录微信小程序用户信息
CREATE TABLE `users` (
                        `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
                        `openid` VARCHAR(64) NOT NULL COMMENT '微信用户OpenID',
                        `nickname` VARCHAR(64) DEFAULT NULL COMMENT '用户昵称',
                        `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        UNIQUE KEY `uq_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 时间轴表：一个用户可创建多个时间轴，用于记录不同主题的事件
CREATE TABLE `timeline` (
                            `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '时间轴ID',
                            `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
                            `title` VARCHAR(64) NOT NULL COMMENT '时间轴标题，例如“孩子成长”、“恋爱历程”',
                            `description` TEXT COMMENT '时间轴简介或备注',
                            `tag` VARCHAR(255) DEFAULT NULL COMMENT '事件分类',
                            `cover_url` VARCHAR(255) DEFAULT NULL COMMENT '封面图URL（用于首页展示）',
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                            FOREIGN KEY (`user_id`) REFERENCES `user_profile`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间轴表';

-- 事件表：记录某个时间轴中的具体事件，支持纯文本或富文本内容
CREATE TABLE `event` (
                         `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '事件ID',
                         `timeline_id` BIGINT NOT NULL COMMENT '所属时间轴ID',
                         `title` VARCHAR(255) NOT NULL COMMENT '事件标题',
                         `is_rich` TINYINT DEFAULT 0  COMMENT '是否是富文本',
                         `content` LONGTEXT COMMENT '事件内容，支持富文本或纯文本（HTML 格式）',
                         `tag` VARCHAR(255) DEFAULT NULL COMMENT '事件分类',
                         `location` VARCHAR(255) DEFAULT NULL COMMENT '事件地点',
                         `images` JSON DEFAULT NULL COMMENT '图片 OSS key 列表（JSON数组）',
                         `event_time` DATETIME NOT NULL COMMENT '事件发生时间',
                         `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                         `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                         FOREIGN KEY (`timeline_id`) REFERENCES `timeline`(`id`),
                         INDEX `idx_event_time` (`event_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间轴事件表';