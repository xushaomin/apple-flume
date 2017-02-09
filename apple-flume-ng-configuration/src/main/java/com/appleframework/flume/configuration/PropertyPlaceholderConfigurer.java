package com.appleframework.flume.configuration;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;

import com.appleframework.config.core.Constants;
import com.appleframework.config.core.EnvConfigurer;
import com.appleframework.config.core.PropertyConfigurer;
import com.appleframework.config.core.util.ObjectUtils;
import com.appleframework.config.core.util.StringUtils;
import com.taobao.diamond.manager.DiamondManager;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;

public class PropertyPlaceholderConfigurer {

	private static Logger logger = Logger.getLogger(PropertyPlaceholderConfigurer.class);

	private static String KEY_DEPLOY_GROUP = "deploy.group";
	private static String KEY_DEPLOY_DATAID = "deploy.dataId";

	private static String eventListenerClass;

	private static ManagerListener eventListener;

	private static boolean loadRemote = true;

	public static boolean isLoadRemote() {
		return loadRemote;
	}

	public static void setLoadRemote(boolean loadRemoteB) {
		loadRemote = loadRemoteB;
	}

	public static void setEventListenerClass(String eventListenerClasss) {
		eventListenerClass = eventListenerClasss;
	}

	public void setEventListener(ManagerListener eventListenerr) {
		eventListener = eventListenerr;
	}

	public static void processProperties(Properties props) {

		// 获取启动启动-D参数
		Properties systemProps = System.getProperties();
		Enumeration<?> systemEnum = systemProps.keys();
		while (systemEnum.hasMoreElements()) {
			String systemKey = systemEnum.nextElement().toString();
			if (!Constants.SET_SYSTEM_PROPERTIES.contains(systemKey)) {
				String systemValue = systemProps.getProperty(systemKey);
				props.setProperty(systemKey, systemValue);
			}
		}

		if (!isLoadRemote()) {
			PropertyConfigurer.load(props);
			return;
		}

		String group = props.getProperty(KEY_DEPLOY_GROUP);
		String dataId = props.getProperty(KEY_DEPLOY_DATAID);

		logger.warn("配置项：group=" + group);
		logger.warn("配置项：dataId=" + dataId);

		if (!StringUtils.isEmpty(group) && !StringUtils.isEmpty(dataId)) {
			String env = getDeployEnv(props);
			if (!StringUtils.isEmpty(env)) {
				dataId += "-" + env;
				logger.warn("配置项：env=" + env);
			}

			List<ManagerListener> managerListeners = new ArrayList<ManagerListener>();

			ManagerListener springMamagerListener = new ManagerListener() {

				public Executor getExecutor() {
					return null;
				}

				public void receiveConfigInfo(String configInfo) {
					// 客户端处理数据的逻辑
					logger.warn("已改动的配置：\n" + configInfo);
					StringReader reader = new StringReader(configInfo);
					try {
						PropertyConfigurer.props.load(reader);
					} catch (IOException e) {
						logger.error(e);
					}
				}
			};
			managerListeners.add(springMamagerListener);

			// 定义事件源
			try {
				if (!StringUtils.isNullOrEmpty(eventListenerClass)) {
					// 定义并向事件源中注册事件监听器
					Class<?> clazz = Class.forName(eventListenerClass);
					ManagerListener managerListener = (ManagerListener) clazz.newInstance();
					managerListeners.add(managerListener);
				}
			} catch (Exception e) {
				logger.error(e);
			}

			try {
				if (ObjectUtils.isNotEmpty(eventListener)) {
					managerListeners.add(eventListener);
				}
			} catch (Exception e) {
				logger.error(e);
			}

			DiamondManager manager = new DefaultDiamondManager(group, dataId, managerListeners);

			try {
				String configInfo = manager.getAvailableConfigureInfomation(30000);
				logger.warn("配置项内容: \n" + configInfo);
				if (!StringUtils.isEmpty(configInfo)) {
					StringReader reader = new StringReader(configInfo);
					props.load(reader);
					PropertyConfigurer.load(props);
				} else {
					logger.error("在配置管理中心找不到配置信息");
				}
			} catch (IOException e) {
				logger.error(e);
			}
		} else {
			PropertyConfigurer.load(props);
		}

		// 讲-D开头的的配置设置到系统变量
		Iterator<Entry<Object, Object>> it = props.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (key.toString().startsWith("-D")) {
				String systemKey = key.toString().trim().substring(2);
				String systemValue = value.toString().trim();
				setSystemProperty(systemKey, systemValue);
				logger.warn(key.toString() + "=" + systemValue);
			}
		}

	}

	private static String getDeployEnv(Properties props) {
		String env = getSystemProperty(Constants.KEY_DEPLOY_ENV);
		if (StringUtils.isEmpty(env)) {
			env = getSystemProperty(Constants.KEY_ENV);
			if (StringUtils.isEmpty(env)) {
				env = EnvConfigurer.env;
				if (StringUtils.isEmpty(env)) {
					env = props.getProperty(Constants.KEY_DEPLOY_ENV);
				}
			}
		}
		return env;
	}

	private static void setSystemProperty(String key, String value) {
		try {
			System.setProperty(key, value);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private static String getSystemProperty(String key) {
		try {
			return System.getProperty(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

}