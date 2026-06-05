package com.example.mapper;

import com.example.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户表（customers）数据访问接口。
 * 每个方法对应 CustomerMapper.xml 中一条 SQL。
 */
@Mapper
public interface CustomerMapper {

    /** 查询全部客户，按 id 倒序 */
    List<Customer> findAll();

    /** 按主键查询单个客户 */
    Customer findById(Long id);

    /** 按手机号精确匹配 */
    Customer findByPhone(String phone);

    /** 组合条件分页查询：关键字（模糊匹配姓名/手机号）+ 性别筛选 */
    List<Customer> search(@Param("keyword") String keyword, @Param("gender") String gender);

    /** 新增客户，自动回填自增主键到 customer.id */
    int insert(Customer customer);

    /** 更新客户全部字段 */
    int update(Customer customer);

    /** 按主键删除 */
    int deleteById(Long id);

    /** 判断主键是否存在 */
    boolean existsById(Long id);

    /** 统计客户总数 */
    long count();

    /** 判断手机号是否已被其他客户使用（排除指定 id，用于编辑时查重） */
    boolean existsByPhone(@Param("phone") String phone, @Param("excludeId") Long excludeId);
}
