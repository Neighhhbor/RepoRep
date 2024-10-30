package com.hxxdemo.datasourse;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
 
@Aspect
@Order(-10) // 保证该AOP在@Transactional之前执行
@Component
public class DynamicDataSourceAspect {
	@Before("@annotation(targetDataSource)")
	public void changeDataSource(JoinPoint point, TargetDataSource targetDataSource) throws Throwable {
		String dsId = targetDataSource.value();
		if (!DynamicDataSourceContextHolder.containsDataSource(dsId)) {
			System.err.println("数据源[{}]不存在，使用默认数据源 > {}" + targetDataSource.value() + point.getSignature());
		} else {
			System.out.println("UseDataSource : {} > {}" + targetDataSource.value() + point.getSignature());
			DynamicDataSourceContextHolder.setDataSourceType(targetDataSource.value());
		}
	}
 
	@After("@annotation(targetDataSource)")
	public void restoreDataSource(JoinPoint point, TargetDataSource targetDataSource) {
		System.out.println("RevertDataSource : {} > {}"+targetDataSource.value()+point.getSignature());
		DynamicDataSourceContextHolder.clearDataSourceType();
	}

}
