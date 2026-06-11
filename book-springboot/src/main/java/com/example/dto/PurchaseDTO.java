package com.example.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PurchaseDTO {
    private Long id;
    private Long bookId;
    private String title;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String purchaseDate;
}
