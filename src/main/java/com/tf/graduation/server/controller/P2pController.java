package com.tf.graduation.server.controller;

import com.tf.graduation.server.Model.ResponseModel;
import com.tf.graduation.server.p2pService.EchoServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * created by tianfeng on 2020/2/5
 */
@Controller
@RequestMapping("/p2p")
public class P2pController {
    @Autowired
    EchoServer echoServer;
    @RequestMapping("/start")
    @ResponseBody
    public ResponseModel start(){
        return echoServer.start();
    }
}
