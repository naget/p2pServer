package com.tf.graduation.server.aspect;

import com.alibaba.fastjson.JSON;
import com.tf.graduation.server.service.RedisService;
import com.tf.graduation.server.utils.HttpServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * created by tianfeng on 2020/1/20
 */
@Aspect
@Component
@Slf4j
public class StateAOP {
    @Autowired
    RedisService redisService;

    /**
     * 定义切入点，切入点为com.tf.graduation.server.controller中的所有函数
     * 通过@Pointcut注解声明频繁使用的切点表达式
     */
    @Pointcut("execution(public * com.tf.graduation.server.controller.MainController.*(..)))")
    public void StateAspect() {

    }

    /**
     * @description 在连接点执行之前执行的通知
     */
    @Before("StateAspect()")
    public void doBefore(JoinPoint pjp) throws NoSuchMethodException {
        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = pjp.getSignature().getName();
        Object[] args = pjp.getArgs();


        Class<?> classTarget = pjp.getTarget().getClass();
        Class<?>[] par = ((MethodSignature) pjp.getSignature()).getParameterTypes();
        Method objMethod = classTarget.getMethod(methodName, par);
        StateCheck stateCheck = objMethod.getAnnotation(StateCheck.class);
        if (stateCheck != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof HttpServletRequest) {
                    Map<String, String> params = HttpServletUtil.getStringParams((HttpServletRequest) args[i]);
                    log.info("checkState get params:{}", JSON.toJSONString(params));
                    Integer result = (Integer) redisService.get("USERLOGIN"+params.get("nickname"), "state");
                    if (result!= null && result.equals(1)) {
                        log.info("返回结点数据");
                    } else {
                        throw new IllegalStateException("用户未登录");
                    }
                }
            }
        }
    }
//
//    /**
//     * @description  在连接点执行之后执行的通知（返回通知和异常通知的异常）
//     */
//    @After("StateAspect()")
//    public void doAfterGame(){
//        System.out.println("经纪人为球星表现疯狂鼓掌！");
//    }
//
//    /**
//     * @description  在连接点执行之后执行的通知（返回通知）
//     */
//    @AfterReturning("StateAspect()")
//    public void doAfterReturningGame(){
//        System.out.println("返回通知：经纪人为球星表现疯狂鼓掌！");
//    }
//
//    /**
//     * @description  在连接点执行之后执行的通知（异常通知）
//     */
//    @AfterThrowing("StateAspect()")
//    public void doAfterThrowingGame(){
//        System.out.println("异常通知：球迷要求退票！");
//    }
}
