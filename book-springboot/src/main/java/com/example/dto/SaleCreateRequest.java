package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class SaleCreateRequest {
    private Long customerId;
    private List<SaleItemsRequest> items;
}
