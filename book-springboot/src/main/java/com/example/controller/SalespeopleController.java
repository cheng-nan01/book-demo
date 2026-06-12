package com.example.controller;

import com.example.dto.PageResult;
import com.example.entity.Salespeople;
import com.example.service.SalespeopleService;
import com.example.util.Result;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salespeople")
@RequiredArgsConstructor
public class SalespeopleController {

    private final SalespeopleService salespeopleService;

    @GetMapping
    public Result<PageResult<Salespeople>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        PageInfo<Salespeople> pageInfo = salespeopleService.search(keyword, page, pageSize);
        return Result.success(PageResult.of(pageInfo.getList(), pageInfo.getTotal()));
    }

    @PostMapping
    public Result<Salespeople> create(@RequestBody Salespeople salespeople) {
        try {
            return Result.success(salespeopleService.save(salespeople));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Salespeople> update(@PathVariable Long id, @RequestBody Salespeople salespeople) {
        try {
            return Result.success(salespeopleService.update(id, salespeople));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            salespeopleService.delete(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
