package com.tf.graduation.server.p2pService;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
 
/**
 * 
 * @author shilei
 *
 * @time 2019年6月19日 下午5:17:50
 *
 * @desc Netty中注入 Spring Autowired
 */
@Component
public class ToolNettySpirngAutowired implements ApplicationContextAware {
 
	private static ApplicationContext applicationContext;
 
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (ToolNettySpirngAutowired.applicationContext == null) {
			ToolNettySpirngAutowired.applicationContext = applicationContext;
		}
	}
 
	// 获取applicationContext
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
 
	// 通过name获取 Bean.
	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}
 
	// 通过class获取Bean.
	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}
 
	// 通过name,以及Clazz返回指定的Bean
	public static <T> T getBean(String name, Class<T> clazz) {
		return getApplicationContext().getBean(name, clazz);
	}
 
}