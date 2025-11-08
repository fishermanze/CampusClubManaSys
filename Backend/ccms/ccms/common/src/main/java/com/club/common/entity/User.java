// common/src/main/java/com/club/common/entity/User.java
package com.club.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户实体类（所有服务共用的基础用户信息）
 */
@Data
@TableName("user") // 对应数据库表名
public class User {
    @TableId(type = IdType.AUTO) // 自增主键
    private Long id;

    @TableField("username")
    private String username; // 登录账号（唯一）

    @TableField("password")
    private String password; // 加密存储的密码

    @TableField("name")
    private String name; // 真实姓名

    @TableField("college")
    private String college; // 所属学院

    @TableField("role")
    private String role; // 角色：admin（Ⅰ级）、clubPresident（Ⅱ级）、member（Ⅲ级）

    @TableField("hobby")
    private String hobby; // 爱好（用逗号分隔，如"篮球,编程,音乐"）

    // 注意：不要添加服务私有字段，仅保留所有服务都需要的基础信息
}