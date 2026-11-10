package com.qiubai.lab.web.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 表示 app_user 表的一条用户记录。
 * 当前是普通 Java 对象；数据库读写和字段映射将在 Mapper 步骤实现。
 */
// Lombok 注解：在编译时为字段生成 public getter，作用等同于之前手写的读取方法。
@Getter
// 在编译时为可写字段生成 public setter；只负责赋值，不会自动校验或保存数据库。
@Setter
public class User {

    // 对应 BIGINT 主键；新对象尚未插入数据库时，id 可以为 null。
    private Long id;

    // 用户名；数据库负责唯一约束，格式校验后续由应用层实现。
    private String username;

    // 昵称，允许中文。
    private String nickname;

    // 对应 created_at；LocalDateTime 与 DATETIME 都不携带时区。
    private LocalDateTime createdAt;



}
