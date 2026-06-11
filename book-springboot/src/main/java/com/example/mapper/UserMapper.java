package com.example.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    /** 修改密码 */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /** 修改用户名 */
    int updateUsername(@Param("id") Long id, @Param("username") String username);

    /** 判断用户名是否被他人占用（改名时检查） */
    boolean existsByUsernameExcludeId(@Param("username") String username, @Param("excludeId") Long excludeId);
}
