package com.tf.graduation.server.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

/**
 * created by tianfeng on 2020/1/17
 */
@Data
public class NodeRecord {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String deviceName;
    private String deviceNickname;
    private String macAddress;
    private String ipv4;
    private String ipv6;
    private Integer active;
    private String createdAt;
    private String updatedAt;
    private String updatedBy;
    private String createdBy;
}
