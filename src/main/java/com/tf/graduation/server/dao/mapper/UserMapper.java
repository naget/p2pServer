package com.tf.graduation.server.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tf.graduation.server.dao.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * created by tianfeng on 2020/1/17
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where nickname=#{nickname}")
    public User selectByNickname(String nickname);
    @Insert("insert into user(nickname,password) values(#{param1},#{param2})")
    public void create(String nickname,String password);
}