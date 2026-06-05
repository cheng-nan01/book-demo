package com.example.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PurchaseCreateRequest {
    private Long bookId;
    private Integer quantity;
    private BigDecimal unitPrice;
}
