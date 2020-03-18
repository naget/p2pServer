package com.tf.graduation.server.service;

import com.tf.graduation.server.dao.entity.User;
import com.tf.graduation.server.dao.mapper.UserMapper;
import com.tf.graduation.server.utils.InformationDigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * created by tianfeng on 2020/1/17
 */
@Service
@Slf4j
public class UserServiceImpl {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisService redisService;

    public boolean login(String nickname, String password) {
        log.info("start to login  nickname:{},password:{}",nickname,password);
        User u = userMapper.selectByNickname(nickname);
        if (u == null) return false;
        boolean success =  u.getPassword().equals(InformationDigestUtil.getSHA256(password));
        if (!success)return false;
        redisService.set("USERLOGIN"+nickname,"state",1);
        return success;
    }

    public boolean register(String nickname, String password) {
        if (userMapper.selectByNickname(nickname)!=null)return false;
        String encodeStr = InformationDigestUtil.getSHA256(password);
        log.info("start to register  nickname:{},password:{}",nickname,encodeStr);
        userMapper.create(nickname,encodeStr);
        return true;
    }

    public void logout(String nickname){
        redisService.expairKey("USERLOGIN"+nickname);
    }
}
