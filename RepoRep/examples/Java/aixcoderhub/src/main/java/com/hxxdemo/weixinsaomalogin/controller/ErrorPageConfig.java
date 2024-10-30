package com.hxxdemo.weixinsaomalogin.controller;

import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * @描叙：  错误页面配置
 */
@Configuration
public class ErrorPageConfig {

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        return (container -> {
        	/**
        	 * 本项目配置错误页面
        	 */
//            ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/error/401");//401
//            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/error/404");//404
//            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500");//500
//
//            container.addErrorPages(error401Page, error404Page, error500Page);
        });
    }
}
