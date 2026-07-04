package com.study.entity;
import lombok.Data;
@Data
public class User {
    private Integer userId;
    private String userName;
    private String phone;
    private String passwordHash;
    private String userType;
}