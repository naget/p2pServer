package com.tf.graduation.server.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * created by tianfeng on 2020/4/2
 */
@Data
public class VersionRecord {
    @TableId(value = "id",type = IdType.AUTO)
    private int id;
    private int userId;
    private String deviceName;//改为device_mac_address or id
    private int version;
    private int state;
    private String updatedTime;
}
