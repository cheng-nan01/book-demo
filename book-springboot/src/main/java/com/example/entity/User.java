package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体 — 对应数据库 users 表。
 * 用于系统登录认证。
 */
@Data
public class User {

    /** 主键，数据库自增 */
    private Long id;

    /** 登录用户名，唯一索引 */
    private String username;

    /** 密码（ */
    private String password;

    /** 注册时间，插入时自动设置 */
    private LocalDateTime createdAt;

    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
