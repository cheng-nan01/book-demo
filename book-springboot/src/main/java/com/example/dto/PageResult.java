package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;
    private Long total;
    
    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }
    
    public static <T> PageResult<T> of(List<T> list, Long total) {
        return new PageResult<>(list, total);
    }
}
