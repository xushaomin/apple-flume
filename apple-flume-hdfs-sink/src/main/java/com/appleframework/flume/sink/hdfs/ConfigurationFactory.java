package com.appleframework.flume.sink.hdfs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.flume.Context;
import org.apache.hadoop.conf.Configuration;

public class ConfigurationFactory {

	private static Configuration configuration;
	private static List<String> configPrefixList;

	static {
		configuration = new Configuration();
		configPrefixList = new ArrayList<>();
		configPrefixList.add("dfs.client.failover.proxy.provider");
		configPrefixList.add("dfs.nameservices");
		configPrefixList.add("dfs.namenode.rpc-address");
		configPrefixList.add("dfs.namenode.rpc-address");
		configPrefixList.add("dfs.ha.namenodes");
		configPrefixList.add("fs.defaultFS");
	}

	public static Configuration instance() {
		return configuration;
	}

	public static void set(String name, String value) {
		configuration.set(name, value);
	}

	public static void set(Context context) {
		Map<String, String> parameters = context.getParameters();
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			String key = entry.getKey();
			for (String prefixKey : configPrefixList) {
				if(key.startsWith(prefixKey)) {
					String value = entry.getValue();
					configuration.set(key, value);
				}
			}
		}
	}
}
