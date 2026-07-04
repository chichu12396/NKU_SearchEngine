package com.study.mapper;

import com.study.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    // 登录校验：根据用户名查询用户信息
    @Select("SELECT * FROM User WHERE UserName = #{userName}")
    User findByUserName(@Param("userName") String userName);

    // 注册新用户
    @Insert("INSERT INTO User (UserID, UserName, Phone, PasswordHash, UserType) " +
            "VALUES (#{userId}, #{userName}, #{phone}, #{passwordHash}, #{userType})")
    int insertUser(User user);
    @Select("SELECT * FROM User WHERE Phone = #{phone} AND PasswordHash = #{password}")
    User loginByPhone(@Param("phone") String phone, @Param("password") String password);
}