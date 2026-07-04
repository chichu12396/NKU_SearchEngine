package com.study.controller;

import com.study.entity.Result;
import com.study.entity.User;
import com.study.service.UserService;
import com.study.utils.JwtUtil; // 确保导包正确

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 【修改点 1】像引入 Service 一样，把 JwtUtil 注入进来
    @Autowired
    private JwtUtil jwtUtil;

    // 登录接口
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginUser) {

        User user = userService.loginByPhone(loginUser.getPhone(), loginUser.getPasswordHash());
        
        if (user != null) {
  
            String token = jwtUtil.createToken(user.getUserId());
            Map<String, Object> loginResult = new HashMap<>();
            loginResult.put("token", token);
            loginResult.put("userName", user.getUserName()); 
            loginResult.put("userId", user.getUserId());
            // 4. 打包返回给前端
            return Result.success(loginResult); 
        } else {
            return Result.error("手机号或密码错误！");
        }
    }

    // 注册接口 (保持不变)
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        if (user.getUserType() == null) {
            user.setUserType("Student");
        }
        
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功！欢迎加入进行检索。");
        } else {
            return Result.error("注册失败，用户名可能已被占用！");
        }
    }
}