package com.example.controller;

import com.example.dto.CustomerDTO;
import com.example.dto.CustomerRequest;
import com.example.dto.PageResult;
import com.example.entity.Customer;
import com.example.service.CustomerService;
import com.example.util.Result;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Result<PageResult<CustomerDTO>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String gender,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize) {

        PageInfo<Customer> customerPage = customerService.search(keyword, gender, page, pageSize);

        List<CustomerDTO> customers = customerPage.getList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageResult<CustomerDTO> result = PageResult.of(customers, customerPage.getTotal());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<CustomerDTO> findById(@PathVariable Long id) {
        try {
            Customer customer = customerService.findById(id);
            return Result.success(convertToDTO(customer));
        } catch (Exception e) {
            return Result.notFound(e.getMessage());
        }
    }

    @PostMapping
    public Result<CustomerDTO> create(@Valid @RequestBody CustomerRequest request) {
        try {
            Customer customer = new Customer();
            customer.setName(request.getName());
            customer.setGender(request.getGender());
            customer.setPhone(request.getPhone());
            Customer saved = customerService.save(customer);
            return Result.success(convertToDTO(saved));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<CustomerDTO> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        try {
            Customer customer = new Customer();
            customer.setName(request.getName());
            customer.setGender(request.getGender());
            customer.setPhone(request.getPhone());
            Customer updated = customerService.update(id, customer);
            return Result.success(convertToDTO(updated));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            customerService.delete(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.badRequest(e.getMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setGender(customer.getGender());
        dto.setPhone(customer.getPhone());
        dto.setCreatedAt(customer.getCreatedAt() != null ? customer.getCreatedAt().toString() : null);
        return dto;
    }
}
