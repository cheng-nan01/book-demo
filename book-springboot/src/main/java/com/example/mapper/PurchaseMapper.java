package com.example.mapper;

import com.example.entity.Purchase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 进货表（purchases）数据访问接口。
 * 每个方法对应 PurchaseMapper.xml 中一条 SQL。
 */
@Mapper
public interface PurchaseMapper {

    /** 查询全部进货记录，LEFT JOIN books 获取书名，按进货时间倒序 */
    List<Purchase> findAll();

    /** 按主键查单条进货记录 */
    Purchase findById(Long id);

    /** 查询某图书的所有进货记录 */
    List<Purchase> findByBookId(Long bookId);

    /** 新增进货记录，自动回填自增主键 */
    int insert(Purchase purchase);

    /** 删除某图书的所有进货记录 */
    int deleteByBookId(Long bookId);

    /** 某时间段的进货总额 */
    Double getTotalPurchaseAmount(@Param("startDate") String startDate,
                                   @Param("endDate") String endDate);
}
