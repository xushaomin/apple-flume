package com.appleframework.flume.configuration;

import java.util.Properties;

import com.appleframework.boot.utils.SystemPropertiesUtils;

/**
 * Unit test for PropertyPlaceholderConfigurerTest.
 */
public class PropertyPlaceholderConfigurerTest {
	
	public static void main(String[] args) {
		Properties props = SystemPropertiesUtils.getProp();
		PropertyPlaceholderConfigurer.processProperties(props);
	}
}
