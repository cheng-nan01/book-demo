package com.example.mapper;

import com.example.entity.Salespeople;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SalespeopleMapper {
    List<Salespeople> search(@Param("keyword") String keyword);
    Salespeople findById(Long id);
    int insert(Salespeople salespeople);
    int update(Salespeople salespeople);
    int deleteById(Long id);
    boolean existsByNameExcludeId(@Param("name") String name, @Param("excludeId") Long excludeId);
}
