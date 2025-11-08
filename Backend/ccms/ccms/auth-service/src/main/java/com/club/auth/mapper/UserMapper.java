package com.club.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.club.common.entity.User;
import org.apache.ibatis.annotations.Mapper; // 导入@Mapper注解

@Mapper // 添加该注解，标识为MyBatis的Mapper接口
public interface UserMapper extends BaseMapper<User> {
    // 继承BaseMapper后，已包含selectById、selectOne等基础方法
    // 如需复杂查询，可在此添加方法并在XML中实现
}