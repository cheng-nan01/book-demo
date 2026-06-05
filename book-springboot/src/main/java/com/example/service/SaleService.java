package com.example.service;

import com.example.entity.Book;
import com.example.entity.Sale;
import com.example.entity.SaleItem;
import com.example.mapper.SaleMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleMapper saleMapper;
    private final BookService bookService;

    public PageInfo<Sale> search(Long customerId, LocalDateTime startDate,
                                  LocalDateTime endDate, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return new PageInfo<>(saleMapper.search(customerId, startDate, endDate));
    }

    public Sale findById(Long id) {
        Sale sale = saleMapper.findById(id);
        if (sale == null) {
            throw new RuntimeException("销售记录不存在");
        }
        sale.setItems(saleMapper.findItemsBySaleId(id));
        return sale;
    }

    /** 查包含某图书的所有销售单 */
    public List<Sale> findSalesByBookId(Long bookId) {
        return saleMapper.findSalesByBookId(bookId);
    }

    @Transactional
    public void deleteSaleItem(Long itemId) {
        SaleItem item = saleMapper.findItemById(itemId);
        if (item == null) throw new RuntimeException("明细不存在");
        // 恢复库存
        bookService.updateStock(item.getBookId(), item.getQuantity());
        // 删明细
        saleMapper.deleteItemById(itemId);
        // 重算销售单总额
        saleMapper.recalcSaleTotals(item.getSaleId());
    }

    @Transactional
    public void updateSaleItem(Long itemId, Integer quantity, BigDecimal unitPrice) {
        SaleItem oldItem = saleMapper.findItemById(itemId);
        if (oldItem == null) throw new RuntimeException("明细不存在");
        // 库存变化 = 旧数量 - 新数量（正数=归还库存，负数=扣库存）
        int stockDelta = oldItem.getQuantity() - quantity;
        bookService.updateStock(oldItem.getBookId(), stockDelta);
        // 更新明细
        SaleItem update = new SaleItem();
        update.setId(itemId);
        update.setQuantity(quantity);
        update.setUnitPrice(unitPrice);
        saleMapper.updateItem(update);
        // 重算销售单总额
        saleMapper.recalcSaleTotals(oldItem.getSaleId());
    }

    @Transactional
    public Sale createSale(Long customerId, List<Map<String, Object>> items) {
        Sale sale = new Sale();
        sale.setCustomerId(customerId);
        sale.prePersist();

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Map<String, Object> item : items) {
            Long bookId = Long.valueOf(item.get("bookId").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());

            Book book = bookService.findById(bookId);

            if (book.getStock() < quantity) {
                throw new RuntimeException("图书《" + book.getTitle() + "》库存不足");
            }

            SaleItem saleItem = new SaleItem();
            saleItem.setBookId(bookId);
            saleItem.setBookTitle(book.getTitle());
            saleItem.setQuantity(quantity);
            saleItem.setUnitPrice(book.getPrice());
            saleItem.setUnitCost(book.getCostPrice());
            saleItem.calculateSubtotal();

            sale.getItems().add(saleItem);

            totalAmount = totalAmount.add(book.getPrice().multiply(BigDecimal.valueOf(quantity)));
            totalCost = totalCost.add(book.getCostPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        sale.setTotalAmount(totalAmount);
        sale.setTotalCost(totalCost);

        saleMapper.insert(sale);

        for (SaleItem item : sale.getItems()) {
            item.setSaleId(sale.getId());
            saleMapper.insertItem(item);
            bookService.decreaseStock(item.getBookId(), item.getQuantity());
        }

        return sale;
    }

    public BigDecimal getTodaySalesAmount() {
        BigDecimal amount = saleMapper.getTodaySalesAmount();
        return amount != null ? amount : BigDecimal.ZERO;
    }

    public Long countTodaySales() {
        return saleMapper.countTodaySales();
    }

    public BigDecimal getTotalProfit(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal profit = saleMapper.getTotalProfit(startDate, endDate);
        return profit != null ? profit : BigDecimal.ZERO;
    }

    @Transactional
    public void deleteSale(Long id) {
        Sale sale = findById(id);

        for (SaleItem item : sale.getItems()) {
            bookService.updateStock(item.getBookId(), item.getQuantity());
        }

        saleMapper.deleteItemsBySaleId(id);
        saleMapper.deleteById(id);
    }
}
