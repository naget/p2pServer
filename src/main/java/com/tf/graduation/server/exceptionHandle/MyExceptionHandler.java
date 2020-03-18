package com.tf.graduation.server.exceptionHandle;

import com.tf.graduation.server.Model.ResponseModel;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * created by tianfeng on 2020/1/21
 */
@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value = IllegalStateException.class)
    @ResponseBody
    public ResponseModel stateExceptionHandler(Exception e) {
        return ResponseModel.fail(400,e.getMessage());
    }

}
