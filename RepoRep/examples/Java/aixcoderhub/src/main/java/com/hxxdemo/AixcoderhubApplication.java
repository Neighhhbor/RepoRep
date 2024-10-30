package com.hxxdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.hxxdemo.datasourse.DynamicDataSourceRegister;

@SpringBootApplication
@MapperScan(basePackages = {"com.hxxdemo.*.dao"})
@EnableScheduling
@EnableAutoConfiguration(exclude = VelocityAutoConfiguration.class)
@Import(DynamicDataSourceRegister.class)
public class AixcoderhubApplication extends SpringBootServletInitializer {

	public static void main(String[] args)  throws Exception{
		SpringApplication.run(AixcoderhubApplication.class, args);
	}
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AixcoderhubApplication.class);
	}
}
