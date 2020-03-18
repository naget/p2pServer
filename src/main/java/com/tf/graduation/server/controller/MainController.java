package com.tf.graduation.server.controller;

import com.tf.graduation.server.Model.ResponseModel;
import com.tf.graduation.server.aspect.StateCheck;
import com.tf.graduation.server.service.UserServiceImpl;
import com.tf.graduation.server.utils.HttpServletUtil;
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
    @RequestMapping("/login")
    @ResponseBody
    public ResponseModel login(HttpServletRequest request){
        Map<String,String> params =HttpServletUtil.getStringParams(request);
        boolean result = userService.login(params.get("nickname"),params.get("password"));
        if (!result)return ResponseModel.fail(400,"登录失败");
        return ResponseModel.success();
    }

    @RequestMapping("/logout")
    @ResponseBody
    public ResponseModel logout(HttpServletRequest request){
        Map<String,String> params = HttpServletUtil.getStringParams(request);
        if (params.get("nickname")==null)return ResponseModel.fail(400,"缺少参数：nickname");
        userService.logout(params.get("nickname"));
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






}
