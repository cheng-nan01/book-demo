package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BookCreateRequest {
    @NotBlank(message = "ISBN不能为空")
    private String isbn;

    @NotBlank(message = "书名不能为空")
    private String title;

    @NotBlank(message = "作者不能为空")
    private String author;

    private String publisher;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private BigDecimal costPrice;

    @NotNull(message = "库存不能为空")
    private Integer stock;
}
