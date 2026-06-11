package com.example.dto;

import lombok.Data;

@Data
public class SaleItemsRequest {
    private Long bookId;
    private Integer quantity;
}
