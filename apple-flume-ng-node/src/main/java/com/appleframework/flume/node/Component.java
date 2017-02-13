package com.appleframework.flume.node;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.appleframework.boot.core.CommandOption;
import com.appleframework.boot.core.Container;
import com.appleframework.boot.core.log4j.Log4jContainer;
import com.appleframework.boot.core.log4j.LoggingConfig;
import com.appleframework.boot.core.monitor.MonitorConfig;
import com.appleframework.boot.core.monitor.MonitorContainer;
import com.appleframework.boot.jmx.JavaContainerManager;
import com.appleframework.boot.utils.SystemPropertiesUtils;
import com.appleframework.config.core.EnvConfigurer;
import com.appleframework.flume.configuration.PropertyPlaceholderConfigurer;

public class Component {

    private static Logger logger = Logger.getLogger(Component.class);

	static void init(String[] args) {
		//处理启动参数
		CommandOption.parser(args);
    	        	
    	MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        
    	final List<Container> containers = new ArrayList<Container>();
        containers.add(new Log4jContainer());
        containers.add(new MonitorContainer());
        
        for (Container container : containers) {
            container.start();
            try {
				
				Hashtable<String, String> properties = new Hashtable<String, String>();

				properties.put(Container.TYPE_KEY, Container.DEFAULT_TYPE);
				properties.put(Container.ID_KEY, container.getType());
				
				ObjectName oname = ObjectName.getInstance("com.appleframework", properties);
				Object mbean = null;
				if(container instanceof Log4jContainer) {
					mbean = new LoggingConfig();
				}
				else if(container instanceof MonitorContainer) {
					mbean = new MonitorConfig();
				}
				else {
					JavaContainerManager containerManager = new JavaContainerManager();
					containerManager.setContainer(container);
					mbean = containerManager;
				}
				
				if (mbs.isRegistered(oname)) {
					mbs.unregisterMBean(oname);
				}
				mbs.registerMBean(mbean, oname);
			} catch (Exception e) {
				logger.error("注册JMX服务出错：" + e.getMessage(), e);
			}
            logger.warn("服务 " + container.getType() + " 启动!");
        }
        
        logger.warn(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + " 所有服务启动成功!");
       
        //初始化配置
        if(null != EnvConfigurer.env) {
        	PropertyPlaceholderConfigurer.processProperties(SystemPropertiesUtils.getProp());
        }

	}
}
