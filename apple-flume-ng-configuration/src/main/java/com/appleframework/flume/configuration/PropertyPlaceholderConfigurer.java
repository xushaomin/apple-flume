package com.appleframework.flume.configuration;

import java.util.Collection;
import java.util.Properties;

import com.appleframework.config.PropertyConfigurerFactory;
import com.appleframework.config.core.event.ConfigListener;

public class PropertyPlaceholderConfigurer {
	
	private static Collection<ConfigListener> eventListeners;
    
    private static Collection<String> eventListenerClasss;

	private static String eventListenerClass;

	private static ConfigListener eventListener;

	public static void processProperties(Properties props) {
		PropertyConfigurerFactory factory = new PropertyConfigurerFactory(props);
		factory.setEventListener(eventListener);
		factory.setEventListenerClass(eventListenerClass);
		factory.setEventListenerClasss(eventListenerClasss);
		factory.setEventListeners(eventListeners);
		factory.init();
	}

	public static void setEventListeners(Collection<ConfigListener> eventListeners) {
		PropertyPlaceholderConfigurer.eventListeners = eventListeners;
	}

	public static void setEventListenerClasss(Collection<String> eventListenerClasss) {
		PropertyPlaceholderConfigurer.eventListenerClasss = eventListenerClasss;
	}

	public static void setEventListenerClass(String eventListenerClass) {
		PropertyPlaceholderConfigurer.eventListenerClass = eventListenerClass;
	}

	public static void setEventListener(ConfigListener eventListener) {
		PropertyPlaceholderConfigurer.eventListener = eventListener;
	}

}