package com.example.controller;

import com.example.dto.BookDTO;
import com.example.dto.BookCreateRequest;
import com.example.dto.BookUpdateRequest;
import com.example.dto.PageResult;
import com.example.entity.Book;
import com.example.service.BookService;
import com.example.util.Result;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public Result<PageResult<BookDTO>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String stockStatus,
            @RequestParam(required = false) String publisher,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize) {

        PageInfo<Book> bookPage = bookService.search(keyword, stockStatus, publisher, page, pageSize);

        List<BookDTO> books = bookPage.getList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageResult<BookDTO> result = PageResult.of(books, bookPage.getTotal());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<BookDTO> findById(@PathVariable Long id) {
        try {
            Book book = bookService.findById(id);
            return Result.success(convertToDTO(book));
        } catch (Exception e) {
            return Result.notFound(e.getMessage());
        }
    }

    @PostMapping
    public Result<BookDTO> create(@Valid @RequestBody BookCreateRequest request) {
        try {
            Book book = new Book();
            book.setIsbn(request.getIsbn());
            book.setTitle(request.getTitle());
            book.setAuthor(request.getAuthor());
            book.setPublisher(request.getPublisher());
            book.setPrice(request.getPrice());
            book.setCostPrice(request.getCostPrice());
            book.setStock(request.getStock());
            Book saved = bookService.save(book);
            return Result.success(convertToDTO(saved));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<BookDTO> update(@PathVariable Long id, @Valid @RequestBody BookUpdateRequest request) {
        try {
            Book book = new Book();
            book.setIsbn(request.getIsbn());
            book.setTitle(request.getTitle());
            book.setAuthor(request.getAuthor());
            book.setPublisher(request.getPublisher());
            book.setPrice(request.getPrice());
            book.setCostPrice(request.getCostPrice());
            book.setStock(request.getStock());
            Book updated = bookService.update(id, book);
            return Result.success(convertToDTO(updated));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            bookService.delete(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.badRequest(e.getMessage());
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setIsbn(book.getIsbn());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPublisher(book.getPublisher());
        dto.setPrice(book.getPrice());
        dto.setCostPrice(book.getCostPrice());
        dto.setStock(book.getStock());
        dto.setCreatedAt(book.getCreatedAt() != null ? book.getCreatedAt().toString() : null);
        dto.setUpdatedAt(book.getUpdatedAt() != null ? book.getUpdatedAt().toString() : null);
        return dto;
    }
}
