package com.example.service;

import com.example.entity.Salespeople;
import com.example.mapper.SalespeopleMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalespeopleService {

    private final SalespeopleMapper salespeopleMapper;

    public PageInfo<Salespeople> search(String keyword, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return new PageInfo<>(salespeopleMapper.search(keyword));
    }

    @Transactional
    public Salespeople save(Salespeople salespeople) {
        if (salespeopleMapper.existsByNameExcludeId(salespeople.getName(), null)) {
            throw new RuntimeException("销售员姓名已存在");
        }
        salespeopleMapper.insert(salespeople);
        return salespeople;
    }

    @Transactional
    public Salespeople update(Long id, Salespeople sp) {
        if (salespeopleMapper.existsByNameExcludeId(sp.getName(), id)) {
            throw new RuntimeException("销售员姓名已存在");
        }
        sp.setId(id);
        salespeopleMapper.update(sp);
        return sp;
    }

    @Transactional
    public void delete(Long id) {
        salespeopleMapper.deleteById(id);
    }
}
