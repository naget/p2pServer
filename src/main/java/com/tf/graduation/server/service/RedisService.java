package com.tf.graduation.server.service;

import com.tf.graduation.server.Model.UserInfoOnLine;
import com.tf.graduation.server.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

/**
 * created by tianfeng on 2020/1/18
 */
@Service
public class RedisService {
    @Autowired
    RedisTemplate redisTemplate;
    public boolean set(String k,String hk,Object hv){
        redisTemplate.opsForHash().put(k,hk,hv);
        return true;
    }
    public Object get(String k,String hk){
        Object result =  redisTemplate.opsForHash().get(k,hk);
        return result;
    }

    public boolean putLongKey(String key,Object value){
        redisTemplate.opsForValue().set(key,value);
        return true;
    }

    public boolean put(String key,Object value,Long timeout,TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key,value,timeout,timeUnit);
        return true;
    }

    public Object get(String key){
        Object result = redisTemplate.opsForValue().get(key);
        return result;
    }

    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    public boolean expairKey(String key){
        return redisTemplate.expire(key,0L,TimeUnit.MICROSECONDS);
    }

    public UserInfoOnLine getUserInfo(String token){
        return (UserInfoOnLine) redisTemplate.opsForValue().get(token);
    }
}
