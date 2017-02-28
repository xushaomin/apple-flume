package com.appleframework.flume.ng.configuration.utils;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.flume.Context;

import com.appleframework.config.core.PropertyConfigurer;
import com.appleframework.config.core.util.ObjectUtils;

public class ContextUtil {

	public static void fullContextValue(Context context) {
		Properties defaultProps = PropertyConfigurer.getProps();
		Enumeration<?> propertyNames = defaultProps.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String propertyName = (String) propertyNames.nextElement();
			String propertyValue = defaultProps.getProperty(propertyName);
			if (ObjectUtils.isNotEmpty(propertyName)) {
				context.put(propertyName, propertyValue);
			}
		}
	}
}
