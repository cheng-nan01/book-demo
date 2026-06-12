package com.example.controller;

import com.example.dto.SaleDTO;
import com.example.dto.SaleCreateRequest;
import com.example.dto.SaleItemsDTO;
import com.example.dto.PageResult;
import com.example.entity.Sale;
import com.example.entity.SaleItems;
import com.example.service.SaleService;
import com.example.util.Result;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @GetMapping
    public Result<PageResult<SaleDTO>> findAll(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize) {

        LocalDateTime start = startDate != null && !startDate.isEmpty()
                ? LocalDateTime.parse(startDate + "T00:00:00") : null;
        LocalDateTime end = endDate != null && !endDate.isEmpty()
                ? LocalDateTime.parse(endDate + "T23:59:59") : null;

        PageInfo<Sale> salePage = saleService.search(customerId, start, end, page, pageSize);

        List<SaleDTO> sales = salePage.getList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageResult<SaleDTO> result = PageResult.of(sales, salePage.getTotal());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<SaleDTO> findById(@PathVariable Long id) {
        try {
            Sale sale = saleService.findById(id);
            return Result.success(convertToDTO(sale));
        } catch (Exception e) {
            return Result.notFound(e.getMessage());
        }
    }

    @GetMapping("/customer/{customerId}")
    public Result<List<SaleDTO>> findByCustomerId(@PathVariable Long customerId) {
        PageInfo<Sale> sales = saleService.search(customerId, null, null, 1, Integer.MAX_VALUE);
        List<SaleDTO> result = sales.getList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Result.success(result);
    }

    @GetMapping("/book/{bookId}")
    public Result<List<SaleDTO>> findByBookId(@PathVariable Long bookId) {
        List<Sale> sales = saleService.findSalesByBookId(bookId);
        List<SaleDTO> result = sales.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Result.success(result);
    }

    @PostMapping
    public Result<Map<String, Object>> create(@Valid @RequestBody SaleCreateRequest request) {
        try {
            List<Map<String, Object>> items = request.getItems().stream()
                    .map(item -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("bookId", item.getBookId());
                        map.put("quantity", item.getQuantity());
                        return map;
                    })
                    .collect(Collectors.toList());

            Sale sale = saleService.createSale(request.getCustomerId(), request.getSalespersonId(), items);

            Map<String, Object> result = new HashMap<>();
            result.put("id", sale.getId());
            result.put("totalAmount", sale.getTotalAmount());

            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{saleId}/items/{bookId}")
    public Result<Void> updateItem(@PathVariable Long saleId,
                                    @PathVariable Long bookId,
                                    @RequestBody Map<String, Object> body) {
        try {
            Integer quantity = Integer.valueOf(body.get("quantity").toString());
            java.math.BigDecimal unitPrice = new java.math.BigDecimal(body.get("unitPrice").toString());
            saleService.updateSaleItem(saleId, bookId, quantity, unitPrice);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{saleId}/items/{bookId}")
    public Result<Void> deleteItem(@PathVariable Long saleId,
                                    @PathVariable Long bookId) {
        try {
            saleService.deleteSaleItem(saleId, bookId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            saleService.deleteSale(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private SaleDTO convertToDTO(Sale sale) {
        SaleDTO dto = new SaleDTO();
        dto.setId(sale.getId());
        dto.setCustomerId(sale.getCustomerId());
        dto.setName(sale.getName());
        dto.setGender(sale.getGender());
        dto.setSalespersonId(sale.getSalespersonId());
        dto.setSalespersonName(sale.getSalespersonName());
        dto.setSalespersonGender(sale.getSalespersonGender());
        dto.setTotalAmount(sale.getTotalAmount());
        dto.setTotalCost(sale.getTotalCost());
        dto.setSaleDate(sale.getSaleDate() != null ? sale.getSaleDate().toString() : null);

        if (sale.getItems() != null) {
            List<SaleItemsDTO> items = sale.getItems().stream()
                    .map(this::convertItemToDTO)
                    .collect(Collectors.toList());
            dto.setItems(items);
        }

        return dto;
    }

    private SaleItemsDTO convertItemToDTO(SaleItems item) {
        SaleItemsDTO dto = new SaleItemsDTO();
        dto.setSaleId(item.getSaleId());
        dto.setBookId(item.getBookId());
        dto.setTitle(item.getTitle());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setUnitCost(item.getUnitCost());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}
