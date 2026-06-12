package com.example.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 销售实体 — 对应数据库 sales 表。
 * 客户信息（name / gender）来自 JOIN customers 表的查询结果。
 * 明细列表（items）来自 sale_items 表，由 Mapper 层单独查询后填充。
 */
@Data
public class Sale {

    /** 主键 sale_id，数据库自增（别名映射为 id） */
    private Long id;

    /** sales 表的外键 customer_id */
    private Long customerId;

    /** JOIN customers 表获得的客户名（不在 sales 表中） */
    private String name;

    /** JOIN customers 表获得的客户性别（不在 sales 表中） */
    private String gender;

    /** sales 表的外键 salesperson_id */
    private Long salespersonId;

    /** JOIN salespeople 表获得的销售员姓名（不在 sales 表中） */
    private String salespersonName;

    /** JOIN salespeople 表获得的销售员性别（不在 sales 表中） */
    private String salespersonGender;

    /** 订单总金额 = 所有明细 subtotal 之和 */
    private BigDecimal totalAmount;

    /** 订单总成本 = 所有明细 unitCost * quantity 之和，默认 0 */
    private BigDecimal totalCost = BigDecimal.ZERO;

    /** 销售时间，插入时自动设为当前时间 */
    private LocalDateTime saleDate;

    /** 销售明细列表，不直接对应数据库列，由 Mapper 单独查询 sale_items 表后填充 */
    private List<SaleItems> items = new ArrayList<>();

    public void prePersist() {
        this.saleDate = LocalDateTime.now();
    }
}
