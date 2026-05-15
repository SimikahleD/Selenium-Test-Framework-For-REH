package com.orangehrm.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerManager {
	
	//This Method returns a Logger instance for the provider class
	public static Logger getLogger(Class <?> clazz) {
		return LogManager.getLogger();
	}
}
