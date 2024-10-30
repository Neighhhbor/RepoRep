package com.hxxdemo.datasourse;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
 
public class DynamicDataSource extends AbstractRoutingDataSource {
 
	@Override
	protected Object determineCurrentLookupKey() {
		return DynamicDataSourceContextHolder.getDataSourceType();
	}
}
