package com.example.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleDTO {
    private Long id;
    private Long customerId;
    private String name;
    private String gender;
    private BigDecimal totalAmount;
    private BigDecimal totalCost;
    private String saleDate;
    private List<SaleItemsDTO> items;
}
