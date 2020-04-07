package com.tf.graduation.server.Model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * created by tianfeng on 2020/4/3
 */
@Data
@ToString
public class UserInfoOnLine implements Serializable {
    private int userId;
    private String userName;
    private String deviceName;
    private String macAddress;
    private String token;
}
