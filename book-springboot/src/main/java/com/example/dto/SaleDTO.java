package com.example.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerGender;
    private BigDecimal totalAmount;
    private BigDecimal totalCost;
    private String saleDate;
    private List<SaleItemDTO> items;
}
