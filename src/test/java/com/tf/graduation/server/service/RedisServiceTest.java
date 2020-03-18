package com.tf.graduation.server.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * created by tianfeng on 2020/1/20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RedisServiceTest {
    @Autowired
    RedisService redisService;
    @Test
    public void setHash(){
        redisService.set("测试","年龄",11);
    }

    @Test
    public void getValue(){
        Assert.assertTrue(redisService.hasKey("测试"));
        Assert.assertEquals(redisService.get("测试","年龄"),11);

    }
}
