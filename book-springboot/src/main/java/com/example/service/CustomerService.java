package com.example.service;

import com.example.entity.Customer;
import com.example.mapper.CustomerMapper;
import com.example.mapper.SaleMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerMapper customerMapper;
    private final SaleMapper saleMapper;

    public Customer findById(Long id) {
        if (!customerMapper.existsById(id)) {
            throw new RuntimeException("客户不存在");
        }
        return customerMapper.findById(id);
    }

    public PageInfo<Customer> search(String keyword, String gender, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return new PageInfo<>(customerMapper.search(keyword, gender));
    }

    @Transactional
    public Customer save(Customer customer) {
        customer.prePersist();
        customerMapper.insert(customer);
        return customer;
    }

    @Transactional
    public Customer update(Long id, Customer customerDetails) {
        Customer customer = findById(id);
        customer.setName(customerDetails.getName());
        customer.setGender(customerDetails.getGender());
        customer.setPhone(customerDetails.getPhone());
        customerMapper.update(customer);
        return customer;
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        if (saleMapper.existsByCustomerId(id)) {
            throw new RuntimeException("该客户存在关联销售记录，无法删除");
        }
        customerMapper.deleteById(id);
    }
}
