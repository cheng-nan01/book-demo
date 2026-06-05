package com.example.service;

import com.example.entity.Book;
import com.example.entity.Purchase;
import com.example.mapper.PurchaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseMapper purchaseMapper;
    private final BookService bookService;

    public List<Purchase> findAll() {
        return purchaseMapper.findAll();
    }

    public Purchase findById(Long id) {
        Purchase purchase = purchaseMapper.findById(id);
        if (purchase == null) {
            throw new RuntimeException("进货记录不存在");
        }
        return purchase;
    }

    public List<Purchase> findByBookId(Long bookId) {
        return purchaseMapper.findByBookId(bookId);
    }

    @Transactional
    public Purchase createPurchase(Long bookId, Integer quantity, BigDecimal unitPrice) {
        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("进货数量必须大于0");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("进货单价必须大于0");
        }

        Book book = bookService.findById(bookId);

        Purchase purchase = new Purchase();
        purchase.setBookId(bookId);
        purchase.setBookTitle(book.getTitle());
        purchase.setQuantity(quantity);
        purchase.setUnitPrice(unitPrice);
        purchase.setTotalAmount(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        purchase.prePersist();

        purchaseMapper.insert(purchase);
        bookService.updateStock(bookId, quantity);

        return purchase;
    }
}
