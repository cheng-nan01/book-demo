package com.example.service;

import com.example.mapper.SaleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final SaleMapper saleMapper;
    private final SaleService saleService;

    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("bookCount", saleMapper.getTotalBooksCount());
        dashboard.put("customerCount", saleMapper.getTotalCustomersCount());
        dashboard.put("todaySales", saleService.getTodaySalesAmount());
        dashboard.put("monthProfit", saleMapper.getCurrentMonthProfit());
        return dashboard;
    }

    public List<Map<String, Object>> getBookSalesRanking(int limit) {
        List<Map<String, Object>> results = saleMapper.getBookSalesRanking(limit);
        List<Map<String, Object>> ranking = new ArrayList<>();

        for (Map<String, Object> row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("bookId", row.get("id"));
            item.put("title", row.get("title"));
            item.put("author", row.get("author"));
            item.put("salesCount", toLong(row.get("sales_count")));
            item.put("salesAmount", row.get("sales_amount"));
            ranking.add(item);
        }

        return ranking;
    }

    public List<Map<String, Object>> getMonthlyStatistics(int year) {
        return mapMonthlyResults(saleMapper.getMonthlyStatistics(year));
    }

    public List<Map<String, Object>> getMonthlyStatisticsByRange(int year, int startMonth, int endMonth) {
        return mapMonthlyResults(saleMapper.getMonthlyStatisticsByRange(year, startMonth, endMonth));
    }

    private List<Map<String, Object>> mapMonthlyResults(List<Map<String, Object>> results) {
        List<Map<String, Object>> statistics = new ArrayList<>();
        for (Map<String, Object> row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("year", row.get("year"));
            item.put("month", toInt(row.get("month")));
            BigDecimal amount = toBigDecimal(row.get("total_amount"));
            BigDecimal cost = toBigDecimal(row.get("total_cost"));
            item.put("totalAmount", amount);
            item.put("salesAmount", amount);
            item.put("totalCost", cost);
            if (amount != null && cost != null) {
                item.put("profit", amount.subtract(cost));
            } else {
                item.put("profit", BigDecimal.ZERO);
            }
            statistics.add(item);
        }
        return statistics;
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal bd) return bd;
        return new BigDecimal(val.toString());
    }

    private Long toLong(Object val) {
        if (val == null) return 0L;
        if (val instanceof Long l) return l;
        if (val instanceof Number n) return n.longValue();
        return Long.parseLong(val.toString());
    }

    private Integer toInt(Object val) {
        if (val == null) return 0;
        if (val instanceof Integer i) return i;
        if (val instanceof Number n) return n.intValue();
        return Integer.parseInt(val.toString());
    }
}
