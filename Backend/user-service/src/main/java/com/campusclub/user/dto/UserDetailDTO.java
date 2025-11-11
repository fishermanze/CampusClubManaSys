package com.campusclub.user.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDetailDTO {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String role;
    private String status;
    private Date lastLogin;
    private Integer loginCount;
    private String studentId;
    private String major;
    private String grade;
    private String className;
    private String gender;
    private Date birthDate;
    private String hobbies;
    private String bio;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private String socialMedia;
    private String preferredClubTypes;
    private String avatar;
    private Date createdAt;
    private Date updatedAt;
    private int followingCount;
    private int followerCount;
    private boolean isFollowing;
    private boolean isMutual;
}