package com.study.service;

import com.study.entity.User;

public interface UserService {
    // 登录：成功返回用户信息，失败返回null
    User login(String userName, String password);

    // 注册：返回是否成功
    boolean register(User user);
	User loginByPhone(String phone, String passwordHash);
}