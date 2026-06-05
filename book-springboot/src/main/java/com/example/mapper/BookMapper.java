package com.example.mapper;

import com.example.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图书表（books）数据访问接口。
 * 每个方法对应 BookMapper.xml 中一条 SQL。
 */
@Mapper
public interface BookMapper {

    /** 查询全部图书，按 id 倒序 */
    List<Book> findAll();

    /** 按主键查询单本图书 */
    Book findById(Long id);

    /** 按 ISBN 查图书（ISBN 唯一），用于新增/编辑时查重 */
    Book findByIsbn(String isbn);

    /** 组合条件分页查询：关键字（模糊匹配书名/作者/ISBN）+ 库存状态（全部/低库存/售罄） */
    List<Book> search(@Param("keyword") String keyword, @Param("stockStatus") String stockStatus,
                      @Param("publisher") String publisher);

    /** 新增图书，自动回填自增主键到 book.id */
    int insert(Book book);

    /** 更新图书全部字段 */
    int update(Book book);

    /** 按主键删除 */
    int deleteById(Long id);

    /** 更新库存：正数=入库，负数=出库。SQL 做 stock = stock + #{delta} */
    int updateStock(@Param("id") Long id, @Param("delta") int delta);

    /** 判断主键是否存在 */
    boolean existsById(Long id);
}
