-- 已于 2026-11-10 执行并验证。表已存在，不要直接重复执行。
-- 显式指定练习数据库，不操作其他项目的数据库。
USE lab_web_foundation;

CREATE TABLE app_user (
    -- 用户唯一编号；插入时由数据库生成，不要求编号连续。
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',

    -- 用户名最多32个字符；格式与最小长度后续在应用层校验。
    username VARCHAR(32) NOT NULL COMMENT '用户名',

    -- 昵称允许中文；NOT NULL 不代表禁止空字符串或纯空白。
    nickname VARCHAR(50) NOT NULL COMMENT '昵称',

    -- 插入时省略该列，数据库自动填入当时的时间；DATETIME 本身不携带时区。
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    -- 主键用于唯一定位一条用户记录。
    PRIMARY KEY (id),

    -- 数据库保证用户名唯一；在本表的排序规则下不区分大小写。
    UNIQUE KEY uk_app_user_username (username)
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '用户基础练习表';
