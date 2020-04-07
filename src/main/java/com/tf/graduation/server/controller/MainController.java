package com.tf.graduation.server.controller;

import com.tf.graduation.server.Model.ResponseModel;
import com.tf.graduation.server.Model.UserInfoOnLine;
import com.tf.graduation.server.aspect.StateCheck;
import com.tf.graduation.server.dao.entity.User;
import com.tf.graduation.server.dao.entity.VersionRecord;
import com.tf.graduation.server.enums.VersionRecordStateEnum;
import com.tf.graduation.server.service.RedisService;
import com.tf.graduation.server.service.UserServiceImpl;
import com.tf.graduation.server.service.VersionRecordServiceImpl;
import com.tf.graduation.server.utils.HttpServletUtil;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * created by tianfeng on 2020/1/20
 */
@Controller
public class MainController {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    VersionRecordServiceImpl versionRecordService;
    @Autowired
    RedisService redisService;

    @RequestMapping("/login")
    @ResponseBody
    public ResponseModel login(HttpServletRequest request){
        Map<String,String> params =HttpServletUtil.getStringParams(request);
        UserInfoOnLine result = userService.login(params.get("nickname"),params.get("password"),params.get("macAddress"));
        if (null==result)return ResponseModel.fail(400,"登录失败");
        return ResponseModel.success(result);
    }

    @RequestMapping("/logout")
    @ResponseBody
    public ResponseModel logout(HttpServletRequest request){
        Map<String,String> params = HttpServletUtil.getStringParams(request);
        if (params.get("token")==null)return ResponseModel.fail(400,"缺少参数：token");
        userService.logout(params.get("token"));
        return ResponseModel.success();
    }

    @RequestMapping("/register")
    @ResponseBody
    public ResponseModel register(HttpServletRequest request){
        Map<String,String> params = HttpServletUtil.getStringParams(request);
        if (params.get("nickname")==null||params.get("password")==null)return ResponseModel.fail(401,"nickname和password缺一不可");
        userService.register(params.get("nickname"),params.get("password"));
        return ResponseModel.success();
    }

    @RequestMapping("/syn")
    @ResponseBody
    @StateCheck
    public ResponseModel syn(HttpServletRequest request){
        return ResponseModel.success();
    }


    @RequestMapping("/getLatestVersion")
    @ResponseBody
    @StateCheck
    public ResponseModel latestVersion(HttpServletRequest request){
        Map<String,String> params = HttpServletUtil.getStringParams(request);
        UserInfoOnLine user = redisService.getUserInfo(params.get("token"));
        VersionRecord record = versionRecordService.getLatestUpdated(user.getUserId());
        return ResponseModel.success(record);
    }

    @RequestMapping("/applyVersion")
    @ResponseBody
    @StateCheck
    public ResponseModel applyVersion(HttpServletRequest request){
        Map<String,String> params = HttpServletUtil.getStringParams(request);
        UserInfoOnLine user = redisService.getUserInfo(params.get("token"));
        VersionRecord record = versionRecordService.getLatest(user.getUserId());
        if (record.getState()==VersionRecordStateEnum.UPDATING.getCode()&&record.getDeviceName().equals(user.getDeviceName())){
            return ResponseModel.success(record);
        }
        if (record==null||record.getState()==VersionRecordStateEnum.UPDATED.getCode()){
            VersionRecord record1= versionRecordService.apply(user.getUserId(),user);
            if (record1!=null){
                return ResponseModel.success(record1);
            }
        }
        return ResponseModel.fail(10000,"其他结点正在更新，请稍后！");
    }

    @RequestMapping("/updateRecordState")
    @ResponseBody
    @StateCheck
    public ResponseModel updateRecordState(HttpServletRequest request){
        Map<String,String> params = HttpServletUtil.getStringParams(request);
        UserInfoOnLine user = redisService.getUserInfo(params.get("token"));
        if (versionRecordService.updateState(user.getUserId(),user)){
            return ResponseModel.success("版本更新成功");
        }
        return ResponseModel.fail(10001,"版本更新失败");
    }





}
