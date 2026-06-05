package com.example.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表（users）数据访问接口。
 * 用于登录/注册认证流程。
 */
@Mapper
public interface UserMapper {

    /** 按用户名查用户（登录时校验密码） */
    User findByUsername(String username);

    /** 判断用户名是否已存在（注册时查重） */
    boolean existsByUsername(String username);

    /** 注册新用户，自动回填自增主键 */
    int insert(User user);

    /** 按主键查询 */
    User findById(Long id);
}
