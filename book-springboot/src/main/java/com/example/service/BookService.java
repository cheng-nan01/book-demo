package com.example.service;

import com.example.entity.Book;
import com.example.mapper.BookMapper;
import com.example.mapper.SaleMapper;
import com.example.mapper.PurchaseMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final SaleMapper saleMapper;
    private final PurchaseMapper purchaseMapper;

    public PageInfo<Book> search(String keyword, String stockStatus, String publisher,
                                  int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return new PageInfo<>(bookMapper.search(keyword, stockStatus, publisher));
    }

    public Book findById(Long id) {
        if (!bookMapper.existsById(id)) {
            throw new RuntimeException("图书不存在");
        }
        return bookMapper.findById(id);
    }

    /** 查 ISBN 是否已存在（直接按存储值查） */
    private boolean isbnExists(String raw, Long excludeId) {
        Book existing = bookMapper.findByIsbn(raw);
        return existing != null && !existing.getId().equals(excludeId);
    }

    @Transactional
    public Book save(Book book) {
        String raw = book.getIsbn();
        if (raw != null && !raw.trim().isEmpty()) {
            if (isbnExists(raw, -1L)) {
                throw new RuntimeException("该ISBN已存在，不能重复添加");
            }
        }
        book.prePersist();
        bookMapper.insert(book);
        return book;
    }

    @Transactional
    public Book update(Long id, Book bookDetails) {
        Book book = findById(id);

        if (bookDetails.getIsbn() != null && !bookDetails.getIsbn().trim().isEmpty()) {
            if (isbnExists(bookDetails.getIsbn(), id)) {
                throw new RuntimeException("该ISBN已被其他图书使用");
            }
            book.setIsbn(bookDetails.getIsbn());
        }

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setPublisher(bookDetails.getPublisher());
        book.setPrice(bookDetails.getPrice());
        book.setCostPrice(bookDetails.getCostPrice());
        book.setStock(bookDetails.getStock());
        book.preUpdate();
        bookMapper.update(book);
        return book;
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        if (saleMapper.existsItemByBookId(id)) {
            throw new RuntimeException("该图书存在销售记录，无法删除");
        }
        purchaseMapper.deleteByBookId(id);
        bookMapper.deleteById(id);
    }

    @Transactional
    public void updateStock(Long id, int quantity) {
        Book book = findById(id);
        book.setStock(book.getStock() + quantity);
        book.preUpdate();
        bookMapper.update(book);
    }

    @Transactional
    public void decreaseStock(Long id, int quantity) {
        Book book = findById(id);
        if (book.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }
        book.setStock(book.getStock() - quantity);
        book.preUpdate();
        bookMapper.update(book);
    }
}
