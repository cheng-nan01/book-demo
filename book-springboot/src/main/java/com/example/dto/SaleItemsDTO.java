package com.example.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleItemsDTO {
    private Long saleId;
    private Long bookId;
    private String title;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal unitCost;
    private BigDecimal subtotal;
}
