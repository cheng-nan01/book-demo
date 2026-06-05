package com.example.dto;

import lombok.Data;

@Data
public class SaleItemRequest {
    private Long bookId;
    private Integer quantity;
}
