package com.tf.graduation.server.service;

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

    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    public boolean expairKey(String key){
        return redisTemplate.expire(key,0L,TimeUnit.MICROSECONDS);
    }
}
