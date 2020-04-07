package com.tf.graduation.server.service;

import com.baomidou.mybatisplus.extension.api.R;
import com.tf.graduation.server.Model.UserInfoOnLine;
import com.tf.graduation.server.dao.entity.NodeRecord;
import com.tf.graduation.server.dao.entity.User;
import com.tf.graduation.server.dao.mapper.NodeRecordMapper;
import com.tf.graduation.server.dao.mapper.UserMapper;
import com.tf.graduation.server.utils.DateUtil;
import com.tf.graduation.server.utils.InformationDigestUtil;
import com.tf.graduation.server.utils.JavaWebToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private NodeRecordMapper nodeRecordMapper;

    public UserInfoOnLine login(String nickname, String password,String macAddress) {
        log.info("start to login  nickname:{},password:{},mac:{}",nickname,password,macAddress);
        User u = userMapper.selectByNickname(nickname);
        if (u == null) return null;
        boolean success =  u.getPassword().equals(InformationDigestUtil.getSHA256(password));
        if (!success)return null;
        if (macAddress==null) return null;
        //查看此设备是否登录过,否则注册
        List<String> deviceNames = nodeRecordMapper.getDeviceNames(macAddress,u.getId());
        if (deviceNames==null||deviceNames.isEmpty()){
            NodeRecord record = new NodeRecord();
            record.setActive(1);
            record.setDeviceName(macAddress);
            record.setMacAddress(macAddress);
            record.setUserId(u.getId());
            record.setCreatedAt(DateUtil.getNowString());
            nodeRecordMapper.insert(record);
        }
        deviceNames = nodeRecordMapper.getDeviceNames(macAddress,u.getId());
        if (deviceNames.size()>1){
            log.warn(macAddress+"对应多个deviceName："+deviceNames.toString());
        }
        Map<String,Object> m = new HashMap<>();
        m.put("userId",u.getId());
        m.put("deviceName",deviceNames.get(0));
        m.put("macAddress",macAddress);
        String token = JavaWebToken.createJavaWebToken(m);
        UserInfoOnLine userInfo = new UserInfoOnLine();
        userInfo.setUserId(u.getId());
        userInfo.setDeviceName(deviceNames.get(0));
        userInfo.setMacAddress(macAddress);
        userInfo.setUserName(nickname);
        userInfo.setToken(token);
        //根据设备mac和用户id生成token，作为key放入redis
        redisService.put(token,userInfo,30L,TimeUnit.MINUTES);
//        redisService.set("USERLOGIN"+nickname,"state",1);
//        redisService.set("USERLOGIN"+nickname,"info",u);
        return userInfo;
    }

    public boolean register(String nickname, String password) {
        if (userMapper.selectByNickname(nickname)!=null)return false;
        String encodeStr = InformationDigestUtil.getSHA256(password);
        log.info("start to register  nickname:{},password:{}",nickname,encodeStr);
        userMapper.create(nickname,encodeStr);
        return true;
    }

    public void logout(String token){
        redisService.expairKey(token);
    }
}
