package com.example.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 销售明细实体 — 对应数据库 sale_items 表。
 * 每条记录代表销售单中一本书的销售详情。
 * bookTitle 来自 LEFT JOIN books 表的查询结果。
 */
@Data
public class SaleItem {

    /** 主键，数据库自增 */
    private Long id;

    /** sale_items 表的外键 sale_id，关联 sales 表 */
    private Long saleId;

    /** sale_items 表的外键 book_id，关联 books 表 */
    private Long bookId;

    /** JOIN books 表获得的书名（不在 sale_items 表中） */
    private String bookTitle;

    /** 该图书的销售数量 */
    private Integer quantity;

    /** 销售时的单价（取自 books.price） */
    private BigDecimal unitPrice;

    /** 销售时的成本（取自 books.cost_price），默认 0 */
    private BigDecimal unitCost = BigDecimal.ZERO;

    /** 小计 = unitPrice × quantity */
    private BigDecimal subtotal;

    /** 计算小计（替代 JPA 的 @PrePersist） */
    public void calculateSubtotal() {
        if (unitPrice != null && quantity != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
