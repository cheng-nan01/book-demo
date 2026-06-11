package com.example.mapper;

import com.example.entity.Sale;
import com.example.entity.SaleItems;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 销售表（sales）及销售明细表（sale_items）数据访问接口。
 * 同时承担统计查询（仪表盘、月度报表、排行榜）。
 */
@Mapper
public interface SaleMapper {

    // ===== 销售主表 CRUD =====

    /** 组合条件分页查询：客户 + 日期范围。LEFT JOIN customers 获取客户名和性别 */
    List<Sale> search(@Param("customerId") Long customerId,
                      @Param("startDate") LocalDateTime startDate,
                      @Param("endDate") LocalDateTime endDate);

    /** 按主键查销售单，JOIN customers 获取客户名和性别 */
    Sale findById(Long id);

    /** 按复合主键查询单条销售明细 */
    SaleItems findItemBySaleAndBook(@Param("saleId") Long saleId, @Param("bookId") Long bookId);

    /** 查询某销售单下所有明细行，LEFT JOIN books 获取书名 */
    List<SaleItems> findItemsBySaleId(Long saleId);

    /** 新增销售单，自动回填自增主键 */
    int insert(Sale sale);

    // ===== 销售明细 CRUD =====

    /** 新增一条销售明细 */
    int insertItem(SaleItems item);

    // ===== 删除 =====

    /** 按主键删除销售单 */
    int deleteById(Long id);

    /** 删除某销售单下所有明细行（先删明细，再删主表） */
    int deleteItemsBySaleId(Long saleId);

    // ===== 外键约束检查 =====

    /** 检查是否有销售记录关联某客户（删除客户前校验） */
    boolean existsByCustomerId(Long customerId);

    /** 检查是否有销售记录包含某图书（删除图书前校验） */
    boolean existsItemByBookId(Long bookId);

    /** 查找包含指定图书的所有销售单，每单带完整明细列表 */
    List<Sale> findSalesByBookId(@Param("bookId") Long bookId);

    /** 删除单条销售明细 */
    int deleteItemBySaleAndBook(@Param("saleId") Long saleId, @Param("bookId") Long bookId);

    /** 更新一条销售明细（按 saleId + bookId 定位） */
    int updateItem(SaleItems item);

    /** 明细变动后重算销售单总金额和总成本 */
    int recalcSaleTotals(@Param("saleId") Long saleId);

    // ===== 仪表盘统计 =====

    /** 今日销售额（SUM total_amount WHERE DATE = 今天） */
    BigDecimal getTodaySalesAmount();

    /** 今日订单数 */
    long countTodaySales();

    /** 某时间段的利润（销售额 - 成本） */
    BigDecimal getTotalProfit(@Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);

    // ===== 图表统计 =====

    /** 图书销量排行榜：GROUP BY book_id ORDER BY 销量 DESC */
    List<Map<String, Object>> getBookSalesRanking(int limit);

    /** 某年的月度销售额统计：GROUP BY month */
    List<Map<String, Object>> getMonthlyStatistics(int year);

    /** 某年某几个月的销售额统计：GROUP BY month（按月份范围过滤） */
    List<Map<String, Object>> getMonthlyStatisticsByRange(@Param("year") int year,
                                                           @Param("startMonth") int startMonth,
                                                           @Param("endMonth") int endMonth);

    // ===== 总计数 =====

    /** 图书总数 */
    long getTotalBooksCount();

    /** 客户总数 */
    long getTotalCustomersCount();

    /** 本月利润 = 本月所有销售的 (total_amount - total_cost) 之和 */
    BigDecimal getCurrentMonthProfit();
}
