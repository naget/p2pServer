package com.tf.graduation.server.dao.entity;

import lombok.Data;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

/**
 * created by tianfeng on 2020/1/17
 */
@Data
public class User implements Serializable {
    @Transient
    private Integer id;
    private String nickname;
    private String password;
}
