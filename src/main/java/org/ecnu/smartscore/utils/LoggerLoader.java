/**
 * 
 */
package org.ecnu.smartscore.utils;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author microdog
 * 
 */
public class LoggerLoader {

	public static void loadLogger(String filename) {
		System.setProperty("WORKDIR", FilePath.getPathInUserDir(""));
		System.setProperty("LOGFILENAME", filename);
		PropertyConfigurator.configure(FilePath
				.getPathInUserDir("log4j.properties"));

		Logger LOGGER = LoggerFactory.getLogger(LoggerLoader.class.getName());
		LOGGER.info("Logger loaded.");
	}

}
