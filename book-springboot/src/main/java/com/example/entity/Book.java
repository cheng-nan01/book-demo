package com.example.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 图书实体 — 对应数据库 books 表。
 * 所有数据库列通过 MyBatis XML 的 resultMap 映射到此类的字段。
 */
@Data
public class Book {

    /** 主键，数据库自增 */
    private Long id;

    /** ISBN 号，唯一索引，不可为空 */
    private String isbn;

    /** 书名，不可为空 */
    private String title;

    /** 作者 */
    private String author;

    /** 出版社 */
    private String publisher;

    /** 零售价 */
    private BigDecimal price;

    /** 进货成本价，默认 0 */
    private BigDecimal costPrice = BigDecimal.ZERO;

    /** 当前库存数量，默认 0 */
    private Integer stock = 0;

    /** 创建时间，插入时自动设置 */
    private LocalDateTime createdAt;

    /** 更新时间，编辑时自动刷新 */
    private LocalDateTime updatedAt;

    /** 插入前初始化创建时间和更新时间 */
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /** 更新前刷新更新时间 */
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
