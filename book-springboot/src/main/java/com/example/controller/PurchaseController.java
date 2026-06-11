package com.example.controller;

import com.example.dto.PurchaseDTO;
import com.example.dto.PurchaseCreateRequest;
import com.example.entity.Purchase;
import com.example.service.PurchaseService;
import com.example.util.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @GetMapping
    public Result<List<PurchaseDTO>> findAll() {
        List<PurchaseDTO> purchases = purchaseService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Result.success(purchases);
    }

    @GetMapping("/{id}")
    public Result<PurchaseDTO> findById(@PathVariable Long id) {
        try {
            Purchase purchase = purchaseService.findById(id);
            return Result.success(convertToDTO(purchase));
        } catch (Exception e) {
            return Result.notFound(e.getMessage());
        }
    }

    @GetMapping("/book/{bookId}")
    public Result<List<PurchaseDTO>> findByBookId(@PathVariable Long bookId) {
        List<PurchaseDTO> purchases = purchaseService.findByBookId(bookId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Result.success(purchases);
    }

    @PostMapping
    public Result<PurchaseDTO> create(@Valid @RequestBody PurchaseCreateRequest request) {
        try {
            Purchase purchase = purchaseService.createPurchase(
                    request.getBookId(),
                    request.getQuantity(),
                    request.getUnitPrice()
            );
            return Result.success(convertToDTO(purchase));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private PurchaseDTO convertToDTO(Purchase purchase) {
        PurchaseDTO dto = new PurchaseDTO();
        dto.setId(purchase.getId());
        dto.setBookId(purchase.getBookId());
        dto.setTitle(purchase.getTitle());
        dto.setQuantity(purchase.getQuantity());
        dto.setUnitPrice(purchase.getUnitPrice());
        dto.setTotalAmount(purchase.getTotalAmount());
        dto.setPurchaseDate(purchase.getPurchaseDate() != null ? purchase.getPurchaseDate().toString() : null);
        return dto;
    }
}
