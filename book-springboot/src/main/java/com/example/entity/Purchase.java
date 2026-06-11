package com.example.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 进货实体 — 对应数据库 purchases 表。
 * title 来自 JOIN books 表的查询结果。
 */
@Data
public class Purchase {

    /** 主键，数据库自增 */
    private Long id;

    /** purchases 表的外键 book_id，关联 books 表 */
    private Long bookId;

    /** JOIN books 表获得的书名（不在 purchases 表中） */
    private String title;

    /** 进货数量 */
    private Integer quantity;

    /** 进货单价 */
    private BigDecimal unitPrice;

    /** 进货总额 = unitPrice × quantity */
    private BigDecimal totalAmount;

    /** 进货时间，插入时自动设为当前时间 */
    private LocalDateTime purchaseDate;

    /** 插入前计算总额并设置时间*/
    public void prePersist() {
        this.purchaseDate = LocalDateTime.now();
        if (unitPrice != null && quantity != null) {
            this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
