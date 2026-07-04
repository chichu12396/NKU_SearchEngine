package com.study.service.impl;

import com.study.entity.User;
import com.study.mapper.UserMapper;
import com.study.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String userName, String password) {
        User dbUser = userMapper.findByUserName(userName);
        if (dbUser != null && dbUser.getPasswordHash().equals(password)) {
            dbUser.setPasswordHash(null); 
            return dbUser;
        }
        return null;
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        User existUser = userMapper.findByUserName(user.getUserName());
        if (existUser != null) {
            return false; // 用户名已存在
        }
        int rows = userMapper.insertUser(user);
        return rows > 0;
    }

    @Override
    public User loginByPhone(String phone, String password) {
        return userMapper.loginByPhone(phone, password);
    }
}