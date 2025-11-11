package com.campusclub.user.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserProfileDTO {
    private Long id;
    private Long userId;
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
}