package com.example.dto;

import lombok.Data;

@Data
public class CustomerDTO {
    private Long id;
    private String name;
    private String gender;
    private String phone;
    private String createdAt;
}
