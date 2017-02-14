package com.appleframework.flume.configuration;

import java.util.Properties;

import com.appleframework.config.PropertyConfigurerFactory;

public class PropertyPlaceholderConfigurer {

	public static void processProperties(Properties props) {
		PropertyConfigurerFactory factory = new PropertyConfigurerFactory(props);
		factory.init();
	}

}