package com.example.controller;

import com.example.service.StatisticsService;
import com.example.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        Map<String, Object> data = statisticsService.getDashboardData();
        return Result.success(data);
    }

    @GetMapping("/ranking")
    public Result<List<Map<String, Object>>> getRanking(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> ranking = statisticsService.getBookSalesRanking(limit);
        return Result.success(ranking);
    }

    @GetMapping("/monthly")
    public Result<List<Map<String, Object>>> getMonthlyStatistics(
            @RequestParam int year,
            @RequestParam(required = false) Integer startMonth,
            @RequestParam(required = false) Integer endMonth) {
        
        List<Map<String, Object>> data;
        if (startMonth != null && endMonth != null) {
            data = statisticsService.getMonthlyStatisticsByRange(year, startMonth, endMonth);
        } else {
            data = statisticsService.getMonthlyStatistics(year);
        }
        
        return Result.success(data);
    }
}
