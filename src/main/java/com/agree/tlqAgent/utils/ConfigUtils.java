package com.agree.tlqAgent.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public enum ConfigUtils {
	INSTANCE;
	
	private static Logger logger=LoggerFactory.getLogger(ConfigUtils.class);
	private static Properties properties = null;
	private static final String CONFIG_PATH="config/tlqconfig.properties";
	
	private  void loadProperty() {
		if (properties == null) {
			load();
		} else {
			String timeStamp = properties.getProperty("timeStamp");
			String now_timeStamp = new File(CONFIG_PATH)
					.lastModified() + "";
			if (!now_timeStamp.equals(timeStamp)) {
				load();
			}
		}
	}
	
	private static void load() {
		properties = new Properties();
		InputStream is = null;
		try {
			logger.debug(new File(CONFIG_PATH).getAbsolutePath());

			is = new FileInputStream(CONFIG_PATH);
			properties.load(is);
			properties.put("timeStamp",
					new File(CONFIG_PATH).lastModified() + "");
		} catch (FileNotFoundException e) {
			logger.error("config file is not existed" + e.getMessage(), e);
		} catch (IOException e) {
			logger.error("load config file error" + e.getMessage(), e);
		}
	}
	
	public String getConfig(String key,String defaultValue) {
		loadProperty();
		if (properties!=null&&properties.getProperty(key)!=null) {
			return properties.getProperty(key);
		}
		return defaultValue;
	}
}
