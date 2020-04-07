package com.tf.graduation.server.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getNowString(){
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s8 = formatter.format(currentTime);
        return s8;
    }
}