package com.hxxdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter{
	@Override
    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
////        .allowedOrigins("http://www.aixcoder.com")
//        .allowedOrigins("http://192.168.1.151:8088")
//        .allowedMethods("GET", "POST")
//        .allowCredentials(true).maxAge(1);
    }
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ErrorPageInterceptor());//.addPathPatterns("/action/**", "/mine/**");默认所有
        super.addInterceptors(registry);
    }
}
