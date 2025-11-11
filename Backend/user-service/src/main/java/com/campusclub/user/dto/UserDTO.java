package com.campusclub.user.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String role;
    private String status;
    private Date lastLogin;
    private Integer loginCount;
    private String avatar;
    private Date createdAt;
    private Date updatedAt;
}