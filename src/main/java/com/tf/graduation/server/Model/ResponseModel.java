package com.tf.graduation.server.Model;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.ConstructorArgs;

/**
 * created by tianfeng on 2020/1/21
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseModel {
    private int code;
    private String message;
    private Object data;
    public static ResponseModel success(){
        return ResponseModel.builder().code(200).message("success").build();
    }

    public static ResponseModel fail(int code,String message){
        return ResponseModel.builder().code(code).message(message).build();
    }
}
