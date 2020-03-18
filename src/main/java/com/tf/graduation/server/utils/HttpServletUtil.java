package com.tf.graduation.server.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * created by tianfeng on 2020/1/20
 */
public class HttpServletUtil {
    public static Map<String, String> getStringParams(HttpServletRequest request) {
        Map<String,String> map = new HashMap<>();
        Enumeration<String> parameteNames = request.getParameterNames();

        while (parameteNames.hasMoreElements()) {
            String parameteName = parameteNames.nextElement();
            String value = request.getParameter(parameteName);
//            String[] parameteValues = request.getParameterValues(parameteName);
            map.put(parameteName,value);
        }
        return map;
    }
}
