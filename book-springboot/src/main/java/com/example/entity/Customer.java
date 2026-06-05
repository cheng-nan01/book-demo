package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客户实体 — 对应数据库 customers 表。
 */
@Data
public class Customer {

    /** 主键，数据库自增 */
    private Long id;

    /** 客户姓名，不可为空 */
    private String name;

    /** 性别：男/女 */
    private String gender;

    /** 手机号 */
    private String phone;

    /** 创建时间，插入时自动设置 */
    private LocalDateTime createdAt;

    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
